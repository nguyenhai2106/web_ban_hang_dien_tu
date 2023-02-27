package com.donations.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donations.common.entity.Setting;
import com.donations.common.entity.SettingCategory;

public interface SettingRepository extends JpaRepository<Setting, String> {
	public List<Setting> findByCategory(SettingCategory category);
}
