package com.example.ecommerce_3week.presentation;

import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.dto.user.UserChargeRequest;
import com.example.ecommerce_3week.dto.user.UserFacadeResponse;
import com.example.ecommerce_3week.dto.user.UserResponse;
import com.example.ecommerce_3week.facade.UserFacade;
import com.example.ecommerce_3week.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserFacade userFacade;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserFacadeResponse> getUser(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(new UserFacadeResponse(user.getId(), user.getUsername(), user.getBalance()));
    }

    @PostMapping("/charge")
    public ResponseEntity<Void> charge(@RequestBody UserChargeRequest request) {
        userFacade.chargeUser(request);
        return ResponseEntity.ok().build();
    }

}
