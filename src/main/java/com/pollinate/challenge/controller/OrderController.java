package com.pollinate.challenge.controller;

import com.pollinate.challenge.controller.dto.OrderRequest;
import com.pollinate.challenge.controller.dto.OrderResponse;
import com.pollinate.challenge.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "Endpoints for creating and retrieving orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @Operation(summary = "Create a new order")
  public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
    log.info("REST request to create order for products: {}", request.getProductIds());
    return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get order by ID")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  @GetMapping
  @Operation(summary = "List all orders")
  public ResponseEntity<List<OrderResponse>> getAllOrders() {
    return ResponseEntity.ok(orderService.getAllOrders());
  }
}
