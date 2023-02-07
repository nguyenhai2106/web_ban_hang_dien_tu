package com.donations.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.donations.common.entity.Product;

@Service
@Transactional
public class ProductService {
	@Autowired
	private ProductRepository repository;
	
	public List<Product> listAll() {
		return repository.findAll();
	}
}
