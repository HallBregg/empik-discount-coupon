package pl.awaitq.empikdc.coupon;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
interface CouponRepository extends JpaRepository<Coupon, UUID> {

    boolean existsByCode(CouponCode code);

    Optional<Coupon> findByCode(CouponCode code);
}
