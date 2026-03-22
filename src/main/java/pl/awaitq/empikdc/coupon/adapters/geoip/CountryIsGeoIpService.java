package pl.awaitq.empikdc.coupon.adapters.geoip;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.awaitq.empikdc.coupon.domain.port.GeoIpService;
import pl.awaitq.empikdc.coupon.domain.port.GeoIpServiceException;
import pl.awaitq.empikdc.coupon.domain.ISOCountry;


record CountryIsResponse(String ip, String country) {}


@Component
class CountryIsGeoIpService implements GeoIpService {
    private final RestClient client;
    private final Logger logger = LoggerFactory.getLogger(CountryIsGeoIpService.class);

    public CountryIsGeoIpService(RestClient.Builder builder) {
        this.client = builder
                .baseUrl("https://api.country.is")
                .build();
    }

    @Override
    @Cacheable(cacheNames = "countryData", key = "#ip")
    public ISOCountry getCountryFromIp(String ip) {
        try{
            CountryIsResponse response = client
                    .get()
                    .uri("/{ip}", ip)
                    .retrieve()
                    .body(CountryIsResponse.class);

            if (response == null || response.country() == null) {
                logger.error("Failed to get country from IP: {}", ip);
                throw new GeoIpServiceException("Failed to get country from IP: " + ip);
            }

            return new ISOCountry(response.country());

        } catch (RestClientException | IllegalArgumentException e) {
            logger.error("Failed to get country from IP: {}", ip, e);
            throw new GeoIpServiceException("Failed to get country from IP: " + ip, e);
        }
    }
}
