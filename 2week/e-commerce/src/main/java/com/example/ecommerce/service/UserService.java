package com.example.ecommerce.service;

import com.example.ecommerce.Exception.UserNotFoundException;
import com.example.ecommerce.Repository.UserRepository;
import com.example.ecommerce.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    private static final String USER_NOT_FOUND_MSG = "사용자를 찾을 수 없습니다.";

    public User getUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MSG));

        return user;
    }


    public Long getUserBalance(Long userId){

        return 1000L;
    }
}
