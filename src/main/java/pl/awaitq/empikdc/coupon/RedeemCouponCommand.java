package pl.awaitq.empikdc.coupon;

import java.util.UUID;

record RedeemCouponCommand(String couponCode, UUID userId, String ip) {
}
