package pl.awaitq.empikdc.coupon;

public class CouponLimitReachedDomainException extends CouponDomainException {
    private static final CouponErrorCode errorCode = CouponErrorCode.LIMIT_REACHED;

    public CouponLimitReachedDomainException() {
        super(errorCode, "Usage limit for this coupon has been reached.");
    }
}
