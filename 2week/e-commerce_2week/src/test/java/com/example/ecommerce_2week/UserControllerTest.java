package com.example.ecommerce_2week;

import com.example.ecommerce_2week.Controller.UserController;
import com.example.ecommerce_2week.DTO.PurchaseRequest;
import com.example.ecommerce_2week.DTO.PurchaseResponse;
import com.example.ecommerce_2week.entity.Balance;
import com.example.ecommerce_2week.entity.Product;
import com.example.ecommerce_2week.entity.Transaction;
import com.example.ecommerce_2week.entity.User;
import com.example.ecommerce_2week.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(UserController.class)
@Transactional
@Rollback(true)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean //실제 서비스 대신 mcck사용
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환용

    @Test
    void testGetUserResponseBody() throws Exception {
        // Mock 데이터 설정
        Balance mockBalance = new Balance(1L, 10000L, null, null);
        User mockUser = new User(1L, "test", mockBalance);
        mockBalance.setUser(mockUser);

        when(userService.getUser(1L)).thenReturn(mockUser);

        // API 호출 및 JSON 응답 검증
        String responseBody = mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1)) // id 확인
                .andExpect(jsonPath("$.name").value("test")) // name 확인
                .andExpect(jsonPath("$.balance.amount").value(10000)) // balance.amount 확인
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("응답 JSON: " + responseBody);
    }

    @Test
    void testChargeBalance() throws Exception{
        // Mock 데이터 설정
        Balance mockBalance = new Balance(1L, 15000L, null, null); // 충전 후 15000
        User mockUser = new User(1L, "test", mockBalance);
        mockBalance.setUser(mockUser);

        when(userService.chargeBalance(eq(1L), eq(5000L))).thenReturn(mockUser);

        // API 호출 및 JSON 응답 검증
        String responseBody = mockMvc.perform(get("/user/charge/1/5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1)) // userId 확인
                .andExpect(jsonPath("$.name").value("test")) // name 확인
                .andExpect(jsonPath("$.balance.amount").value(15000)) // balance.amount 확인 (기존 10000 + 5000)
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("응답 JSON: " + responseBody);

    }

    //상품 구매
    @Test
    void testPurchaseProduct() throws Exception {
        // Given (Mock 데이터 준비)
        User mockUser = new User(1L, "testUser", new Balance(1L, 50000L, null, null));
        Product mockProduct = new Product(1L, "Laptop", 20000L, 10);
        int quantity = 2;
        Long totalPrice = mockProduct.getPrice() * quantity;

        // transactionId를 수동으로 설정
        Transaction mockTransaction = new Transaction(mockUser, mockProduct, quantity, totalPrice);
        mockTransaction.setId(1L);  // 여기서 transactionId를 직접 설정

        PurchaseRequest request = new PurchaseRequest(1L, 1L, quantity);

        when(userService.purchase(any(PurchaseRequest.class))).thenReturn(new PurchaseResponse(mockTransaction));


        // When & Then (API 호출 및 검증)
        String responseBody = mockMvc.perform(post("/user/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // JSON 변환
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(1))  // 예상 transactionId 검증
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(quantity))
                .andExpect(jsonPath("$.remainingBalance").value(30000)) // 50000 - 40000
                .andReturn()  // ResultActions 반환
                .getResponse()
                .getContentAsString(); // String으로 응답 받기

        System.out.println("응답 JSON: " + responseBody);

    }



}
