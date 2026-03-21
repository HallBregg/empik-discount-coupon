package pl.awaitq.empikdc.coupon;

public class CouponAlreadyExistsDomainException extends CouponDomainException {
    private static final CouponErrorCode errorCode = CouponErrorCode.COUPON_ALREADY_EXISTS;

    public CouponAlreadyExistsDomainException(CouponCode code) {
        super(errorCode, "Coupon for code %s already exists".formatted(code));
    }
}
