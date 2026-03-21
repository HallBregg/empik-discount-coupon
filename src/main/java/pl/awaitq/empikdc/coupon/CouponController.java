package pl.awaitq.empikdc.coupon;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("/api/coupons")
class CouponController {
    private final CreateCouponUseCase createCouponUseCase;
    private final RedeemCouponUseCase redeemCouponUseCase;
    private final Logger logger = LoggerFactory.getLogger(CouponController.class);

    public CouponController(CreateCouponUseCase createCouponUseCase, RedeemCouponUseCase redeemCouponUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.redeemCouponUseCase = redeemCouponUseCase;
    }

    private static final Set<String> LOCALHOST_WHITELIST = Set.of(
            "127.0.0.1",
            "::1",
            "0:0:0:0:0:0:0:1"
    );

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCouponResponse createCoupon(@Valid @RequestBody CreateCouponRequest body, HttpServletRequest request) {
        CreateCouponCommand command = new CreateCouponCommand(
                body.code(),
                body.country(),
                body.maxUsage()
        );
        CreateCouponResult result = createCouponUseCase.createCoupon(command);
        return CreateCouponResponse.of(result);
    }

    @PostMapping("/redeem")
    @ResponseStatus(HttpStatus.OK)
    public RedeemCouponResponse redeemCoupon(@Valid @RequestBody RedeemCouponRequest body, HttpServletRequest request){
        RedeemCouponCommand redeemCouponCommand = new RedeemCouponCommand(
                body.couponCode(),
                body.userId(),
                request.getRemoteAddr());
        RedeemCouponResult redeemCouponResult = redeemCouponUseCase.redeemCoupon(redeemCouponCommand);
        return RedeemCouponResponse.of(redeemCouponResult);
    }
}
