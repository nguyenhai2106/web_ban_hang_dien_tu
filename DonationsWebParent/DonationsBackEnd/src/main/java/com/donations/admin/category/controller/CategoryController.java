package com.donations.admin.category.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.donations.admin.FileUploadUtil;
import com.donations.admin.category.CategoryCsvExporter;
import com.donations.admin.category.CategoryPageInfo;
import com.donations.admin.category.CategoryService;
import com.donations.common.entity.Category;
import com.donations.common.exception.CategoryNotFoundException;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;

	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, sortDir, model, null);
	}

	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, @Param("sortDir") String sortDir, Model model,
			@Param("keyword") String keyword) {
		if (pageNum < 1) {
			pageNum = 1;
		}

		if (sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

		List<Category> listCategories = service.listByPage(categoryPageInfo, pageNum, sortDir, keyword);

		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;

		if (endCount > categoryPageInfo.getTotalElements()) {
			endCount = categoryPageInfo.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("endCount", endCount);
		model.addAttribute("startCount", startCount);
		model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
		model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("sortField", "name");
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", reverseSortDir);
		return "categories/categories";
	}

	@GetMapping("/categories/new")
	public String newUser(Model model, Authentication authentication) {
		Category category = new Category();
		category.setEnabled(false);
		List<Category> listCategories = service.listCategoriesUsedInForm();
		model.addAttribute("category", category);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");
		return "categories/category_form";
	}

	@PostMapping("/categories/save")
	public String saveCategory(Category category, RedirectAttributes redirectAttributes,
			@RequestParam("imageCategory") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = service.save(category);
			String uploadDir = "../category-images/" + savedCategory.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
		service.save(category);
		redirectAttributes.addFlashAttribute("message", "The category has been save!");
		return "redirect:/categories";
	}

//	private String getRedirectURLToAffectedUser(Category category) {
//		String firstPartOfEmail = user.getEmail().split("@")[0];
//		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
//	}

	@GetMapping("categories/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listCategoriesUsedInForm();
			model.addAttribute("category", category);
			model.addAttribute("pageTilte", "Edit Category (ID: " + id + ")");
			model.addAttribute("listCategories", listCategories);
			return "categories/category_form";
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}

	}

	@GetMapping("categories/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			service.delete(id);
			String categoryDir = "../category-images/" + id;
			FileUploadUtil.removeDir(categoryDir);
			redirectAttributes.addFlashAttribute("message",
					"The user with ID " + id + " has been deleted successfully!");
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/categories";
	}

	@GetMapping("categories/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled, RedirectAttributes redirectAttributes,
			@Param("pageNum") String pageNum, @Param("sortDir") String sortDir, @Param("keyword") String keyword)
			throws CategoryNotFoundException {
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? "Enabled" : "Disabled";
		String message = "The user ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		if (keyword == null || keyword.isEmpty() || keyword.equals("null")) {
			keyword = "";
		}
		return "redirect:/categories/page/" + pageNum + "?sortField=name" + "&sortDir=" + sortDir + "&keyword=" + keyword;
	}

	@GetMapping("/categories/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Category> categories = service.listCategoriesUsedInForm();
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(categories, response);
	}

}
