package pl.awaitq.empikdc.coupon.domain.dto;

import pl.awaitq.empikdc.coupon.domain.CouponCode;
import pl.awaitq.empikdc.coupon.domain.CouponErrorCode;

public class CouponAlreadyExistsDomainException extends CouponDomainException {
    private static final CouponErrorCode errorCode = CouponErrorCode.COUPON_ALREADY_EXISTS;

    public CouponAlreadyExistsDomainException(CouponCode code) {
        super(errorCode, "Coupon for code %s already exists".formatted(code));
    }
}
