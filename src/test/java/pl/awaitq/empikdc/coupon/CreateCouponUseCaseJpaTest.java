package pl.awaitq.empikdc.coupon;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.TestDatabaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CreateCouponUseCase.class)
@DataJpaTest
//@SpringBootTest  // Instead of @Import and @DataJpaTest
class CreateCouponUseCaseJpaTest {

    @Autowired
    private CreateCouponUseCase createCouponUseCase;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    void shouldCreateCouponAndPersistNormalizedValues(){
        CreateCouponCommand command = new CreateCouponCommand("  TestCoupon123  ", "pl", 100);
        CreateCouponResult result = createCouponUseCase.createCoupon(command);

        assertThat(result).isNotNull();
        assertThat(couponRepository.count()).isEqualTo(1);

        Optional<Coupon> savedCoupon = couponRepository.findByCode(new CouponCode("TESTCOUPON123"));
        assertThat(savedCoupon).isPresent();
        assertThat(savedCoupon.get().getCode().getValue()).isEqualTo("TESTCOUPON123");
        assertThat(savedCoupon.get().getCountry().getValue()).isEqualTo("PL");
        assertThat(savedCoupon.get().getMaxUsage()).isEqualTo(100);
        assertThat(savedCoupon.get().getCurrentUsage()).isZero();
        assertThat(savedCoupon.get().getCreatedAt()).isNotNull();
    }

}