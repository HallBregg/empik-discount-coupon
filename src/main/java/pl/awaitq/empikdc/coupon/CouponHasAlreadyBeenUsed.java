package pl.awaitq.empikdc.coupon;

public class CouponHasAlreadyBeenUsed extends CouponDomainException {
    private static final CouponErrorCode errorCode = CouponErrorCode.ALREADY_USED;

    public CouponHasAlreadyBeenUsed() {
        super(errorCode, "Coupon has already been used.");
    }
}
