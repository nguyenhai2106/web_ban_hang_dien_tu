package com.donations.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	@Autowired
	private CategoryRepository repository;

	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = repository.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}

//	@Test
//	public void testCreateSubCategory() {
//		Category parent = new Category(4);
//		Category smartphone = new Category("Gaming Laptops", parent);
//		Category savedCategory = repository.save(smartphone);
//		assertThat(savedCategory.getId()).isGreaterThan(0);
//	}

	@Test
	public void testGetCategory() {
		Category category = repository.findById(2).get();
		System.out.println(category.getName());
		category.getChildrens().stream().forEach(c -> {
			System.out.println(c.getName());
		});
	}

	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories = repository.findAll();
		for (Category category : categories) {
			if (category.getParent() == null) {
				System.out.println(category.getName());
				category.getChildrens().stream().forEach(c -> {
					System.out.println("--" + c.getName());
					printChildent(c, 1);
				});
			}
		}
	}

	@Test
	public void printChildent(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		for (Category category : parent.getChildrens()) {
			for (int i = 0; i < newSubLevel; i++) {
				System.out.print("--");
			}
			System.out.println(category.getName());
		}
	}

	@Test
	public void testListRooCategories() {
		List<Category> rootCategories = repository.findRootCategories(Sort.by("name").ascending());
		rootCategories.stream().forEach(cat -> {
			System.out.println(cat);
		});
	}

	@Test
	public void testFindByName() {
		String nameString = "Electronics1";
		Category listCategories = repository.findByName(nameString);
		assertThat(listCategories.getName()).isEqualTo(nameString);
	}
}
