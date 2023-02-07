package com.donations.admin.product;

import java.util.Date;
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

	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreateTime(new Date());
		}
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlia = product.getName().toLowerCase().replaceAll(" ", "_");
			product.setAlias(defaultAlia);
		} else {
			product.setAlias(product.getAlias().toLowerCase().replaceAll(" ", "_"));
		}
		Product savedProduct = repository.save(product);
		product.setUpdateTimeDate(new Date());
		return savedProduct;
	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repository.findByName(name);
		if (isCreatingNew) {
			if (productByName != null) {
				return "Duplicate";
			}
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}
		return "OK";
	}

	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repository.updateEnabledStatus(id, enabled);
	}

	public void delete(Integer id) throws ProductNotFoundException {
		Long countById = repository.countById(id);
		if (countById == null || countById == 0) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
		repository.deleteById(id);
	}
}
