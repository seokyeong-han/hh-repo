package com.example.ecommerce.api.user.controller;

import com.example.ecommerce.api.user.dto.UserResponse;
import com.example.ecommerce.api.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserFacade  userFacade;

    //유저 ID 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse response = userFacade.getByUserId(id);
        return ResponseEntity.ok(response);
    }

}
