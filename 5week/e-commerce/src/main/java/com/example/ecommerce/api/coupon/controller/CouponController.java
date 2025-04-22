package com.example.ecommerce.api.coupon.controller;

import com.example.ecommerce.api.coupon.dto.CouponResponse;
import com.example.ecommerce.api.coupon.facade.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/coupon")
public class CouponController {
    private final CouponFacade couponFacade;

    public CouponController(CouponFacade couponFacade) {
        this.couponFacade = couponFacade;
    }

    // 전체 쿠폰 목록 조회
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupon() {
        return ResponseEntity.ok(couponFacade.getAllCoupon());
    }

    // 단일 쿠폰 목록 조회
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse>  getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponFacade.getCouponById(id));
    }
    
    //쿠폰 사용
    @PostMapping("/{couponId}/assign")
    public ResponseEntity<Void> assignCoupon(@PathVariable Long couponId, @RequestParam Long userId) {
        couponFacade.assignCouponToUser(couponId, userId);
        return ResponseEntity.ok().build();
    }

}
