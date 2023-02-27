package com.donations.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.donations.common.entity.GeneralSettingBag;
import com.donations.common.entity.Setting;
import com.donations.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository repository;

	public List<Setting> listAllSettings() {
		return repository.findAll();
	}

	public GeneralSettingBag getSenGeneralSettingBags() {
		List<Setting> settings = new ArrayList<>();

		List<Setting> generalSettings = repository.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = repository.findByCategory(SettingCategory.CURRENCY);

		settings.addAll(generalSettings);
		settings.addAll(currencySettings);

		return new GeneralSettingBag(settings);
	}

	public void saveAll(Iterable<Setting> settings) {
		repository.saveAll(settings);
	}
}
