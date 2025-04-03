package com.example.ecommerce_2week.Controller;

import com.example.ecommerce_2week.DTO.UserResponse;
import com.example.ecommerce_2week.entity.User;
import com.example.ecommerce_2week.repository.UserRepository;
import com.example.ecommerce_2week.service.UserService;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
        User user = userService.getUser(id);
        UserResponse response = new UserResponse(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/charge/{id}/{amount}")
    public ResponseEntity<UserResponse> chargeBalance(@PathVariable Long id, @PathVariable Long amount){
        User user = userService.chargeBalance(id,amount);
        UserResponse response = new UserResponse(user);
        return ResponseEntity.ok(response);
    }




}
