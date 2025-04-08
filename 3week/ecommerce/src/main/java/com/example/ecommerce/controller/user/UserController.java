package com.example.ecommerce.controller.user;

import com.example.ecommerce.domain.user.User;

import com.example.ecommerce.dto.user.UserWithBalanceResponseDto;
import com.example.ecommerce.facade.UserFacade;
import com.example.ecommerce.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserFacade userFacade;

    @GetMapping("/{id}")
    public ResponseEntity<UserWithBalanceResponseDto> getUser(@PathVariable Long id) {
        UserWithBalanceResponseDto response = userFacade.getUserWithBalance(id);
        return ResponseEntity.ok(response);
    }
}
