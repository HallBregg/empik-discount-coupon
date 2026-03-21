package pl.awaitq.empikdc.coupon;

import java.time.Instant;
import java.util.UUID;

public class CouponOptimisticLockingFailureException extends RuntimeException {
    private final UUID couponId;
    private final UUID userId;
    private final Instant triedAt;

    public CouponOptimisticLockingFailureException(UUID couponId, UUID userId, Instant triedAt) {
        this.couponId = couponId;
        this.userId = userId;
        this.triedAt = triedAt;
        super("Tried to redeem coupon %s for user %s at %s, but it has been updated since then".formatted(couponId, userId, triedAt));
    }
}
