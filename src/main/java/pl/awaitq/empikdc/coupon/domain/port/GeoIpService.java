package pl.awaitq.empikdc.coupon.domain.port;


import pl.awaitq.empikdc.coupon.domain.ISOCountry;


/**
 *  GeoIpServiceException should be thrown when there is an error with the geoip service.
 */
public interface GeoIpService {
    ISOCountry getCountryFromIp(String ip);
}
