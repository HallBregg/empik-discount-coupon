package pl.awaitq.empikdc.coupon.domain.port;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.awaitq.empikdc.coupon.domain.Coupon;
import pl.awaitq.empikdc.coupon.domain.CouponCode;
import pl.awaitq.empikdc.coupon.domain.ISOCountry;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    boolean existsByCode(CouponCode code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findForUpdateByCode(CouponCode code);

    Optional<Coupon> findByCode(CouponCode code);

    @Modifying
    @Query("""
        update Coupon c
        set c.currentUsage = c.currentUsage + 1
        where
            c.code = :code and
            c.country = :country and
            c.currentUsage < c.maxUsage
    """)
    int incrementRedemptions(CouponCode code, ISOCountry country);
}
