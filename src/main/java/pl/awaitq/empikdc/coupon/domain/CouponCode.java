package pl.awaitq.empikdc.coupon.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;


@Converter
class CouponCodeConverter implements AttributeConverter<CouponCode, String> {
    @Override
    public String convertToDatabaseColumn(CouponCode attribute) {
        return attribute.value();
    }

    @Override
    public CouponCode convertToEntityAttribute(String dbData) {
        return new CouponCode(dbData);
    }
}


public record CouponCode(String value) {
    public CouponCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value.trim().toUpperCase();
    }

    public static CouponCode of(String value) {
        return new CouponCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CouponCode that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
