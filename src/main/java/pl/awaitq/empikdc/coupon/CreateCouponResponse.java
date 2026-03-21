package pl.awaitq.empikdc.coupon;

import java.time.Instant;
import java.util.UUID;

record CreateCouponResponse(UUID id, String code, String country, int maxUsage, int currentUsage, Instant createdAt) {
    public static CreateCouponResponse of (CreateCouponResult createCouponResult) {
        return new CreateCouponResponse(
                createCouponResult.id(),
                createCouponResult.code(),
                createCouponResult.country(),
                createCouponResult.maxUsage(),
                createCouponResult.currentUsage(),
                createCouponResult.createdAt());
    }
}
