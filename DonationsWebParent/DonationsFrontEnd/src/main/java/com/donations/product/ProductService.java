package com.donations.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.donations.common.entity.Product;
import com.donations.common.exception.ProductNotFoundException;

@Service
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 4;
	public static final int SEARCH_RESULTS_PER_PAGE = 4;
	@Autowired
	private ProductRepository repository;

	public Page<Product> listByCategory(int pageNum, Integer categoryId) {
		String categoryIdMatch = "-" + categoryId + "";
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		Page<Product> listBycategoryPage = repository.listByCategory(categoryId, categoryIdMatch, pageable);
		return listBycategoryPage;
	}

	public Product getProductByAlias(String alias) throws ProductNotFoundException {
		Product product = repository.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException("Could not find any product with alias " + alias);
		}
		return product;
	}

	public Page<Product> search(String keyword, int pageNum) {
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return repository.search(keyword, pageable);
	}
}
