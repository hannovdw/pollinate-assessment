package com.pollinate.challenge.service;

import com.pollinate.challenge.controller.dto.OrderRequest;
import com.pollinate.challenge.controller.dto.OrderResponse;
import com.pollinate.challenge.domain.Order;
import com.pollinate.challenge.domain.Product;
import com.pollinate.challenge.exception.ResourceNotFoundException;
import com.pollinate.challenge.repository.OrderRepository;
import com.pollinate.challenge.repository.ProductRepository;
import com.pollinate.challenge.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;
  @Mock private ProductRepository productRepository;
  @InjectMocks private OrderServiceImpl orderService;

  @Test
  @DisplayName("Should successfully create order and calculate correct total")
  void createOrder_Success() {
    // Arrange
    Product p1 = Product.builder().id(1L).name("Item A").price(new BigDecimal("10.50")).build();
    Product p2 = Product.builder().id(2L).name("Item B").price(new BigDecimal("20.00")).build();
    OrderRequest request = new OrderRequest(List.of(1L, 2L));

    Order savedOrder = Order.builder()
        .id(100L)
        .products(List.of(p1, p2))
        .totalPrice(new BigDecimal("30.50"))
        .createdAt(LocalDateTime.now())
        .build();

    when(productRepository.findAllById(any())).thenReturn(List.of(p1, p2));
    when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

    // Act
    OrderResponse response = orderService.createOrder(request);

    // Assert
    assertNotNull(response);
    assertEquals(100L, response.getId());
    assertEquals(new BigDecimal("30.50"), response.getTotalPrice());
    assertEquals(2, response.getProducts().size());
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when some product IDs are missing")
  void createOrder_PartialProductsNotFound() {
    // Arrange: Request 2 items, but DB only finds 1
    OrderRequest request = new OrderRequest(List.of(1L, 99L));
    Product p1 = Product.builder().id(1L).price(BigDecimal.TEN).build();

    when(productRepository.findAllById(any())).thenReturn(List.of(p1));

    // Act & Assert
    ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
        () -> orderService.createOrder(request));

    assertTrue(ex.getMessage().contains("99")); // Best practice: verify error message contains the offending ID
    verify(orderRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should find order by ID and map correctly")
  void getOrderById_Success() {
    Order order = Order.builder().id(1L).totalPrice(BigDecimal.TEN).products(List.of()).build();
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    OrderResponse response = orderService.getOrderById(1L);

    assertNotNull(response);
    assertEquals(1L, response.getId());
  }

  @Test
  @DisplayName("Should throw exception when finding non-existent order")
  void getOrderById_NotFound() {
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
  }
}
