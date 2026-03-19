package pl.awaitq.empikdc.coupon;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
class CouponCodeConverter implements AttributeConverter<CouponCode, String> {
    @Override
    public String convertToDatabaseColumn(CouponCode attribute) {
        return attribute.getValue();
    }

    @Override
    public CouponCode convertToEntityAttribute(String dbData) {
        return new CouponCode(dbData);
    }
}
