package com.pollinate.challenge.controller;

import com.pollinate.challenge.controller.dto.ProductRequest;
import com.pollinate.challenge.controller.dto.ProductResponse;
import com.pollinate.challenge.service.ProductService;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Endpoints for creating and retrieving products")
public class ProductController {

  private final ProductService productService;

  @PostMapping
  @Operation(summary = "Create a new product")
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
    log.info("REST request to create product: {}", request.getName());
    return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get product by ID")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @GetMapping
  @Operation(summary = "List all products")
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    return ResponseEntity.ok(productService.getAllProducts());
  }
}