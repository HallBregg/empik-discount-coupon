package pl.awaitq.empikdc.coupon;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(
    uniqueConstraints = {
            @UniqueConstraint(name = "unique_coupon_redemption_coupon_id_user_id", columnNames = {"coupon_id", "user_id"})
    },
    indexes = {
            @Index(name = "index_coupon_redemption_coupon_id", columnList = "coupon_id"),
            @Index(name = "index_coupon_redemption_user_id", columnList = "user_id"),
            @Index(name = "index_coupon_redemption_redeemed_at", columnList = "redeemed_at")
    }
)
class CouponRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID couponId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Instant redeemedAt;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false, length = 2)
    @Convert(converter = ISOCountryConverter.class)
    private ISOCountry country;

    @Version
    @Column(nullable = false)
    private int version;  // rather not required

    protected CouponRedemption() {}

    private CouponRedemption(UUID couponId, UUID userId, String ip, ISOCountry country) {
        if (couponId == null) {
            throw new IllegalArgumentException("Coupon ID cannot be null.");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("IP cannot be null or empty.");
        }
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null or empty.");
        }

        this.couponId = couponId;
        this.userId = userId;
        this.ip = ip;
        this.country = country;
        this.redeemedAt = Instant.now();
    }
}
