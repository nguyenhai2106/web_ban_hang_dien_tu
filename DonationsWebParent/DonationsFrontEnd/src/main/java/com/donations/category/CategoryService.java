package com.donations.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.common.entity.Category;

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository repository;

	public List<Category> listNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();

		List<Category> listEnabledCategories = repository.findAllEnabled();

		listNoChildrenCategories = listEnabledCategories.stream()
				.filter(category -> category.getChildrens().size() == 0 || category == null)
				.collect(Collectors.toList());
		return listNoChildrenCategories;
	}

	public Category getCategory(String alias) {
		return repository.findByAliasEnabled(alias);
	}

	public List<Category> getCategoryParents(Category category) {
		List<Category> listParents = new ArrayList<>();
		Category parent = category.getParent();
		while (parent != null) {
			listParents.add(0, parent);
			parent = parent.getParent();
		}
		listParents.add(category);
		return listParents;
	}
}
