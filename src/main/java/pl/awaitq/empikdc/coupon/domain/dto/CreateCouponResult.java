package pl.awaitq.empikdc.coupon.domain.dto;

import pl.awaitq.empikdc.coupon.domain.Coupon;

import java.time.Instant;
import java.util.UUID;


public record CreateCouponResult(UUID id, String code, String country, int maxUsage, int currentUsage, Instant createdAt) {
    public static CreateCouponResult of(Coupon coupon) {
        return new CreateCouponResult(
                coupon.getId(),
                coupon.getCode().value(),
                coupon.getCountry().value(),
                coupon.getMaxUsage(),
                coupon.getCurrentUsage(),
                coupon.getCreatedAt());
    }
}
