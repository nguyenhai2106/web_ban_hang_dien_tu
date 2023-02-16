package com.donations.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.donations.category.CategoryService;
import com.donations.common.entity.Category;
import com.donations.common.entity.Product;

@Controller
public class ProductController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable(name = "category_alias") String alias, Model model) {
		return viewCategoryByPage(alias, 1, model);
	}

	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable(name = "category_alias") String alias,
			@PathVariable(name = "pageNum") int pageNum, Model model) {
		Category category = categoryService.getCategory(alias);
		if (category == null) {
			return "error/404";
		}
		List<Category> listCategoryParents = categoryService.getCategoryParents(category);

		Page<Product> productsPage = productService.listByCategory(pageNum, category.getId());

		List<Product> listProducts = productsPage.getContent();

		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > productsPage.getTotalElements()) {
			endCount = productsPage.getTotalElements();
		}

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("totalPages", productsPage.getTotalPages());
		model.addAttribute("totalItems", productsPage.getTotalElements());
		model.addAttribute("category", category);

		model.addAttribute("pageTitle", category.getName());
		model.addAttribute("listCategoryParents", listCategoryParents);
		return "product_by_category";
	}
}
