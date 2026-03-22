package pl.awaitq.empikdc.coupon.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;


@Converter
class ISOCountryConverter implements AttributeConverter<ISOCountry, String> {

    @Override
    public String convertToDatabaseColumn(ISOCountry attribute) {
        return attribute.value();
    }

    @Override
    public ISOCountry convertToEntityAttribute(String dbData) {
        return new ISOCountry(dbData);
    }
}


public record ISOCountry(String value) {
    public ISOCountry {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        value = value.trim().toUpperCase();

        if (!Set.of(Locale.getISOCountries()).contains(value)) {
            throw new IllegalArgumentException("Value is not a valid ISO country code");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ISOCountry that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
