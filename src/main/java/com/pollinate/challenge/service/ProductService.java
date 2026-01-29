package com.pollinate.challenge.service;

import com.pollinate.challenge.controller.dto.ProductRequest;
import com.pollinate.challenge.controller.dto.ProductResponse;
import java.util.List;

public interface ProductService {
  ProductResponse createProduct(ProductRequest request);
  ProductResponse getProductById(Long id);
  List<ProductResponse> getAllProducts();
}
