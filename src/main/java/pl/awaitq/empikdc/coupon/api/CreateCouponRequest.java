package pl.awaitq.empikdc.coupon.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCouponRequest(
        @NotBlank
        String code,

        @NotBlank
        @Size(min=2, max=3)
        String country,

        @Min(0)
        int maxUsage
) {}
