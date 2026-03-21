package pl.awaitq.empikdc.coupon;

public class GeoIpServiceException extends RuntimeException {

    public GeoIpServiceException(String message) {
        super(message);
    }

    public GeoIpServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
