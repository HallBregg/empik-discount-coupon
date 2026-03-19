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
        ISOCountry country = new ISOCountry(command.country());

        if(couponRepository.existsByCode(couponCode)){
            throw new RuntimeException("Coupon already exists");
        }

        Coupon coupon = Coupon.create(couponCode, country, command.maxUsage());
        try{
            couponRepository.save(coupon);
        } catch(DataIntegrityViolationException e){
            throw new RuntimeException("Coupon already exists");
        }

        return CreateCouponResult.of(coupon);
    }
}
