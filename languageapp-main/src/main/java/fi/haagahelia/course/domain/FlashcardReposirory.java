package fi.haagahelia.course.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FlashcardReposirory extends CrudRepository<Flashcard, Long> {
	
	List<Flashcard> findByAnsweredCorrectlyFalse();

	Flashcard getByFront(String string);

	void deleteByFront(String string);

}
