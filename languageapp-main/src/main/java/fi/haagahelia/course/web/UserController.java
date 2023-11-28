package fi.haagahelia.course.web;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;

import fi.haagahelia.course.domain.SignupForm;
import fi.haagahelia.course.domain.UserNotFoundException;
import fi.haagahelia.course.domain.AppUser;
import fi.haagahelia.course.domain.AppUserRepository;
import fi.haagahelia.course.domain.ResetPasswordForm;

@Controller
public class UserController {
	@Autowired
	private AppUserRepository urepository;

	@Autowired
	private JavaMailSender mailSender;

	// Show sign up form
	@RequestMapping(value = "signup")
    public String addStudent(Model model){
    	System.out.println("we are at signup");
    	model.addAttribute("signupform", new SignupForm());
        return "signup";
    }	
	
	// Login
	@RequestMapping(value="/login")
	public String login() {
		return "login";
	}
	
	// Show list of users for admin
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public String listUsers(Model model) {
		model.addAttribute("users", urepository.findAll());
        return "userlist";
    }
	
	// Delete user for admin
	@PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/delete_user/{id}", method = RequestMethod.GET)
    public String deleteUser(@PathVariable("id") Long userId) {
    	urepository.deleteById(userId);
        return "redirect:../admin/users";
	}  
	
	// Add user for admin
	@PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add_user", method = RequestMethod.GET)
    public String addUser(Model model) {
		model.addAttribute("userform", new SignupForm());
        return "add_user";
	} 
	
	// Save user for admin
	@RequestMapping(value = "saveuser_admin", method = RequestMethod.POST)
	public String adminSave(@Valid @ModelAttribute("userform") SignupForm signupForm, BindingResult bindingResult,
			HttpServletRequest request, Model model) throws MessagingException {
		if (!bindingResult.hasErrors()) { // validation errors
			if (signupForm.getPassword().equals(signupForm.getPasswordCheck())) { // check password match
				String pwd = signupForm.getPassword();
				BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
				String hashPwd = bc.encode(pwd);


				AppUser newUser = new AppUser();
				newUser.setPasswordHash(hashPwd);
				newUser.setUsername(signupForm.getUsername());
				newUser.setEmail(signupForm.getEmail());
				newUser.setRole("USER");
				newUser.setEnabled(true);
				if (urepository.findByUsername(signupForm.getUsername()) == null) { // Check if user exists
					if (urepository.findByEmail(signupForm.getEmail()) == null) { // check if email already exists
						urepository.save(newUser);
					} else {
						bindingResult.rejectValue("email", "err.email", "Email is already in use");
						return "add_user";
					}
				} else {
					bindingResult.rejectValue("username", "err.username", "Username already exists");
					return "add_user";
				}
			} else {
				bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");
				return "add_user";
			}
		} else {
			return "add_user";
		}
		return "redirect:/admin/users";
	}
	

	// Save the user and
	@RequestMapping(value = "saveuser", method = RequestMethod.POST)
	public String save(@Valid @ModelAttribute("signupform") SignupForm signupForm, BindingResult bindingResult,
			HttpServletRequest request, Model model) throws MessagingException {
		if (!bindingResult.hasErrors()) { // validation errors
			if (signupForm.getPassword().equals(signupForm.getPasswordCheck())) { // check password match
				String pwd = signupForm.getPassword();
				BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
				String hashPwd = bc.encode(pwd);

				UUID uuid = UUID.randomUUID();
				String verificationToken = uuid.toString().replaceAll("-", "");

				AppUser newUser = new AppUser();
				newUser.setPasswordHash(hashPwd);
				newUser.setUsername(signupForm.getUsername());
				newUser.setEmail(signupForm.getEmail());
				newUser.setRole("USER");
				newUser.setVerificationToken(verificationToken);
				if (urepository.findByUsername(signupForm.getUsername()) == null) { // Check if user exists
					if (urepository.findByEmail(signupForm.getEmail()) == null) { // check if email already exists
						urepository.save(newUser);

						String url = request.getRequestURL().toString();
						String verificationLink = url.replace(request.getServletPath(), "") + "/verify_email?token="
								+ verificationToken;
						sendVerificationEmail(newUser.getEmail(), verificationLink);
						model.addAttribute("message", "We have sent you a verification email. Please check your email.");
					} else {
						bindingResult.rejectValue("email", "err.email", "Email is already in use");
						return "signup";
					}
				} else {
					bindingResult.rejectValue("username", "err.username", "Username already exists");
					return "signup";
				}
			} else {
				bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");
				return "signup";
			}
		} else {
			return "signup";
		}
		return "signup";
	}

	// Verify user email
	@RequestMapping(value = "/verify_email", method = RequestMethod.GET)
	public String verifyEmail(@RequestParam(value = "token") String token, Model model) {
	    AppUser user = urepository.findByVerificationToken(token);
	    
	    if (user != null) {
	        user.setEnabled(true);
	        user.setVerificationToken(null);
	        urepository.save(user);
	        
	        return "verify_email";
	    } else {
	        return "token_error";
	    }
	    
	}

	// Direct user to the forgot password page
	@RequestMapping(value = "/forgot_password", method = RequestMethod.GET)
	public String forgotPassword(Model model) {
		return "forgotpassword";
	}

	// Send a reset email to the user
	@RequestMapping(value = "/forgot_password", method = RequestMethod.POST)
	public String processForgotPasword(HttpServletRequest request, Model model) throws MessagingException {
		try {
			String email = request.getParameter("email");
			UUID uuid = UUID.randomUUID();
			String token = uuid.toString().replaceAll("-", "");

			AppUser appUser = urepository.findByEmail(email);
			
			if (appUser == null) {
				throw new UserNotFoundException("Could not find the user with this email.");
			} else if (!appUser.isEnabled()) {
				throw new UserNotFoundException("The user is not verified. Please check your email for verification link");
			} else {
				appUser.setResetToken(token);
				urepository.save(appUser);
			}

			String url = request.getRequestURL().toString();

			// gets rid of /forgot_password
			String passwordResetLink = url.replace(request.getServletPath(), "") + "/reset_password?token=" + token;
			System.out.println(passwordResetLink);

			sendResetEmail(email, passwordResetLink);

			model.addAttribute("message", "We have sent you a reset link. Please check your email.");

		} catch (UserNotFoundException exeption) {
			model.addAttribute("error", exeption.getMessage());
		} catch (MessagingException exception) {
			model.addAttribute("error", "Error while sending email");
		}
		return "forgotpassword";
	}

	// Direct user to the reset password form if the token is valid
	@RequestMapping(value = "/reset_password", method = RequestMethod.GET)
	public String showResetPasswordForm(@RequestParam(value = "token") String token, Model model) {
		AppUser user = urepository.findByResetToken(token);
		model.addAttribute("token", token);
		model.addAttribute("resetform", new ResetPasswordForm());

		if (user == null) {
			return "token_error";
		}

		return "reset_password";
	}

	// Reset user password
	@RequestMapping(value = "/reset_password", method = RequestMethod.POST)
	public String showResetPasswordForm(@RequestParam(value = "token") String token,
			@Valid @ModelAttribute("resetform") ResetPasswordForm resetForm, BindingResult bindingResult) {
		AppUser appUser = urepository.findByResetToken(token);

		if (!bindingResult.hasErrors()) {
			if (resetForm.getPassword().equals(resetForm.getPasswordCheck())) {
				String pwd = resetForm.getPassword();
				BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
				String hashPwd = bc.encode(pwd);

				appUser.setPasswordHash(hashPwd);
				appUser.setResetToken(null);

				urepository.save(appUser);
			}
		} else {
			bindingResult.rejectValue("passwordCheck", "err.passCheck", "Passwords does not match");
			return "reset_password";
		}

		return "redirect:/login";
	}
	
	// Sending verification email
	private void sendVerificationEmail(String email, String verificationLink) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("languageapp4@gmail.com");
		helper.setTo(email);

		String content = "<p>Hello,</p>"
				+ "<p>Thank you for registering. Please verify your email by clicking the link below:</p>"
				+ "<p><a href=\"" + verificationLink + "\">Verify my email</a></p>";

		helper.setSubject("Email Verification");
		helper.setText(content, true);

		mailSender.send(message);

	}
	
	// Send password reset email
	private void sendResetEmail(String email, String passwordResetLink) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("languageapp4@gmail.com");
		helper.setTo(email);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password</p>"
				+ "<p>Click the link below to reset your password</p>" + "<p><a href=\"" + passwordResetLink
				+ "\">Change my password</a></p>";

		helper.setSubject("Password reset link");
		helper.setText(content, true);

		mailSender.send(message);
	}

}
