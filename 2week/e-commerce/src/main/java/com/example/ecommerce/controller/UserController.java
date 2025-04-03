package com.example.ecommerce.controller;

import com.example.ecommerce.DTO.UserResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserDetail(@PathVariable Long userId){
        User user = userService.getUserDetail(userId);
        UserResponse response = new UserResponse(user);
        return ResponseEntity.ok(response);
    }
}
