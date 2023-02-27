package com.donations.admin.setting.country;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.donations.common.entity.Country;

@RestController
public class CountryRestCountroller {
	@Autowired
	private CountryRepository repository;

	@GetMapping("/countries/list")
	public List<Country> listAll() {
		return repository.findAllByOrderByNameAsc();
	}

	@PostMapping("/countries/save")
	public String save(@RequestBody Country country) {
		Country savedCountry = repository.save(country);
		return String.valueOf(savedCountry.getId());
	}

	@DeleteMapping("countries/delete/{id}")
	public void delete(@PathVariable("id") Integer id) {
		repository.deleteById(id);
	}
}
