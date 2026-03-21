package pl.awaitq.empikdc.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, UUID> {
    boolean existsByCouponIdAndUserId(UUID couponId, UUID userId);
}
