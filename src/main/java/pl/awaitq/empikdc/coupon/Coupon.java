package pl.awaitq.empikdc.coupon;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(
        name = "coupon",
        indexes = {
                @Index(name = "index_coupon_country", columnList = "country"),
                @Index(name = "index_coupon_created_at", columnList = "created_at")
        }
)
class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    @Convert(converter = CouponCodeConverter.class)
    private CouponCode code;

    @Column(nullable = false)
    @Convert(converter = ISOCountryConverter.class)
    private ISOCountry country;

    @Column(nullable = false)
    private int maxUsage;

    @Column(nullable = false)
    private int currentUsage;

    @Column(nullable = false)
    private Instant createdAt;

    @Version
    private int version;

    public static Coupon create(CouponCode code, ISOCountry country, int maxUsage) {
        return new Coupon(code, country, maxUsage);
    }

    public boolean countryAllowed(ISOCountry country) {
        return this.country.equals(country);
    }

    public boolean isMaxUsageReached() {
        return currentUsage >= maxUsage;
    }

    public void redeem() {
        currentUsage++;
    }

    protected Coupon() {}

    private Coupon(CouponCode code, ISOCountry country, int maxUsage){
        if (code == null) {
            throw new IllegalArgumentException("Coupon code cannot be null or empty.");
        }
        if (country == null) {
            throw new IllegalArgumentException("Coupon country cannot be null.");
        }
        if (maxUsage <= 0) {
            throw new IllegalArgumentException("Max usage must be greater than 0.");
        }
        this.code = code;
        this.country = country;
        this.maxUsage = maxUsage;
        this.currentUsage = 0;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public CouponCode getCode() {
        return code;
    }

    public ISOCountry getCountry() {
        return country;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public int getCurrentUsage() {
        return currentUsage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
