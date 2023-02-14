package com.donations.admin.product;

import java.io.IOException;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.donations.admin.FileUploadUtil;
import com.donations.admin.brand.BrandService;
import com.donations.admin.category.CategoryService;
import com.donations.admin.security.DonationsUserDetails;
import com.donations.common.entity.Brand;
import com.donations.common.entity.Category;
import com.donations.common.entity.Product;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "id", "asc", null, 0);
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
		model.addAttribute("numberOfExtraImages", 0);
		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipartFile,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal DonationsUserDetails loggedUser) throws IOException {

		if (loggedUser.hasRole("Salesperson")) {
			productService.saveProductPrice(product);
			System.out.println("Save by Salesperson!");
			redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
			return "redirect:/products";
		}
		ProductSaveHelper.setMainImageName(product, mainImageMultipartFile);

		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);

		ProductSaveHelper.setNewExtraImageNames(product, extraImageMultipartFile);

		ProductSaveHelper.setProductDetails(product, detailNames, detailValues, detailIDs);

		Product savedProduct = productService.save(product);

		ProductSaveHelper.saveUploadedImages(mainImageMultipartFile, extraImageMultipartFile, savedProduct);

		ProductSaveHelper.deleteExtraImageWeredRemoveOnForm(product);

		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
		return "redirect:/products";
	}

//	private void deleteExtraImageWeredRemoveOnForm(Product product) {
//		String extraImageDir = "../product-images/" + product.getId() + "/extras";
//		Path dirPath = Paths.get(extraImageDir);
//		try {
//			Files.list(dirPath).forEach(file -> {
//				String fileName = file.toFile().getName();
//				if (!product.containsImageName(fileName)) {
//					try {
//						Files.delete(file);
//						LOGGER.info("Deleted extra image: " + fileName);
//					} catch (IOException e) {
//						LOGGER.error("Could not delete extra image: " + fileName);
//					}
//				}
//			});
//		} catch (IOException e) {
//			LOGGER.error("Could not list directory: " + dirPath);
//		}
//	}
//
//	private void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
//
//		if (imageIDs == null || imageIDs.length == 0) {
//			return;
//		}
//
//		Set<ProductImage> images = new HashSet<>();
//
//		for (int i = 0; i < imageIDs.length; i++) {
//			Integer id = Integer.parseInt(imageIDs[i]);
//			String name = imageNames[i];
//			images.add(new ProductImage(id, name, product));
//		}
//
//		product.setImages(images);
//	}
//
//	private void setProductDetails(Product product, String[] detailNames, String[] detailValues, String[] detailIDs) {
//		if (detailNames == null || detailNames.length == 0) {
//			return;
//		}
//		for (int i = 0; i < detailNames.length; i++) {
//			String name = detailNames[i];
//			String value = detailValues[i];
//			Integer id = Integer.parseInt(detailIDs[i]);
//			if (id != 0) {
//				product.addDetail(id, name, value);
//			} else if (!name.isEmpty() && !value.isEmpty()) {
//				product.addDetail(name, value);
//			}
//		}
//	}
//
//	private void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipartFile,
//			Product savedProduct) throws IOException {
//		if (!mainImageMultipartFile.isEmpty()) {
//			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
//			String uploadDir = "../product-images/" + savedProduct.getId();
//			FileUploadUtil.cleanDir(uploadDir);
//			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
//		}
//		if (extraImageMultipartFile.length > 0) {
//			String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
//			for (MultipartFile multipartFile : extraImageMultipartFile) {
//				if (multipartFile.isEmpty())
//					continue;
//				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
//			}
//		}
//
//	}
//
//	private void setNewExtraImageNames(Product product, MultipartFile[] extraImageMultipartFile) {
//		if (extraImageMultipartFile.length > 0) {
//			for (MultipartFile multipartFile : extraImageMultipartFile) {
//				if (!multipartFile.isEmpty()) {
//					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//					if (!product.containsImageName(fileName)) {
//						product.addExtraImage(fileName);
//					}
//				}
//			}
//		}
//
//	}
//
//	public void setMainImageName(Product product, MultipartFile multipartFile) {
//		if (!multipartFile.isEmpty()) {
//			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//			product.setMainImage(fileName);
//		}
//	}

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
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productMainImagesDir = "../product-images/" + id;
			FileUploadUtil.removeDir(productMainImagesDir);
			FileUploadUtil.removeDir(productExtraImagesDir);
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/products";
	}

	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Product product = productService.getById(id);

			List<Brand> listBrands = brandService.listAll();

			model.addAttribute("product", product);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("listBrands", listBrands);

			Integer numberOfExtraImages = product.getImages().size();
			model.addAttribute("numberOfExtraImages", numberOfExtraImages);
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Product product = productService.getById(id);

			model.addAttribute("product", product);

			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/products";
		}
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDirt") String sortDir, @Param("keyword") String keyword,
			@Param("categoryId") Integer categoryId) {
		if (pageNum < 1) {
			pageNum = 1;
		}
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		if (categoryId != null) {
			model.addAttribute("categoryId", categoryId);
		}
		return "products/products";
	}
}
