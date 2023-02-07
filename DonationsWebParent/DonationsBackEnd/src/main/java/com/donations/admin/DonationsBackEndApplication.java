package com.donations.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.Entity;

@SpringBootApplication
@EntityScan({ "com.donations.common.entity", "com.donations.admin.user" })
public class DonationsBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonationsBackEndApplication.class, args);
	}

}
