package fi.haagahelia.course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import fi.haagahelia.course.domain.Flashcard;
import fi.haagahelia.course.domain.FlashcardReposirory;
import fi.haagahelia.course.domain.AppUser;
import fi.haagahelia.course.domain.AppUserRepository;

@SpringBootApplication
public class StudentListSignUpApplication {
	private static final Logger log = LoggerFactory.getLogger(StudentListSignUpApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StudentListSignUpApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner studentDemo(AppUserRepository urepository, FlashcardReposirory frepository) {
		return (args) -> {

		};
	}
}
