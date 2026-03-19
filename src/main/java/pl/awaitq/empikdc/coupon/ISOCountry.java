package pl.awaitq.empikdc.coupon;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

class ISOCountry {
    private final String value;

    public ISOCountry(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        value = value.trim().toUpperCase();

        if (!Set.of(Locale.getISOCountries()).contains(value)) {
            throw new IllegalArgumentException("Value is not a valid ISO country code");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
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
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
