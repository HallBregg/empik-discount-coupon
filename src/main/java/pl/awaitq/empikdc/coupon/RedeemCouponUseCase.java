package pl.awaitq.empikdc.coupon;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
class RedeemCouponUseCase {
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final GeoIpService geoIpService;

    public RedeemCouponUseCase(CouponRepository couponRepository, CouponRedemptionRepository couponRedemptionRepository, GeoIpService geoIpService) {
        this.couponRepository = couponRepository;
        this.couponRedemptionRepository = couponRedemptionRepository;
        this.geoIpService = geoIpService;
    }

    @Transactional
    public RedeemCouponResult redeemCoupon(RedeemCouponCommand command) {
        // Optimistic locking at this version.
        // For now, I assume that those discount coupons are not activated in spikes.
        // If they are, then we should think about DB atomic update. That would require moving domain logic into the DB
        // and queries. So for now, optimistic locking is a good starting point. If a business provides
        // additional information about specific of the coupons and expected traffic, then we could reimplement into atomic update.
        // Also, we could use Pessimistic locking if we require full control over activations.
        // The locking mechanism depends on the traffic we expect and the type of coupons.

        CouponCode couponCode = new CouponCode(command.couponCode());
        ISOCountry isoCountry;
        try {
            isoCountry = geoIpService.getCountryFromIp(command.ip());
        } catch (GeoIpServiceException e) {
            // We have to make a decision what we want to do when we are unable to fetch the result.
            // For now to be in line with business requirements, we should reject such a request.
            // In practice, it is worth logging this error and proceeding with an
            // "unknown" country and let the user activate the coupon.
            // Our choice of the external service should not punish the user unless this is a critical business requirement.
            throw new RuntimeException("Failed to resolve country from IP", e);
        }

        Coupon coupon = couponRepository
                .findByCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!coupon.countryAllowed(isoCountry)) {
            throw new RuntimeException("Invalid country");
        }

        if (coupon.isMaxUsageReached()) {
            throw new RuntimeException("Limit reached");
        }

        if (couponRedemptionRepository.existsByCouponIdAndUserId(coupon.getId(), command.userId())) {
            throw new RuntimeException("Already used");
        }
        coupon.redeemed();

        CouponRedemption couponRedemption = CouponRedemption.create(
                coupon.getId(),
                command.userId(),
                command.ip(),
                isoCountry);

        try {
            couponRepository.save(coupon);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("Race condition.");
        }

        try {
            couponRedemptionRepository.save(couponRedemption);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Already used");
        }
        return new RedeemCouponResult();
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
