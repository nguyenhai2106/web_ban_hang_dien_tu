package com.donations.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.donations.common.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryRepositoryTests {
	@Autowired
	private CategoryRepository repository;

	@Test
	public void testListEnabledCategories() {
		List<Category> categories = repository.findAllEnabled();
		categories.stream().forEach(c -> {
			System.out.println(c + " " + c.isEnabled());
		});
	}

	@Test
	public void testFindCategoryByAlias() {
		String alias = "desktop_computers";
		Category category = repository.findByAliasEnabled(alias);
		assertThat(category).isNotNull();
	}
}
