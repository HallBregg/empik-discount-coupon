package pl.awaitq.empikdc.coupon;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Service;


@Service
class CreateCouponUseCase {
    private final CouponRepository couponRepository;

    public CreateCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    // TODO: Validate command.
    public void createCoupon(CreateCouponCommand command) {
    }

}
