package pl.awaitq.empikdc.coupon;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pl.awaitq.empikdc.coupon.fixtures.FakeGeoIpService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({RedeemCouponUseCase.class, RedeemCouponUseCaseIntegrationTest.RedeemCouponUseCaseIntegrationTestConfiguration.class})
class RedeemCouponUseCaseIntegrationTest {

    @TestConfiguration
    static class RedeemCouponUseCaseIntegrationTestConfiguration {
        @Bean
        FakeGeoIpService geoIpService() {
            return new FakeGeoIpService();
        }
    }

    private RedeemCouponUseCase redeemCouponUseCase;

    private CouponRepository couponRepository;

    private CouponRedemptionRepository couponRedemptionRepository;

    private FakeGeoIpService geoIpService;

    @Autowired
    public RedeemCouponUseCaseIntegrationTest(
            RedeemCouponUseCase redeemCouponUseCase,
            CouponRepository couponRepository,
            CouponRedemptionRepository couponRedemptionRepository,
            FakeGeoIpService geoIpService
    ) {
        this.redeemCouponUseCase = redeemCouponUseCase;
        this.couponRepository = couponRepository;
        this.couponRedemptionRepository = couponRedemptionRepository;
        this.geoIpService = geoIpService;
    }

    @Test
    void shouldRedeemCouponSuccessfully() {
        Coupon coupon = couponRepository.saveAndFlush(
                Coupon.create(new CouponCode("SUMMER10"), new ISOCountry("PL"), 2)
        );

        geoIpService.returnCountry(new ISOCountry("PL"));

        RedeemCouponResult result = redeemCouponUseCase.redeemCoupon(
                new RedeemCouponCommand("summer10", UUID.randomUUID(), "10.10.10.10")
        );

        assertThat(result).isNotNull();
        assertThat(result.couponRedemptionId()).isNotNull();
        assertThat(result.redeemedAt()).isNotNull();

        Coupon savedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(savedCoupon.getCurrentUsage()).isEqualTo(1);
        assertThat(couponRedemptionRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldThrowWhenCouponNotFound() {
        geoIpService.returnCountry(new ISOCountry("PL"));

        assertThatThrownBy(() -> redeemCouponUseCase.redeemCoupon(
                new RedeemCouponCommand("missing", UUID.randomUUID(), "10.10.10.10")
        )).isInstanceOf(RuntimeException.class)
                .hasMessage("Coupon code MISSING not found");

        assertThat(couponRedemptionRepository.count()).isZero();
    }

    @Test
    void shouldThrowWhenCountryIsNotAllowed() {
        couponRepository.saveAndFlush(
                Coupon.create(new CouponCode("SUMMER10"), new ISOCountry("PL"), 2)
        );

        geoIpService.returnCountry(new ISOCountry("DE"));

        assertThatThrownBy(() -> redeemCouponUseCase.redeemCoupon(
                new RedeemCouponCommand("SUMMER10", UUID.randomUUID(), "10.10.10.10")
        )).isInstanceOf(CouponInvalidCountryDomainException.class);

        assertThat(couponRedemptionRepository.count()).isZero();
    }

    @Test
    void shouldThrowWhenCouponLimitReached() {
        Coupon coupon = couponRepository.saveAndFlush(
                Coupon.create(new CouponCode("SUMMER10"), new ISOCountry("PL"), 1)
        );
        coupon.redeem();
        couponRepository.saveAndFlush(coupon);

        geoIpService.returnCountry(new ISOCountry("PL"));

        assertThatThrownBy(() -> redeemCouponUseCase.redeemCoupon(
                new RedeemCouponCommand("SUMMER10", UUID.randomUUID(), "10.10.10.10")
        )).isInstanceOf(CouponLimitReachedDomainException.class);

        assertThat(couponRedemptionRepository.count()).isZero();
    }

    @Test
    void shouldThrowWhenCouponAlreadyUsedByUser() {
        Coupon coupon = couponRepository.saveAndFlush(
                Coupon.create(new CouponCode("SUMMER10"), new ISOCountry("PL"), 2)
        );
        UUID userId = UUID.randomUUID();

        geoIpService.returnCountry(new ISOCountry("PL"));
        redeemCouponUseCase.redeemCoupon(new RedeemCouponCommand("SUMMER10", userId, "10.10.10.10"));

        assertThatThrownBy(() -> redeemCouponUseCase.redeemCoupon(
                new RedeemCouponCommand("SUMMER10", userId, "10.10.10.10")
        )).isInstanceOf(CouponHasAlreadyBeenUsed.class);

        Coupon savedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(savedCoupon.getCurrentUsage()).isEqualTo(1);
        assertThat(couponRedemptionRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldThrowWhenGeoIpServiceFails() {
        couponRepository.saveAndFlush(
                Coupon.create(new CouponCode("SUMMER10"), new ISOCountry("PL"), 2)
        );

        geoIpService.throwFailure();

        assertThatThrownBy(() -> redeemCouponUseCase.redeemCoupon(
                new RedeemCouponCommand("SUMMER10", UUID.randomUUID(), "10.10.10.10")
        )).isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to resolve country from IP")
                .hasCauseInstanceOf(GeoIpServiceException.class);

        assertThat(couponRedemptionRepository.count()).isZero();
    }
}
