package com.example.ecommerce_2week;

import com.example.ecommerce_2week.Controller.ProductController;
import com.example.ecommerce_2week.DTO.ProductResponse;
import com.example.ecommerce_2week.entity.Product;
import com.example.ecommerce_2week.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean //실제 서비스 대신 mcck사용
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // JSON 변환용

    @Test
    void testGetProductResponseBody() throws Exception {
        // Mock 데이터 설정
        Product mockProduct = new Product(1L, "Laptop", 1500000L, 10);

        when(productService.getProduct(1L)).thenReturn(mockProduct);

        String responseBody = mockMvc.perform(get("/product/1") // GET 요청
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK 기대
                .andExpect(jsonPath("$.id").value(1)) // 상품 ID 확인
                .andExpect(jsonPath("$.productName").value("Laptop")) // 상품명 확인
                .andExpect(jsonPath("$.price").value(1500000)) // 가격 확인
                .andExpect(jsonPath("$.stock").value(10)) // 재고 확인
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println("응답 JSON: " + responseBody);
    }


}
