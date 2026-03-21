package pl.awaitq.empikdc.coupon;


public interface GeoIpService {
    ISOCountry getCountryFromIp(String ip);
}
