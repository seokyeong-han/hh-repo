package com.example.ecommerce_2week.Controller;

import com.example.ecommerce_2week.DTO.PurchaseRequest;
import com.example.ecommerce_2week.DTO.PurchaseResponse;
import com.example.ecommerce_2week.DTO.UserResponse;
import com.example.ecommerce_2week.entity.User;
import com.example.ecommerce_2week.repository.UserRepository;
import com.example.ecommerce_2week.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User API", description = "고객 관리 API")
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

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> purchaseProduct(@RequestBody PurchaseRequest request) {
        PurchaseResponse response = userService.purchase(request);
        return ResponseEntity.ok(response);
    }





    }
