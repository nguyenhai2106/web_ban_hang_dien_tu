package com.donations.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.donations.admin.user.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Bean
	public UserDetailsService userDetailsService() {
		return new DonationsUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.userDetailsService(userDetailsService())
						.authorizeHttpRequests()
						.requestMatchers("/users/**").hasAuthority("Admin")
						
						.requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
						
						.requestMatchers("/products", "/products/", "/products/detail/**", "products/page/**")
							.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
							
						.requestMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
						
						.requestMatchers("/products/edit/**", "/products/save", "/products/check_unique").hasAnyAuthority("Admin", "Editor", "Salesperson")
						
						.requestMatchers( "/products/**").hasAnyAuthority("Admin", "Editor")
						
						.anyRequest().authenticated()
						.and()
						.formLogin()
							.loginPage("/login")
							.usernameParameter("email")
							.loginProcessingUrl("/login")
							.defaultSuccessUrl("/", true).permitAll()
							.and().logout().permitAll()
							.and().rememberMe()
								.key("SWP490x_01_Assignment4_haintfx17393")
								.tokenValiditySeconds(7 * 24 * 60 * 60);
		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer(HttpSecurity http) {
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**",
				"/fontawesome-free-6.2.1-web/**", "/style.css");
	}

}
