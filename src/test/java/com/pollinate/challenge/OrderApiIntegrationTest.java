package com.pollinate.challenge;

import com.pollinate.challenge.domain.Product;
import com.pollinate.challenge.repository.OrderRepository;
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
@Transactional // Rolls back DB changes after each test to ensure 100% isolation
class OrderApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderRepository orderRepository;

  private Long savedProductId;

  @BeforeEach
  void setUp() {
    // Clear state to avoid OptimisticLocking/Duplicate key issues
    orderRepository.deleteAll();
    productRepository.deleteAll();

    // Seed product without forcing an ID to avoid LockingFailure
    Product product = Product.builder()
        .name("Standard Widget")
        .price(new BigDecimal("25.50"))
        .build();

    Product savedProduct = productRepository.save(product);
    this.savedProductId = savedProduct.getId();
  }

  @Test
  @WithMockUser(username = "admin")
  @DisplayName("Integration: Successfully create an order and verify price calculation")
  void createOrder_Success() throws Exception {
    // Use the dynamically generated ID from the setUp
    String jsonRequest = String.format("{\"productIds\": [%d]}", savedProductId);

    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.totalPrice").value(25.50))
        .andExpect(jsonPath("$.products[0].name").value("Standard Widget"));
  }

  @Test
  @WithMockUser(username = "admin")
  @DisplayName("Integration: Return 404 when product IDs do not exist")
  void createOrder_ProductNotFound() throws Exception {
    // ID 99999 should not exist in the fresh H2 instance
    String jsonRequest = "{\"productIds\": [99999]}";

    mockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  @DisplayName("Security: Reject access to order list when unauthenticated")
  void getAllOrders_Unauthenticated_Returns401() throws Exception {
    mockMvc.perform(get("/api/orders"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "admin")
  @DisplayName("Integration: Successfully fetch all orders")
  void getAllOrders_Authenticated_Returns200() throws Exception {
    mockMvc.perform(get("/api/orders"))
        .andExpect(status().isOk());
  }
}

