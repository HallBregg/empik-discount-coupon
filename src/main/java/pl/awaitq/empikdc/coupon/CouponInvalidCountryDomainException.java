package pl.awaitq.empikdc.coupon;

public class CouponInvalidCountryDomainException extends CouponDomainException {
    private static final CouponErrorCode errorCode = CouponErrorCode.COUNTRY_NOT_ALLOWED;

    public CouponInvalidCountryDomainException(ISOCountry country) {
        super(errorCode, "Country %s is not allowed for this coupon.".formatted(country));
    }
}
