package fi.haagahelia.course.web;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fi.haagahelia.course.domain.AppUserRepository;
import fi.haagahelia.course.domain.Flashcard;
import fi.haagahelia.course.domain.FlashcardReposirory;

import java.util.Random;





@Controller
public class FlashcardController {
	
	@Autowired 
	private FlashcardReposirory frepository;
	
	@Autowired 
	private AppUserRepository urepository;

	// list flashcards
	@RequestMapping("/flashcards")
	public String listFlashcards(Model model) {
		model.addAttribute("flashcards", frepository.findAll());
		
		return "flascardlist";
	}
	
	// Add a new flashcard
	@RequestMapping(value = "/add")
	public String addFlashcard(Model model) {
		model.addAttribute("flashcard", new Flashcard());
		return "addflashcard";
	}
	
	// Save the flashcard
	@RequestMapping(value = "/saveflashcard", method = RequestMethod.POST)
    public String save(Flashcard flashcard){
        frepository.save(flashcard);
        return "redirect:/flashcards";
    }  
		
	// edit flashcard
	@RequestMapping(value = "/edit/{id}")
	public String editFlashcard(@PathVariable("id") Long flashcardId, Model model) {
	    Flashcard flashcard = frepository.findById(flashcardId).orElse(null);

        model.addAttribute("flashcard", flashcard);
        return "edit_flashcard";
	    
	}
	
	// Delete flashcard
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteFlashcard(@PathVariable("id") Long flashcardId, Model model) {
    	frepository.deleteById(flashcardId);
        return "redirect:../flashcards";
	}
	
	// Show back of the flashcard
	@RequestMapping("/showback/{id}")
	public String showBack(@PathVariable("id") Long id, Model model) {
		Flashcard flashcard = frepository.findById(id).get();
		flashcard.setShowBack(true);
		frepository.save(flashcard);
		return "redirect:/flashcards";
		
	}
	
	// Show the front of the flashcard
	@RequestMapping("/showfront/{id}")
	public String showFront(@PathVariable("id") Long id, Model model) {
		Flashcard flashcard = frepository.findById(id).get();
		flashcard.setShowBack(false);
		frepository.save(flashcard);
		return "redirect:/flashcards";
		
	}
	
	// Shows random flashcards
	@RequestMapping("/practice")
	public String practice(Model model) {
		List<Flashcard> flashcards = frepository.findByAnsweredCorrectlyFalse();
	    
	    if (flashcards.isEmpty()) {
	        return "congratulations";
	    } else {
	    Random random = new Random();
	    int randomIndex = random.nextInt(flashcards.size());
	    Flashcard currFlashcard = flashcards.get(randomIndex);
	    
	    model.addAttribute("currFlashcard", currFlashcard);
	    return "practice";
	    }

	}
	
	// Checks if user answer is correct, if correct removes it from showing in practice until reset
	@RequestMapping(value = "/check", method = RequestMethod.POST)
	public String checkAnswer(@RequestParam String userInput, @RequestParam Long flashcardId,Model model) {
		Flashcard flashcard = frepository.findById(flashcardId).get();
	    String correctAnswer = flashcard.getBack();
	    model.addAttribute("currFlashcard", frepository.findById(flashcardId).get());
	    
	    if (userInput.trim().equalsIgnoreCase(correctAnswer)) {
	        model.addAttribute("Message", "Correct");
	        flashcard.setAnsweredCorrectly(true); 
            frepository.save(flashcard);
	        System.out.println("Correct");
	        
	    } else {
	        model.addAttribute("Message", "Incorrect");
	        System.out.println("Incorrect");
	    }
	    
	    return "check";
	}
	
	// Resets the working set of flashcards, so all the flashcards are shown again
	@RequestMapping("/reset")
	public String resetAnswerewdCorrectly() {
		List<Flashcard> flashcards = (List<Flashcard>) frepository.findAll();
		
		for (Flashcard flashcard : flashcards) {
	        flashcard.setAnsweredCorrectly(false);
	    }

	    frepository.saveAll(flashcards);

	    return "redirect:/flashcards";
	}
	
	

}
