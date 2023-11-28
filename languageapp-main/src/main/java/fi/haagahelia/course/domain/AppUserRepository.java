package fi.haagahelia.course.domain;

import org.springframework.data.repository.CrudRepository;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {
	AppUser findByUsername(String username);
	
	AppUser findByResetToken(String token);
	
	AppUser findByVerificationToken(String token);
	
	AppUser findByEmail(String email);
}