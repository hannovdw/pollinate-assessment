package com.pollinate.challenge.service.impl;

import com.pollinate.challenge.controller.dto.OrderRequest;
import com.pollinate.challenge.controller.dto.OrderResponse;
import com.pollinate.challenge.controller.dto.ProductResponse;
import com.pollinate.challenge.domain.Order;
import com.pollinate.challenge.domain.Product;
import com.pollinate.challenge.exception.ResourceNotFoundException;
import com.pollinate.challenge.repository.OrderRepository;
import com.pollinate.challenge.repository.ProductRepository;
import com.pollinate.challenge.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;

  @Transactional
  public OrderResponse createOrder(OrderRequest request) {
    log.info("Attempting to create order for products: {}", request.getProductIds());

    // 1. Handle potential duplicates in request (Best Practice: Use a Set)
    Set<Long> uniqueIds = Set.copyOf(request.getProductIds());

    // 2. Business Rule: All product IDs must exist
    List<Product> products = productRepository.findAllById(uniqueIds);

    if (products.size() != uniqueIds.size()) {
      Set<Long> foundIds = products.stream().map(Product::getId).collect(Collectors.toSet());
      List<Long> missingIds = uniqueIds.stream().filter(id -> !foundIds.contains(id)).toList();

      log.error("Order rejected: Missing product IDs: {}", missingIds);
      throw new ResourceNotFoundException("Invalid Order: Product(s) not found: " + missingIds);
    }

    // 3. Business Rule: Total price must be calculated
    BigDecimal total = products.stream()
        .map(Product::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // 4. Persistence
    Order order = Order.builder()
        .products(products)
        .totalPrice(total)
        .build();

    Order savedOrder = orderRepository.save(order);
    log.info("Order created successfully with ID: {} and Total: {}", savedOrder.getId(), total);

    return mapToResponse(savedOrder);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> getAllOrders() {
    return orderRepository.findAll().stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrderById(Long id) {
    return orderRepository.findById(id)
        .map(this::mapToResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
  }

  private OrderResponse mapToResponse(Order order) {
    return OrderResponse.builder()
        .id(order.getId())
        .totalPrice(order.getTotalPrice())
        .createdAt(order.getCreatedAt())
        .products(order.getProducts().stream()
            .map(p -> ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getPrice())
                .build())
            .toList())
        .build();
  }
}
