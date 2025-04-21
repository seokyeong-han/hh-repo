package com.example.ecommerce.api.user.facade;

import com.example.ecommerce.api.user.dto.UserResponse;
import com.example.ecommerce.domain.user.model.User;
import com.example.ecommerce.domain.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {
    private UserService userService;

    //조회
    public UserResponse getByUserId(Long id) {
        User user = userService.findById(id);
        return UserResponse.from(user);
    }

}
