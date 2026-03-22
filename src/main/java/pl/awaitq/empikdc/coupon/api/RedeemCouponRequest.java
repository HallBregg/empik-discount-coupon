package pl.awaitq.empikdc.coupon.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RedeemCouponRequest(
        @NotBlank
        String couponCode,

        @NotNull
        UUID userId) {
}
