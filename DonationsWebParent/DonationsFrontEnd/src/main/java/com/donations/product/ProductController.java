package com.donations.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.donations.category.CategoryService;
import com.donations.common.entity.Category;
import com.donations.common.entity.Product;
import com.donations.common.exception.CategoryNotFoundException;
import com.donations.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable(name = "category_alias") String alias, Model model)
			throws CategoryNotFoundException {
		return viewCategoryByPage(alias, 1, model);
	}

	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable(name = "category_alias") String alias,
			@PathVariable(name = "pageNum") int pageNum, Model model) {
		Category category;
		try {
			category = categoryService.getCategory(alias);
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
			return "product/product_by_category";
		} catch (CategoryNotFoundException e) {
			return "error/404";
		}
	}

	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable(name = "product_alias") String alias, Model model) {
		try {
			Product product = productService.getProductByAlias(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());

			model.addAttribute("product", product);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("pageTitle", product.getName());
			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}

	@GetMapping("/search")
	public String searchFirstPage(@Param("keyword") String keyword, Model model) {
		return search(keyword, 1, model);
	}

	@GetMapping("/search/page/{pageNum}")
	public String search(@Param("keyword") String keyword, @PathVariable("pageNum") int pageNum, Model model) {
		Page<Product> productsPage = productService.search(keyword, pageNum);
		List<Product> listResults = productsPage.getContent();

		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > productsPage.getTotalElements()) {
			endCount = productsPage.getTotalElements();
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("listResults", listResults);
		model.addAttribute("totalPages", productsPage.getTotalPages());
		model.addAttribute("totalItems", productsPage.getTotalElements());
		model.addAttribute("pageTitle", "Search Results for '" + keyword + "'");
		return "product/search_result";
	}
}
