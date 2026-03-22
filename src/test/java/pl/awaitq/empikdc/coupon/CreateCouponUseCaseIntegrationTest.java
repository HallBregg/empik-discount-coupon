package pl.awaitq.empikdc.coupon;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pl.awaitq.empikdc.coupon.domain.*;
import pl.awaitq.empikdc.coupon.domain.dto.CouponAlreadyExistsDomainException;
import pl.awaitq.empikdc.coupon.domain.dto.CreateCouponCommand;
import pl.awaitq.empikdc.coupon.domain.dto.CreateCouponResult;
import pl.awaitq.empikdc.coupon.domain.port.CouponRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CreateCouponUseCase.class)
class CreateCouponUseCaseIntegrationTest {

    private final CreateCouponUseCase createCouponUseCase;

    private final CouponRepository couponRepository;

    @Autowired
    public CreateCouponUseCaseIntegrationTest(CreateCouponUseCase createCouponUseCase, CouponRepository couponRepository) {
        this.createCouponUseCase = createCouponUseCase;
        this.couponRepository = couponRepository;
    }

    @Test
    void shouldCreateCoupon() {
        CreateCouponCommand command = new CreateCouponCommand(" summer10 ", "pl", 100);

        CreateCouponResult result = createCouponUseCase.createCoupon(command);

        assertThat(result).isNotNull();
        assertThat(couponRepository.count()).isEqualTo(1);

        Coupon savedCoupon = couponRepository.findByCode(new CouponCode("SUMMER10"))
                .orElseThrow();

        assertThat(savedCoupon.getCode().value()).isEqualTo("SUMMER10");
        assertThat(savedCoupon.getCountry().value()).isEqualTo("PL");
        assertThat(savedCoupon.getMaxUsage()).isEqualTo(100);
        assertThat(savedCoupon.getCurrentUsage()).isZero();
        assertThat(savedCoupon.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldThrowWhenCouponAlreadyExistsInPrecheck() {
        createCouponUseCase.createCoupon(new CreateCouponCommand("SUMMER10", "PL", 100));

        assertThatThrownBy(
                () -> createCouponUseCase.createCoupon(
                        new CreateCouponCommand("summer10", "PL", 100)))
                .isInstanceOf(CouponAlreadyExistsDomainException.class)
                .hasMessageContaining("SUMMER10");

        assertThat(couponRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldNormalizeCodeBeforeExistsCheck() {
        createCouponUseCase.createCoupon(new CreateCouponCommand("SUMMER10", "PL", 100));

        assertThatThrownBy(() -> createCouponUseCase.createCoupon(new CreateCouponCommand(" summer10 ", "PL", 100)))
                .isInstanceOf(CouponAlreadyExistsDomainException.class)
                .hasMessageContaining("SUMMER10");
    }

    @Test
    void shouldRejectInvalidCountry() {
        CreateCouponCommand command = new CreateCouponCommand("SUMMER10", "POL", 100);

        assertThatThrownBy(() -> createCouponUseCase.createCoupon(command))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(couponRepository.count()).isZero();
    }

    @Test
    void shouldRejectInvalidCode() {
        CreateCouponCommand command = new CreateCouponCommand("   ", "PL", 100);

        assertThatThrownBy(() -> createCouponUseCase.createCoupon(command))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(couponRepository.count()).isZero();
    }

    @Test
    void shouldRejectInvalidMaxUsage() {
        CreateCouponCommand command = new CreateCouponCommand("SUMMER10", "PL", 0);

        assertThatThrownBy(() -> createCouponUseCase.createCoupon(command))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(couponRepository.count()).isZero();
    }

}
