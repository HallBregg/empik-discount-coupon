package pl.awaitq.empikdc.coupon.api;


import pl.awaitq.empikdc.coupon.domain.dto.CreateCouponResult;

import java.time.Instant;
import java.util.UUID;

public record CreateCouponResponse(UUID id, String code, String country, int maxUsage, int currentUsage, Instant createdAt) {
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
