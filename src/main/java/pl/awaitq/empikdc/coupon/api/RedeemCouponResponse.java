package pl.awaitq.empikdc.coupon.api;

import pl.awaitq.empikdc.coupon.domain.dto.RedeemCouponResult;

import java.time.Instant;
import java.util.UUID;

public record RedeemCouponResponse(UUID couponRedemptionConfirmationNumber, Instant redeemedAt) {
    public static RedeemCouponResponse of(RedeemCouponResult redeemCouponResult) {
        return new RedeemCouponResponse(
                redeemCouponResult.couponRedemptionId(),
                redeemCouponResult.redeemedAt()
        );
    }
}
