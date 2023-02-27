package com.donations.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Setting;
import com.donations.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTests {
	@Autowired
	private SettingRepository repository;

	@Test
	public void testCreateGeneralSettings() {
		Setting siteName = new Setting("SITE_NAME", "Donations", SettingCategory.GENERAL);
		Setting siteLogo = new Setting("SITE_LOGO", "DonationLogo.jpg", SettingCategory.GENERAL);
		Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2921 Donations Ltd.", SettingCategory.GENERAL);
		Setting currencyId = new Setting("CURRENCY_ID", "11", SettingCategory.CURRENCY);
		Setting symbol = new Setting("CURRENCY_SYMBOL", "đ", SettingCategory.CURRENCY);
		Setting symbolPossition = new Setting("CURRENCY_SYMBOL_POSITION", "after", SettingCategory.CURRENCY);
		Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
		Setting decimalDigits = new Setting("DECIMAL_DIGITS", "3", SettingCategory.CURRENCY);
		Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);
		repository.saveAll(List.of(siteName, siteLogo, copyright, currencyId, symbol, symbolPossition, decimalDigits,
				decimalPointType, thousandsPointType));
		Iterable<Setting> iterable = repository.findAll();
		assertThat(iterable).size().isGreaterThan(0);
	}

	@Test
	public void testListSettingsByCategory() {
		List<Setting> settings = repository.findByCategory(SettingCategory.GENERAL);

		settings.forEach(System.out::println);
	}
}
