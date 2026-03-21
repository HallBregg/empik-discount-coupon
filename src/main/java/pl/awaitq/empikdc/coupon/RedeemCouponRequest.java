package pl.awaitq.empikdc.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

record RedeemCouponRequest(
        @NotBlank
        String couponCode,

        @NotNull
        UUID userId) {
}
