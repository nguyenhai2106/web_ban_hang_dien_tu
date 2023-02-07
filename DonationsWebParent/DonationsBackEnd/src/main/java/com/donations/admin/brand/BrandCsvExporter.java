package com.donations.admin.brand;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.donations.admin.AbstractExporter;
import com.donations.common.entity.Brand;

import jakarta.servlet.http.HttpServletResponse;

public class BrandCsvExporter extends AbstractExporter {
	public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv; charset=UTF-8", ".csv", "brand_");
		response.setCharacterEncoding("UTF-8");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = { "Brand ID", "Brand Name", "Categories" };
		String[] fieldMapping = { "id", "name", "categories" };
		csvWriter.writeHeader(csvHeader);
		for (Brand brand : listBrands) {
			csvWriter.write(brand, fieldMapping);
		}
		csvWriter.close();
	}
}
