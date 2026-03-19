package pl.awaitq.empikdc.coupon;

import java.time.Instant;
import java.util.UUID;


record CreateCouponResult(UUID id, String code, String country, int maxUsage, int currentUsage, Instant createdAt) {
    public static CreateCouponResult of(Coupon coupon) {
        return new CreateCouponResult(
                coupon.getId(),
                coupon.getCode().getValue(),
                coupon.getCountry().getValue(),
                coupon.getMaxUsage(),
                coupon.getCurrentUsage(),
                coupon.getCreatedAt());
    }
}
