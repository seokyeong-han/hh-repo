package com.example.ecommerce_3week.service;

import com.example.ecommerce_3week.domain.user.User;
import com.example.ecommerce_3week.domain.user.UserRepository;
import com.example.ecommerce_3week.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
        //test시작하기 전에 항상
    void setUp() {
        userRepository = mock(UserRepository.class); // mocking
        userService = new UserService(userRepository); // 직접 주입
    }

    @Test
    @DisplayName("유저 ID로 조회 성공")
    void findUserById_success() {
        // given
        Long userId = 1L;
        User user = new User(userId, "testuser", 1000L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.findUserById(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getBalance()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("유저 ID로 조회 실패 시 예외 발생")
    void findUserById_fail() {
        // given
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("유저가 없습니다.");
    }

    @Test
    @DisplayName("충전 금액이 null이면 예외 발생")
    void charge_amountNull() {
        // given
        User user = new User(1L, "testuser", 5_000L);

        // when & then
        assertThatThrownBy(() -> userService.charge(user, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전금액은 0보다 커야합니다.");
    }

    @Test
    @DisplayName("충전 금액이 0 이하일 경우 예외 발생")
    void charge_amountZeroOrNegative() {
        // given
        User user = new User(1L, "testuser", 5_000L);

        // when & then
        assertThatThrownBy(() -> userService.charge(user, 0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전금액은 0보다 커야합니다.");

        assertThatThrownBy(() -> userService.charge(user, -1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("충전금액은 0보다 커야합니다.");
    }

    @Test
    @DisplayName("충전 시 최대 한도를 초과하면 예외 발생")
    void charge_exceedsMaxLimit() {
        // given
        User user = new User(1L, "testuser", 990_000L);

        // when & then
        assertThatThrownBy(() -> userService.charge(user, 20_000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대금액 이상 충전 하실 수 없습니다.");
    }


}
