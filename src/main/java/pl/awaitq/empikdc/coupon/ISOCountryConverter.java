package pl.awaitq.empikdc.coupon;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
class ISOCountryConverter implements AttributeConverter<ISOCountry, String> {

    @Override
    public String convertToDatabaseColumn(ISOCountry attribute) {
        return attribute.getValue();
    }

    @Override
    public ISOCountry convertToEntityAttribute(String dbData) {
        return new ISOCountry(dbData);
    }
}
