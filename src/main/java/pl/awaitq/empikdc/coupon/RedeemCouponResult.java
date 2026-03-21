package pl.awaitq.empikdc.coupon;

import java.time.Instant;
import java.util.UUID;

record RedeemCouponResult(UUID couponRedemptionId, Instant redeemedAt) {}
