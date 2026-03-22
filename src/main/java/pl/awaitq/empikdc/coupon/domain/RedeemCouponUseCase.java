package pl.awaitq.empikdc.coupon.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import pl.awaitq.empikdc.coupon.domain.dto.*;
import pl.awaitq.empikdc.coupon.domain.port.CouponRedemptionRepository;
import pl.awaitq.empikdc.coupon.domain.port.CouponRepository;
import pl.awaitq.empikdc.coupon.domain.port.GeoIpService;
import pl.awaitq.empikdc.coupon.domain.port.GeoIpServiceException;


@Service
public class RedeemCouponUseCase {
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final GeoIpService geoIpService;
    private final TransactionTemplate transactionTemplate;
    private final Logger logger = LoggerFactory.getLogger(RedeemCouponUseCase.class);

    public RedeemCouponUseCase(CouponRepository couponRepository, CouponRedemptionRepository couponRedemptionRepository, GeoIpService geoIpService, TransactionTemplate transactionTemplate) {
        this.couponRepository = couponRepository;
        this.couponRedemptionRepository = couponRedemptionRepository;
        this.geoIpService = geoIpService;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional
    public RedeemCouponResult redeemCoupon(RedeemCouponCommand command) {
        // Optimistic locking - Ok for small traffic and a small number of collisions.
        //                      In case of many collisions, we get a lot of wasted work (retry of ObjectOptimisticLockingFailureException).

        // Pessimistic locking - Ok for small traffic. Can be a bottleneck for large traffic,
        //                       but lock prevents from wasted work (we might just time out some requests).

        // Atomic update - Ok for small traffic, Ok for large traffic, but requires domain logic in the DB and queries.
        //

        // For this implementation I have selected Pessimistic locking.
        // Based on business requirements and provided information, this might be an optimal solution.

        // HTTP call outside the database transaction.
        ISOCountry isoCountry;
        try {
            isoCountry = geoIpService.getCountryFromIp(command.ip());
        } catch (GeoIpServiceException e) {
            // We have to make a decision what we want to do when we are unable to fetch the result.
            // For now to be in line with business requirements, we should reject such a request.
            // In practice, it is worth logging this error and proceeding with an
            // "unknown" country and let the user activate the coupon.
            // Our choice of the external service should not punish the user unless this is a critical business requirement.
            logger.error("Failed to resolve country from IP. Cannot redeem coupon.", e);
            throw new RuntimeException("Failed to resolve country from IP", e);
        }
        return redeemCoupon(command, isoCountry);
    }

    private RedeemCouponResult redeemCoupon(RedeemCouponCommand command, ISOCountry isoCountry) {
        return transactionTemplate.execute(status -> {
            CouponCode couponCode = new CouponCode(command.couponCode());
            Coupon coupon = couponRepository
                    .findForUpdateByCode(couponCode)
                    .orElseThrow(() -> {
                        logger.atInfo()
                                .addKeyValue("couponCode", couponCode)
                                .addKeyValue("userId", command.userId())
                                .log("Coupon code {} not found", couponCode);
                        return new CouponNotFoundDomainException("Coupon code %s not found".formatted(couponCode));
                    });

            if (!coupon.countryAllowed(isoCountry)) {
                logger.atInfo()
                        .addKeyValue("couponCode", couponCode)
                        .addKeyValue("userId", command.userId())
                        .log("Coupon code {} is invalid for country {}", couponCode, isoCountry);
                throw new CouponInvalidCountryDomainException(isoCountry);
            }

            if (coupon.isMaxUsageReached()) {
                logger.atInfo()
                        .addKeyValue("couponCode", couponCode)
                        .addKeyValue("userId", command.userId())
                        .log("Limit reached for coupon code {}.", couponCode);
                throw new CouponLimitReachedDomainException();
            }

            if (couponRedemptionRepository.existsByCouponIdAndUserId(coupon.getId(), command.userId())) {
                logger.atInfo()
                        .addKeyValue("couponCode", couponCode)
                        .addKeyValue("userId", command.userId())
                        .log("Coupon code {} has already been used by user {}.", couponCode, command.userId());
                throw new CouponHasAlreadyBeenUsed();
            }
            coupon.redeem();
            couponRepository.save(coupon);

            CouponRedemption couponRedemption = CouponRedemption.create(
                    coupon.getId(),
                    command.userId(),
                    command.ip(),
                    isoCountry);
            try {
                couponRedemptionRepository.save(couponRedemption);
            } catch (DataIntegrityViolationException e) {
                logger.atInfo()
                        .addKeyValue("couponCode", couponCode)
                        .addKeyValue("userId", command.userId())
                        .log("Coupon code {} has already been used by user {}.", couponCode, command.userId(), e);
                throw new CouponHasAlreadyBeenUsed();
            }
            return new RedeemCouponResult(couponRedemption.getId(), couponRedemption.getRedeemedAt());
        });
    }
}


//        ATOMIC version snippet.
//        int updated = couponRepository.incrementRedemptions(code, country);
//        if(updated == 0){
//            Coupon c = couponRepository
//                    .findByCode(code)
//                    .orElseThrow(() -> new RuntimeException("Coupon not found"));
//            if (!c.getCountry().equals(country)) throw new RuntimeException("Invalid country");
//            if (c.getCurrentUsage() >= c.getMaxUsage()) throw new RuntimeException("Limit reached");
//            throw new RuntimeException("Unknown error");
//        }
//
//        try {
//            UUID couponId = couponRepository.findByCode(code).orElseThrow().getId();
//            couponRedemptionRepository.save(CouponRedemption.create(couponId, command.userId(), command.ip(), country));
//
//        } catch (DataIntegrityViolationException e) {
//            throw new RuntimeException("Already used", e);
//        }
//        return new RedeemCouponResult();
