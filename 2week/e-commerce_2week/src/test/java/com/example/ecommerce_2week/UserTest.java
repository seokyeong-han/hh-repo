package com.example.ecommerce_2week;

import com.example.ecommerce_2week.DTO.UserResponse;
import com.example.ecommerce_2week.entity.Balance;
import com.example.ecommerce_2week.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class UserTest {
    @Test
    void 유저_정상조회(){
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .build();

        Balance balance = Balance.builder()
                .id(1L)
                .amount(10000L)
                .updateDt(new Date())
                .user(user)
                .build();

        user.setBalance(balance);
        // when (UserResponse 생성)
        UserResponse userResponse = new UserResponse(user);
        // then (검증)
        assertNotNull(userResponse);
        assertEquals(1L, userResponse.getUserId());
        assertEquals("testUser", userResponse.getName());
        assertEquals(10000L, userResponse.getBalance());
    }

    @Test
    void 유저_정보_없음(){
        // given (유저가 없는 경우)
        User user = null;
        // when & then (예외 발생 검증)
        assertThrows(NullPointerException.class,()-> new UserResponse(user));
    }
    @Test
    void 유저_잔액_충전(){
        // given (유저와 잔액 생성)
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .balance(Balance.builder()
                        .id(1L)
                        .amount(50000L) // 초기 잔액: 50,000원
                        .updateDt(new Date())
                        .build())
                .build();

        // when (충전 실행)
        user.chargeBalance(20000L); // 20,000원 충전

        // then (잔액 검증)
        assertEquals(70000L, user.getBalance());
    }

    @Test
    void 유저_잔액_초과_충전_실패() {
        // given
        User user = User.builder()
                .id(1L)
                .name("testUser")
                .balance(Balance.builder()
                        .id(1L)
                        .amount(990000L) // 초기 잔액: 990,000원
                        .updateDt(new Date())
                        .build())
                .build();

        // when & then (예외 발생 검증)
        assertThrows(IllegalArgumentException.class, () -> user.chargeBalance(20000L)); // 20,000원 충전 → 최대 잔액 초과
    }



}
