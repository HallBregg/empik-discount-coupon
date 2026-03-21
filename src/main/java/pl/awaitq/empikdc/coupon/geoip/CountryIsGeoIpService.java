package pl.awaitq.empikdc.coupon.geoip;


import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pl.awaitq.empikdc.coupon.GeoIpService;
import pl.awaitq.empikdc.coupon.GeoIpServiceException;
import pl.awaitq.empikdc.coupon.ISOCountry;


record CountryIsResponse(String ip, String country) {}


@Component
class CountryIsGeoIpService implements GeoIpService {
    private final RestClient client;

    public CountryIsGeoIpService(RestClient.Builder builder) {
        this.client = builder
                .baseUrl("https://api.country.is")
                .build();
    }

    // TODO: Cache this!
    @Override
    public ISOCountry getCountryFromIp(String ip) {
        try{
            CountryIsResponse response = client
                    .get()
                    .uri("/{ip}", ip)
                    .retrieve()
                    .body(CountryIsResponse.class);

            if (response == null || response.country() == null) {
                throw new GeoIpServiceException("Failed to get country from IP: " + ip);
            }

            return new ISOCountry(response.country());

        } catch (RestClientException | IllegalArgumentException e) {
            throw new GeoIpServiceException("Failed to get country from IP: " + ip, e);
        }
    }
}
