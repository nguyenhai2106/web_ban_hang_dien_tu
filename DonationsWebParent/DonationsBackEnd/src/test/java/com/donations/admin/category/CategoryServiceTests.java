package com.donations.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.donations.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	@MockBean
	private CategoryRepository repository;
	
	@InjectMocks
	private CategoryService service;
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateName() {
		Integer id = null;
		String name = "Electronics";
		String alias = "electronics";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(category);
		Mockito.when(repository.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName"); 
		//assertThat(result).isEqualTo("DuplicateAlias"); 
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnDuplicateAlias() {
		Integer id = null;
		String name = "Alectronics";
		String alias = "electronics";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		Mockito.when(repository.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias"); 
	}
	
	@Test
	public void testCheckUniqueInNewModeReturnOK() {
		Integer id = null;
		String name = "Alectronics";
		String alias = "alectronics";
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		Mockito.when(repository.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("OK"); 
	}
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateName() {
		Integer id = 1;
		String name = "Electronics";
		String alias = "electronics";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(category);
		Mockito.when(repository.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateName"); 
		//assertThat(result).isEqualTo("DuplicateAlias"); 
	}
	
	
	@Test
	public void testCheckUniqueInEditModeReturnDuplicateAlias() {
		Integer id = 1;
		String name = "Alectronics";
		String alias = "electronics";
		
		Category category = new Category(2, name, alias);
		
		Mockito.when(repository.findByName(name)).thenReturn(null);
		Mockito.when(repository.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias);
		
		assertThat(result).isEqualTo("DuplicateAlias"); 
	}
	
}
