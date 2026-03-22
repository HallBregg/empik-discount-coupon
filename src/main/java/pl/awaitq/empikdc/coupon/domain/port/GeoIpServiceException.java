package pl.awaitq.empikdc.coupon.domain.port;

public class GeoIpServiceException extends RuntimeException {

    public GeoIpServiceException(String message) {
        super(message);
    }

    public GeoIpServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
