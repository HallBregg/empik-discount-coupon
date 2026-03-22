package pl.awaitq.empikdc.coupon.domain.dto;

import pl.awaitq.empikdc.coupon.domain.CouponErrorCode;

public class CouponHasAlreadyBeenUsed extends CouponDomainException {
    private static final CouponErrorCode errorCode = CouponErrorCode.ALREADY_USED;

    public CouponHasAlreadyBeenUsed() {
        super(errorCode, "Coupon has already been used.");
    }
}
