package pl.awaitq.empikdc.coupon;

import java.time.Instant;
import java.util.UUID;

record RedeemCouponResponse(UUID couponRedemptionConfirmationNumber, Instant redeemedAt) {
    public static RedeemCouponResponse of(RedeemCouponResult redeemCouponResult) {
        return new RedeemCouponResponse(
                redeemCouponResult.couponRedemptionId(),
                redeemCouponResult.redeemedAt()
        );
    }
}
