package com.donations.admin.user.controller;

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
import com.donations.admin.security.DonationsUserDetails;
import com.donations.admin.user.UserNotFoundException;
import com.donations.admin.user.UserService;
import com.donations.admin.user.exporter.UserCsvExporter;
import com.donations.admin.user.exporter.UserExcelExporter;
import com.donations.admin.user.exporter.UserPDFExporter;
import com.donations.common.entity.Role;
import com.donations.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "id", "asc", null);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
		if (pageNum < 1) {
			pageNum = 1;
		}
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("endCount", endCount);
		model.addAttribute("startCount", startCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model, Authentication authentication) {
		List<Role> listRoles = service.listRoles();
		User user = new User();
		DonationsUserDetails userDetails = (DonationsUserDetails) authentication.getPrincipal();
		model.addAttribute("fullname", userDetails.getFullName());
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
//		System.out.println(user);
//		System.out.println(multipartFile.getOriginalFilename());
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty()) {
				user.setPhotos(null);
			}
			service.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully!");

		return getRedirectURLToAffectedUser(user);
	}

	private String getRedirectURLToAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}

	@GetMapping("users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			model.addAttribute("user", user);
			model.addAttribute("pageTilte", "Edit User (ID: " + id + ")");
			model.addAttribute("listRoles", listRoles);
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}

	}

	@GetMapping("users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			service.delete(id);
			String userPhotosDir = "user-photos/" + id;
			FileUploadUtil.removeDir(userPhotosDir);
			redirectAttributes.addFlashAttribute("message",
					"The user with ID " + id + " has been deleted successfully!");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";
	}

	@GetMapping("users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled, @Param("pageNum") String pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword,
			RedirectAttributes redirectAttributes) throws UserNotFoundException {
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "Enabled" : "Disabled";
		String message = "The user ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		if (keyword == null || keyword.isEmpty() || keyword.equals("null")) {
			keyword = "";
		}
		redirectAttributes.addFlashAttribute("keyword", keyword);
		return "redirect:/users/page/" + pageNum + "?sortField=" + sortField + "&sortDir=" + sortDir + "&keyword="
		+ keyword;

	}

	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}

	@GetMapping("/users/export/pdf")
	public void exportToPEF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserPDFExporter exporter = new UserPDFExporter();
		exporter.export(listUsers, response);
	}
}
