package pl.awaitq.empikdc.coupon.fixtures;

import pl.awaitq.empikdc.coupon.domain.port.GeoIpService;
import pl.awaitq.empikdc.coupon.domain.port.GeoIpServiceException;
import pl.awaitq.empikdc.coupon.domain.ISOCountry;


public class FakeGeoIpService implements GeoIpService {
    private ISOCountry countryToReturn = new ISOCountry("PL");
    private boolean shouldThrow;

    public void returnCountry(ISOCountry country) {
        this.countryToReturn = country;
        this.shouldThrow = false;
    }

    public void throwFailure() {
        this.shouldThrow = true;
    }

    @Override
    public ISOCountry getCountryFromIp(String ip) {
        if (shouldThrow) {
            throw new GeoIpServiceException("Geo IP unavailable");
        }
        return countryToReturn;
    }
}
