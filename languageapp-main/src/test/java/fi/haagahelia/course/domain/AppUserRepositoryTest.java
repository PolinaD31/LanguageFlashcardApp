package fi.haagahelia.course.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@SpringBootTest
public class AppUserRepositoryTest {
	@Autowired
	private AppUserRepository urepository;
	
	@Test
	@Transactional
	public void userCreate() {
	    AppUser appUser = new AppUser();
	    appUser.setUsername("newTestUser");
	    appUser.setEmail("newtest@example.com");
	    appUser.setPasswordHash("testPassword");
	    appUser.setRole("USER");
	    
	    urepository.save(appUser);

	    assertThat(appUser.getId()).isNotNull();
	}
	
	@Test
	@Transactional
	public void userUpdate() {
		AppUser appUser = urepository.findByEmail("admin@example.com");
		appUser.setUsername("updatedAdmin");
		
		assertThat(urepository.findByEmail("admin@example.com")).isNotNull();
	    assertThat(urepository.findByEmail("admin@example.com").getUsername() == "updatedAdmin");
	}
	
	@Test
	@Transactional
	public void userDelete() {
		AppUser appUser = urepository.findByEmail("admin@example.com");
		urepository.deleteById(appUser.getId());

	    assertThat(urepository.findByEmail("admin@example.com")).isNull();
	}
}
