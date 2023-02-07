package com.donations.admin.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Brand;
import com.donations.common.entity.Category;
import com.donations.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {
	@Autowired
	private ProductRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 11);
		Category category = entityManager.find(Category.class, 17);

		Product product = new Product();
		product.setName("Surface Laptop Go (THH-00035)(i5 1035G1/8GB RAM/128GB SSD/12.4 Cảm ứng/Win 10/Vàng)");
		product.setShortDescription(
				"Surface Laptop Go (THH-00035)(i5 1035G1/8GB RAM/128GB SSD/12.4 Cảm ứng/Win 10/Vàng)");
		product.setFullDescription(
				"Surface Laptop Go được thiết kế với vỏ nhôm. Tuy nhiên, vì là mẫu laptop hướng đến phân khúc tầm trung nên Laptop Go chỉ có lớp vỏ trên (chứa mặt màn hình) được làm bằng nhôm, còn lại ở phần dưới (chứa bàn phím và các linh kiện) là được làm bằng nhựa tổng hợp polycarbonate, sợi thủy tinh và 30% hàm lượng chất tái chế nhằm bảo vệ môi trường. ");
		product.setBrand(brand);
		product.setAlias("surface_laptop_go_thh_00035");
		product.setCategory(category);
		product.setPrice(39000000);
		product.setCreateTime(new Date());
		product.setUpdateTimeDate(new Date());
		Product savedProduct = repository.save(product);
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllProducts() {
		Iterable<Product> productsIterable = repository.findAll();
		productsIterable.forEach(System.out::println);
	}

	@Test
	public void testGetProduct() {
		Integer idInteger = 1;
		Product product = repository.findById(idInteger).get();
		System.out.println(product);
		assertThat(product).isNotNull();
	}

	@Test
	public void testUpdateProduct() {
		Product product = repository.findById(1).get();
		product.setPrice(39000000);
		repository.save(product);
		Product updatedProduct = entityManager.find(Product.class, 1);
		System.out.println(updatedProduct.getPrice());
		assertThat(updatedProduct.getPrice()).isEqualTo(39000000);

	}

	@Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repository.findById(productId).get();
		product.setMainImage("main-image.png");
		product.addExtraImage("extra-image-0.jpg");
		product.addExtraImage("extra-image-1.jpg");
		product.addExtraImage("extra-image-2.jpg");
		Product savedProduct = repository.save(product);
		System.out.println(savedProduct.getMainImage());
		assertThat(savedProduct.getMainImage()).isEqualTo("main-image.png");
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}

}
