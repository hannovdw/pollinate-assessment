package com.pollinate.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pollinate.challenge.controller.dto.ProductRequest;
import com.pollinate.challenge.domain.Product;
import com.pollinate.challenge.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;

  // Manually instantiate to avoid UnsatisfiedDependencyException
  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule());

  private Long savedProductId;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    Product product = productRepository.save(Product.builder()
        .name("Existing Product")
        .price(new BigDecimal("50.00"))
        .build());
    this.savedProductId = product.getId();
  }

  @Test
  @WithMockUser(username = "admin")
  @DisplayName("Integration: Successfully create a product")
  void createProduct_Success() throws Exception {
    ProductRequest request = new ProductRequest("New Laptop", new BigDecimal("1200.00"));

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("New Laptop"))
        .andExpect(jsonPath("$.price").value(1200.00));
  }

  @Test
  @WithMockUser(username = "admin")
  @DisplayName("Integration: Get product by ID")
  void getProductById_Success() throws Exception {
    mockMvc.perform(get("/api/products/" + savedProductId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Existing Product"));
  }

  @Test
  @WithMockUser(username = "admin")
  @DisplayName("Integration: Return 404 for non-existent product")
  void getProductById_NotFound() throws Exception {
    mockMvc.perform(get("/api/products/9999"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Security: Reject product creation when unauthenticated")
  void createProduct_Unauthenticated_Returns401() throws Exception {
    ProductRequest request = new ProductRequest("Ghost Item", BigDecimal.TEN);

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "admin")
  @DisplayName("Integration: List all products")
  void getAllProducts_Success() throws Exception {
    mockMvc.perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1));
  }
}