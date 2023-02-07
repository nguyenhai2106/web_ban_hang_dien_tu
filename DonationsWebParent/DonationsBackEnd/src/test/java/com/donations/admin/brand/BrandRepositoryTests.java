package com.donations.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Brand;
import com.donations.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {
	@Autowired
	private BrandRepository brandRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void testCreateNewBrandWithOneCategory() {
		Category category = testEntityManager.find(Category.class, 1);
		Set<Category> categories = new HashSet<>();
		categories.add(category);
		Brand brand = new Brand("AMD", "amdLogo.png");
		brand.setCategories(categories);
		Brand savedBrand = brandRepository.save(brand);
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewBrandWithManyCategory() {
		Category electronics = new Category(1);
		Category camera = new Category(2);
		Brand brand = new Brand("Sony", "sonyLogo.png");
		brand.getCategories().add(camera);
		brand.getCategories().add(electronics);
		Brand savedBrand = brandRepository.save(brand);
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllBrands() {
		Iterable<Brand> brandIterable = brandRepository.findAll();
		brandIterable.forEach(brand -> {
			System.out.println(brand);
		});
	}
	
	@Test
	public void testFindById() {
		Brand brand = brandRepository.findById(1).get();
		System.out.println(brand);
	}
	
	@Test
	public void testUpdateBrand() {
		Brand brand = brandRepository.findById(1).get();
		brand.setName("Camera Sony");
		Brand savedBrand = brandRepository.save(brand);
		System.out.println(savedBrand);
		assertThat(savedBrand.getName()).isEqualTo("Camera Sony");
	}
	
	@Test void testDeleteBrand() {
		Brand brand = brandRepository.findById(1).get();
		brandRepository.delete(brand);
		assertThat(brandRepository.findById(1).isEmpty());
	}

}
