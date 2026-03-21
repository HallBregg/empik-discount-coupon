package pl.awaitq.empikdc.coupon;

import java.util.Objects;


public class CouponCode {
    private final String value;

    public CouponCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = value.trim().toUpperCase();
    }

    public String getValue() {
        return value;
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
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
