package com.donations.admin;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.donations.admin.security.DonationsUserDetails;

@Controller
public class MainController {
	@GetMapping("")
	public String viewHomePage( Authentication authentication) {
		DonationsUserDetails userDetails = (DonationsUserDetails) authentication.getPrincipal();
		System.out.println(userDetails.getFullName());
		return "index";
	}
	@GetMapping("/login")
	public String viewLoginPage() {
		return "login";
	}
}
