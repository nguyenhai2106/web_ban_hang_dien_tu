package com.donations.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
import com.donations.admin.brand.BrandService;
import com.donations.admin.category.CategoryService;
import com.donations.common.entity.Brand;
import com.donations.common.entity.Category;
import com.donations.common.entity.Product;

import jakarta.mail.Multipart;
import jakarta.persistence.CascadeType;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();
		model.addAttribute("listProducts", listProducts);
		return "products/products";
	}

	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
			Product savedProduct = productService.save(product);
			String uploadDir = "../product-images/" + savedProduct.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			productService.save(product);
		}
		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
		return "redirect:/products";
	}

	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled, @Param("sortField") String sortField,
			@Param("sortDir") String sortDir, @Param("keyword") String keyword, RedirectAttributes redirectAttributes)
			throws ProductNotFoundException {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "Enabled" : "Disabled";
		String message = "This product ID " + id + " has been " + status + ".";
		redirectAttributes.addFlashAttribute("message", message);
		if (keyword == null || keyword.isEmpty() || keyword.equals("null")) {
			keyword = "";
		}
		redirectAttributes.addFlashAttribute("keyword", keyword);
		return "redirect:/products";
	}

	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}

}
