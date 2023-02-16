package com.donations.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests().anyRequest().permitAll();
		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer(HttpSecurity http) {
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/style.css",
				"fontawesome-free-6.2.1-web/**");
	}
}
