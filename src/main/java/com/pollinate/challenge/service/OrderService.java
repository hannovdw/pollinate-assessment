package com.pollinate.challenge.service;

import com.pollinate.challenge.controller.dto.OrderRequest;
import com.pollinate.challenge.controller.dto.OrderResponse;
import java.util.List;

public interface OrderService{
  OrderResponse createOrder(OrderRequest request);
  List<OrderResponse> getAllOrders();
  OrderResponse getOrderById(Long id);
}
