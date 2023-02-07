package com.donations.admin.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.donations.common.entity.Role;
import com.donations.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateUser() {

	}

	@Test
	public void testCreateUserWithOneRole() {
		Role roleAdminRole = entityManager.find(Role.class, 1);
		User userHaiNT = new User("nguyentrunghai2106@gmail.com", "123456", "Hải", "Nguyễn Trung");
		userHaiNT.addRole(roleAdminRole);
		User saveUser = repository.save(userHaiNT);
		assertThat(saveUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateUserWithTwoRole() {
		User userHaiNT = new User("haintfx17393@funix.edu.vn", "123456", "Hải", "Nguyễn Trung");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		userHaiNT.addRole(roleEditor);
		userHaiNT.addRole(roleAssistant);
		User saveUser = repository.save(userHaiNT);
		assertThat(saveUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testListAllUsers() {
		Iterable<User> users = repository.findAll();
		users.forEach(user -> System.out.println(user));
	}

	@Test
	public void testGetUserById() {
		User userHaiNT = repository.findById(1).get();
		System.out.println(userHaiNT);
		assertThat(userHaiNT).isNotNull();
	}

	@Test
	public void testUpdateUserDetails() {
		User userHaiNT = repository.findById(1).get();
		userHaiNT.setEnabled(true);
		userHaiNT.setEmail("nguyentrunghai2106@gmail.com");
		repository.save(userHaiNT);
	}

	@Test
	public void testUpdateUserRoles() {
		User userHaiNT = repository.findById(1).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		userHaiNT.getRoles().remove(roleEditor);
		userHaiNT.addRole(roleSalesperson);
		repository.save(userHaiNT);
	}

	@Test
	public void testDeleteUser() {
		Integer userId = 4;
		repository.deleteById(userId);
	}

	@Test
	public void testGetUserByEmail() {
		String email = "nguyentrunghai2106@gmail.com";
		User user = repository.getUserByEmail(email);
		assertThat(user).isNotNull();
	}

	@Test
	public void testCountById() {
		Integer id = 2;
		Long countById = repository.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}

	@Test
	public void testDisableUser() {
		Integer id = 2;
		repository.updateEnabledStatus(id, false);
	}

	@Test
	public void testEnableUser() {
		Integer id = 2;
		repository.updateEnabledStatus(id, true);
	}

	@Test
	public void testListFirstPage() {
		int pageNumber = 1;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repository.findAll(pageable);
		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUser() {
		String keyword = "Hải";
		int pageNumber = 0;
		int pageSize = 11;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repository.findAll(keyword, pageable);
		List<User> listUsers = page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		assertThat(listUsers.size()).isGreaterThan(0);
	}
}
