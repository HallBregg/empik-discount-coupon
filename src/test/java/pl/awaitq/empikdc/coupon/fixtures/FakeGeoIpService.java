package pl.awaitq.empikdc.coupon.fixtures;

import pl.awaitq.empikdc.coupon.GeoIpService;
import pl.awaitq.empikdc.coupon.GeoIpServiceException;
import pl.awaitq.empikdc.coupon.ISOCountry;


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
