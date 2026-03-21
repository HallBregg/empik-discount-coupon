package pl.awaitq.empikdc.coupon;

public class CouponNotFoundDomainException extends CouponDomainException{
    private static final CouponErrorCode code = CouponErrorCode.COUPON_NOT_FOUND;

    public CouponNotFoundDomainException(String message) {
        super(code, message);
    }
}
