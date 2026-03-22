package pl.awaitq.empikdc.coupon.domain.dto;

public record CreateCouponCommand(String code, String country, int maxUsage) {
}
