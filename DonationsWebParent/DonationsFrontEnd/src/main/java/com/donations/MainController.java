package com.donations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.donations.category.CategoryService;
import com.donations.common.entity.Category;

@Controller
public class MainController {
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/")
	public String viewHomePage(Model model) {
		System.out.println("Controller");
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);
		return "index";
	}
}
