package com.example.ecommerce.api.point.controller;

import com.example.ecommerce.api.point.dto.pointChargeRequest;
import com.example.ecommerce.api.point.facade.PointFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/point")
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;

    @PostMapping("/charge")
    public ResponseEntity<Void> chargePoint(@RequestBody pointChargeRequest request) {
        pointFacade.chargePoint(request);
        return ResponseEntity.ok().build();
    }


}

