package pl.awaitq.empikdc.coupon;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
class CreateCouponUseCase {
    private final CouponRepository couponRepository;
    private final Logger logger = LoggerFactory.getLogger(CreateCouponUseCase.class);

    public CreateCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CreateCouponResult createCoupon(CreateCouponCommand command) {
        CouponCode couponCode = new CouponCode(command.code());
        // TODO: validate country code and handle exception
        ISOCountry country = new ISOCountry(command.country());

        // Not necessary when we have a 'unique' on the database.
        if(couponRepository.existsByCode(couponCode)){
            throw new CouponAlreadyExistsDomainException(couponCode);
        }

        Coupon coupon = Coupon.create(couponCode, country, command.maxUsage());
        try{
            couponRepository.saveAndFlush(coupon);
        } catch(DataIntegrityViolationException e){
            throw new CouponAlreadyExistsDomainException(couponCode);
        }

        return CreateCouponResult.of(coupon);
    }
}
