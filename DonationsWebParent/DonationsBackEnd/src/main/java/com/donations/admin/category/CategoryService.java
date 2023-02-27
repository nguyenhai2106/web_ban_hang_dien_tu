package com.donations.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.donations.common.entity.Category;
import com.donations.common.exception.CategoryNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryService {
	public static final int ROOT_CATEGORIES_PER_PAGE = 4;
	@Autowired
	private CategoryRepository repository;

	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();

		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));

			Set<Category> children = sortSubCategories(rootCategory.getChildrens(), sortDir);

			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));

				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}

		return hierarchicalCategories;
	}

	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel,
			String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildrens(), sortDir);
		int newSubLevel = subLevel + 1;

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();

			hierarchicalCategories.add(Category.copyFull(subCategory, name));

			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		}

	}

	public List<Category> listByPage(CategoryPageInfo categoryPageInfo, int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");

		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}

		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

		Page<Category> categoriesPage = null;

		if (keyword != null && !keyword.isEmpty()) {
			categoriesPage = repository.searchCategories(keyword, pageable);
		} else {
			categoriesPage = repository.findRootCategories(pageable);
		}

		List<Category> rootCategories = categoriesPage.getContent();

		categoryPageInfo.setTotalElements(categoriesPage.getTotalElements());

		categoryPageInfo.setTotalPages(categoriesPage.getTotalPages());

		if (keyword != null && !keyword.isEmpty()) {
			List<Category> searchCategories = categoriesPage.getContent();
			for (Category category : searchCategories) {
				category.setHasChildren(category.getChildrens().size() > 0);
			}
			return searchCategories;
		} else {
			return listHierarchicalCategories(rootCategories, sortDir);
		}
	}

	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesUsedInForm = new ArrayList<Category>();
		Iterable<Category> categories = repository.findRootCategories(Sort.by("name").ascending());

		for (Category category : categories) {
			if (category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				Set<Category> children = sortSubCategories(category.getChildrens());
				children.stream().forEach(c -> {
					categoriesUsedInForm.add(Category.copyIdAndName(c.getId(), "--" + c.getName()));
					listSubCategoriesUsedInForm(c, 1, categoriesUsedInForm);
				});
			}
		}
		return categoriesUsedInForm;
	}

	public void listSubCategoriesUsedInForm(Category parent, int subLevel, List<Category> categories) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildrens());
		for (Category category : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += category.getName();
			categories.add(Category.copyIdAndName(category.getId(), name));
			listSubCategoriesUsedInForm(category, newSubLevel, categories);
		}
	}

	public Category save(Category category) {
		Category parent = category.getParent();
		if (parent != null) {
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId() + "-");
			category.setAllParentIDs(allParentIds);
		}
		return repository.save(category);
	}

	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}
	}

	public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = repository.countById(id);
		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not find any user with ID " + id);
		}
		repository.deleteById(id);
	}

	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		Category categoryByName = repository.findByName(name);
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = repository.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			Category categoryByAlias = repository.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		return "OK";
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category cat1, Category cat2) {
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});

		sortedChildren.addAll(children);

		return sortedChildren;
	}

	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}

	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repository.updateEnabledStatus(id, enabled);
	}
}
