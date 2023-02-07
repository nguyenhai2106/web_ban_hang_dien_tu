package com.donations.admin.product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donations.common.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
