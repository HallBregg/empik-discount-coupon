package pl.awaitq.empikdc.coupon;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.awaitq.empikdc.coupon.domain.port.GeoIpService;
import pl.awaitq.empikdc.coupon.domain.ISOCountry;

import static org.assertj.core.api.Assertions.assertThat;


// I want to test whether my geo service really works and returns the correct response.
// I don't want to mock it for this test.
@SpringBootTest // For now. Later we should use SpringJUnitConfig.
@ActiveProfiles("test")
@Tag("live")
class CountryIsGeoIpServiceLiveTest {
    @Autowired
    private GeoIpService geoIpService;

    @Test
    void shouldResolveCountryFromRealApi() {
        ISOCountry result = geoIpService.getCountryFromIp("8.8.8.8");

        assertThat(result).isEqualTo(new ISOCountry("US"));
    }
}
