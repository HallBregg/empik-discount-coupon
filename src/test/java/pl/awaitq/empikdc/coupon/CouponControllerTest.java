package pl.awaitq.empikdc.coupon;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.awaitq.empikdc.coupon.api.CouponController;
import pl.awaitq.empikdc.coupon.api.CreateCouponRequest;
import pl.awaitq.empikdc.coupon.api.CreateCouponResponse;
import pl.awaitq.empikdc.coupon.api.RedeemCouponRequest;
import pl.awaitq.empikdc.coupon.domain.*;
import pl.awaitq.empikdc.coupon.domain.dto.*;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CouponController.class)
@Import(GlobalControllerAdvice.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateCouponUseCase createCouponUseCase;

    @MockitoBean
    private RedeemCouponUseCase redeemCouponUseCase;

    @Test
    void shouldCreateCoupon() throws Exception {
        UUID couponId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-03-22T12:00:00Z");

        CreateCouponResult result = new CreateCouponResult(
                couponId,
                "SUMMER10",
                "PL",
                100,
                0,
                createdAt
        );
        CreateCouponResponse expectedResponse = CreateCouponResponse.of(result);
        when(createCouponUseCase.createCoupon(any(CreateCouponCommand.class))).thenReturn(result);
        CreateCouponRequest request = new CreateCouponRequest(" summer10 ", "pl", 100);
        mockMvc
                .perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
        verify(createCouponUseCase).createCoupon(any(CreateCouponCommand.class));
    }

    @Test
    void shouldReturnBadRequestWhenCreateCouponBodyIsInvalid() throws Exception {
        String invalidJson = """
                {
                  "code": "",
                  "country": "POL",
                  "maxUsage": 0
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                 {
                   "instance":"/api/coupons",
                   "status":400,
                   "title":"Validation error",
                   "type":"https://documentation.empikdc-service/errors/VALIDATION_ERROR",
                   "code":"VALIDATION_ERROR",
                   "errors":[
                     {
                       "field":"code",
                       "message":"must not be blank"
                     }
                   ]
                 }
                 """))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void shouldReturnAlreadyExistsBadRequestWhenCouponAlreadyExists() throws Exception {
        when(createCouponUseCase.createCoupon(any(CreateCouponCommand.class)))
                .thenThrow(new CouponAlreadyExistsDomainException(CouponCode.of("SUMMER10")));

        CreateCouponRequest request = new CreateCouponRequest("SUMMER10", "PL", 100);
        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                 {
                   "instance":"/api/coupons",
                   "status":400,
                   "title":"Coupon error",
                   "type":"https://documentation.empikdc-service/errors/COUPON_ALREADY_EXISTS",
                   "code":"COUPON_ALREADY_EXISTS"
                 }
                 """));
    }

    @Test
    void shouldRedeemCoupon() throws Exception {
        UUID redemptionId = UUID.randomUUID();
        Instant redeemedAt = Instant.parse("2026-03-22T12:05:00Z");

        RedeemCouponResult result = new RedeemCouponResult(redemptionId, redeemedAt);

        when(redeemCouponUseCase.redeemCoupon(any(RedeemCouponCommand.class))).thenReturn(result);

        RedeemCouponRequest request = new RedeemCouponRequest("SUMMER10", UUID.randomUUID());

        mockMvc.perform(post("/api/coupons/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(req -> {
                            req.setRemoteAddr("127.0.0.1");
                            return req;
                        }))
                .andExpect(status().isOk());

        verify(redeemCouponUseCase).redeemCoupon(any(RedeemCouponCommand.class));
    }

    @Test
    void shouldReturnAlreadyUsedBadRequestWhenCouponAlreadyUsed() throws Exception {
        when(redeemCouponUseCase.redeemCoupon(any(RedeemCouponCommand.class)))
                .thenThrow(new CouponHasAlreadyBeenUsed());

        RedeemCouponRequest request = new RedeemCouponRequest("SUMMER10", UUID.randomUUID());

        mockMvc.perform(post("/api/coupons/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(req -> {
                            req.setRemoteAddr("127.0.0.1");
                            return req;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                 {
                   "instance":"/api/coupons/redeem",
                   "status":400,
                   "title":"Coupon error",
                   "type":"https://documentation.empikdc-service/errors/ALREADY_USED",
                   "code":"ALREADY_USED"
                 }
                 """));
    }

    @Test
    void shouldReturnLimitReachedBadRequestWhenCouponLimitReached() throws Exception {
        when(redeemCouponUseCase.redeemCoupon(any(RedeemCouponCommand.class)))
                .thenThrow(new CouponLimitReachedDomainException());

        RedeemCouponRequest request = new RedeemCouponRequest("SUMMER10", UUID.randomUUID());

        mockMvc.perform(post("/api/coupons/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(req -> {
                            req.setRemoteAddr("127.0.0.1");
                            return req;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                 {
                   "instance":"/api/coupons/redeem",
                   "status":400,
                   "title":"Coupon error",
                   "type":"https://documentation.empikdc-service/errors/LIMIT_REACHED",
                   "code":"LIMIT_REACHED"
                 }
                 """));
    }

    @Test
    void shouldReturnCountryNotAllowedBadRequestWhenCountryIsInvalid() throws Exception {
        when(redeemCouponUseCase.redeemCoupon(any(RedeemCouponCommand.class)))
                .thenThrow(new CouponInvalidCountryDomainException(null));

        RedeemCouponRequest request = new RedeemCouponRequest("SUMMER10", UUID.randomUUID());

        mockMvc.perform(post("/api/coupons/redeem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(req -> {
                            req.setRemoteAddr("127.0.0.1");
                            return req;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                 {
                   "instance":"/api/coupons/redeem",
                   "status":400,
                   "title":"Coupon error",
                   "type":"https://documentation.empikdc-service/errors/COUNTRY_NOT_ALLOWED",
                   "code":"COUNTRY_NOT_ALLOWED"
                 }
                 """));
    }
}
