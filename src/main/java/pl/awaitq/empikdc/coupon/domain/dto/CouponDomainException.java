package pl.awaitq.empikdc.coupon.domain.dto;


import pl.awaitq.empikdc.coupon.domain.CouponErrorCode;

public class CouponDomainException extends RuntimeException {
    private final CouponErrorCode errorCode;

    public CouponDomainException(CouponErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }

    public CouponDomainException(CouponErrorCode code, String message) {
        super(message);
        this.errorCode = code;
    }

    public CouponErrorCode getErrorCode() {
        return errorCode;
    }
}
