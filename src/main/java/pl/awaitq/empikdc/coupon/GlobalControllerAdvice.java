package pl.awaitq.empikdc.coupon;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.List;


// Small DTO to support validation errors
record FieldError(String field, String message) {}


// To unify all error messages, we could override ResponseEntityExceptionHandler.
@ControllerAdvice
class GlobalControllerAdvice {
    // I have decided to use RFC 7807 error format. It is well-known and widely used.
    // Also, I have decided to use the ProblemDetail class.
    // Without it, we would have to pack a response in ResponseEntity or annotate a handler with ResponseStatus.

    // Potentially, we could have a site with errors documentation.
    private URI errorTypeUri(String code) {
        return URI.create("https://documentation.empikdc-service/errors/" + code);
    }

    // Domain exceptions
    @ExceptionHandler(CouponDomainException.class)
    public ProblemDetail handleCouponDomainException(CouponDomainException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        if(e instanceof CouponNotFoundDomainException){
            httpStatus = HttpStatus.NOT_FOUND;
        }
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(httpStatus, e.getMessage());
        problemDetail.setTitle("Coupon error");
        problemDetail.setProperty("code", e.getErrorCode().name());
        problemDetail.setType(errorTypeUri(e.getErrorCode().name()));
        return problemDetail;
    }

    // Validation errors - provide some details about an error instead of generic 400.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> structurizedErrors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldError(err.getField(), err.getDefaultMessage()))
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation error");
        problemDetail.setType(errorTypeUri("VALIDATION_ERROR"));
        problemDetail.setProperty("code", "VALIDATION_ERROR");
        problemDetail.setProperty("errors", structurizedErrors);
        return problemDetail;
    }
}
