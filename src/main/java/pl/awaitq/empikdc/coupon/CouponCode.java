package pl.awaitq.empikdc.coupon;

class CouponCode {
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
}
