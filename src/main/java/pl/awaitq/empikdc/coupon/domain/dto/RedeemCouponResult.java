package pl.awaitq.empikdc.coupon.domain.dto;

import java.time.Instant;
import java.util.UUID;

public record RedeemCouponResult(UUID couponRedemptionId, Instant redeemedAt) {}
