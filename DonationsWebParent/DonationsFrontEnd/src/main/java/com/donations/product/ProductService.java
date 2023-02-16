package com.donations.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.donations.common.entity.Product;

@Service
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 4;
	@Autowired
	private ProductRepository repository;

	public Page<Product> listByCategory(int pageNum, Integer categoryId) {
		String categoryIdMatch = "-" + categoryId + "";
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		Page<Product> listBycategoryPage = repository.listByCategory(categoryId, categoryIdMatch, pageable);
		return listBycategoryPage;
	}
}
