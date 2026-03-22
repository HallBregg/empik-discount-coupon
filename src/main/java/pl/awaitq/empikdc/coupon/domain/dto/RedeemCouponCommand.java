package pl.awaitq.empikdc.coupon.domain.dto;

import java.util.UUID;

public record RedeemCouponCommand(String couponCode, UUID userId, String ip) {
}
