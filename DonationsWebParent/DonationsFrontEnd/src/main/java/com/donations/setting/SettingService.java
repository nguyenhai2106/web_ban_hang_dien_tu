package com.donations.setting;

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

	public List<Setting> getSenGeneralSettings() {
		return repository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}

}
