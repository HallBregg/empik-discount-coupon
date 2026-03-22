package pl.awaitq.empikdc.coupon.domain.port;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.awaitq.empikdc.coupon.domain.CouponRedemption;

import java.util.UUID;


public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, UUID> {
    boolean existsByCouponIdAndUserId(UUID couponId, UUID userId);
}
