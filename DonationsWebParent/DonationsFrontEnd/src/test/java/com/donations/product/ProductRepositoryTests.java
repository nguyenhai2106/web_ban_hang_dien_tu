package com.donations.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.donations.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTests {
	@Autowired
	private ProductRepository repository;

	@Test
	public void findProductByAlias() {
		String alias = "laptop_acer_gaming_predator_helios_300_ph315-55-76kg";
		Product productByAlias = repository.findByAlias(alias);
		System.out.println(productByAlias);
		assertThat(productByAlias).isNotNull();
	}

	@Test
	public void searchProduct() {
		String keyword = "apple";
		Pageable pageable = PageRequest.of(2, 4);
		Page<Product> searchedProduct = repository.search(keyword, pageable);
		List<Product> list = searchedProduct.getContent();
		for (Product product : list) {
			System.out.println(product);
		}

	}
}
