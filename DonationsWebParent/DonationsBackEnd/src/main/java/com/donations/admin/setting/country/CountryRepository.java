package com.donations.admin.setting.country;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donations.common.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
	public List<Country> findAllByOrderByNameAsc();
}
