package fi.haagahelia.course;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.haagahelia.course.web.FlashcardController;
import fi.haagahelia.course.web.UserController;

@SpringBootTest
class StudentListSignUpApplicationTests {
	@Autowired
	FlashcardController fcontroller;
	
	@Autowired
	UserController ucontroller;

	@Test
	void contextLoads() {
		assertThat(fcontroller).isNotNull();
		assertThat(ucontroller).isNotNull();
	}

}
