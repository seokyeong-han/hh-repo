package com.example.ecommerce;

import com.example.ecommerce.entity.Balance;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

//@WebMvcTest(UserControllerTest.class) //userController만 로드하여 controller Layer테스트
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockitoBean //가짜(Mock) 객체를 주입 실제 userService 동작은 수행하지 않고 원하는 응답 미리 정의
    private UserService userService;

    @Autowired
    private MockMvc mockMvc; //Spring MVC 컨트롤러를 테스트할 수 있는 가상의 HTTP 요청을 보내는 도구

    @Test
    public void debugUserController() throws Exception {
        mockMvc.perform(get("/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()); // 🔥 요청 결과를 콘솔에 출력
    }
    /* @Test
    @DisplayName("유저의 잔액,이름 조회 ")
    public void User() throws Exception {
        Long userId = 1L;
        User user = new User(userId, "test");
        user.setBalance(new Balance(user, 50L));

        when(userService.getUserDetail(userId)).thenReturn(user);

        mockMvc.perform(get("/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.userId").value(userId))
                .andExpect((ResultMatcher) jsonPath("$.name").value("test"))
                .andExpect((ResultMatcher) jsonPath("$.balance").value(50L));
    }*/
}


