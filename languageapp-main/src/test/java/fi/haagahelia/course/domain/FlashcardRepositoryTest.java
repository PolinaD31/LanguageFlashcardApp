package fi.haagahelia.course.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@SpringBootTest
public class FlashcardRepositoryTest {
	@Autowired
	private FlashcardReposirory frepository;
	
	@Test
	@Transactional
	public void flashcardAdd() {
		Flashcard flashcard = new Flashcard("test", "test");
		frepository.save(flashcard);
		assertThat(flashcard.getId()).isNotNull();
	}
	
	
	@Test
	@Transactional
	public void flashcardUpdate() {
		Flashcard flashcard = new Flashcard("test", "test");
		frepository.save(flashcard);
	    
	    flashcard.setFront("updatedFront");
	    flashcard.setBack("updatedBack");
	    frepository.save(flashcard);
	    
	    Flashcard updatedFlashcard = frepository.findById(flashcard.getId()).orElse(null);
	    
	    assertThat(updatedFlashcard).isNotNull();
	    assertThat(updatedFlashcard.getFront()).isEqualTo("updatedFront");
	    assertThat(updatedFlashcard.getBack()).isEqualTo("updatedBack");
	}
	
	@Test
	@Transactional
	public void flashcardDelete() {
		Flashcard flashcard = new Flashcard("test", "test");
		frepository.save(flashcard);
		
		frepository.deleteByFront("test");
		
		assertThat(frepository.getByFront("test")).isNull();
	}

	
}
