package com.pollinate.challenge.repository;

import com.pollinate.challenge.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
