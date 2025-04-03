package com.example.ecommerce;

import com.example.ecommerce.Repository.UserRepository;
import com.example.ecommerce.Exception.UserNotFoundException;
import com.example.ecommerce.entity.Balance;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.assertj.core.api.Assertions;

import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserTest {
    static Long ID =  1L;

    private MockMvc mockMvc;

    @Mock //Mock 객체 선언
    private UserRepository userRepository;

    @InjectMocks  // userRepository를 주입할 서비스
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //mockito 수동 초기화
        //mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();
    }
    /*@Test
    @DisplayName("사용자 조회 실패")
    void getUser_false(){
        // Given
        Long nonExistentUserId = 999L;

        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty()); // 사용자 없음 설정

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.getUserDetail(nonExistentUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }
*/
    /*@Test
    @DisplayName("사용자 조회 성공")
    void getUserBalance_success(){
        // Given
        User user = new User(ID, "test");
        Balance balance = new Balance(user, null, new Date()); // balance 값 설정

        user.setBalance(balance);
        // When
        Long balanceValue = user.getBalance();
        // Then
        Assertions.assertThat(balanceValue).isEqualTo(0L);
    }*/




}
