package fi.haagahelia.course.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AppUser {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	// Unique constrain for the username
	@Column(name = "username", nullable = false, unique = true)
	private String username;
	
	@Column(name = "password", nullable = false)
	private String passwordHash;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "role", nullable = false)
	private String role;
	
	@Column(name = "enabled")
    private boolean enabled;
	
	@Column(name = "reset_token")
    private String resetToken;
	
	@Column(name = "verification_token")
    private String verificationToken;
    
    
	public AppUser() {
		
	}
	

	public AppUser(String username, String passwordHash,  String email, String role) {
		super();
		this.username = username;
		this.passwordHash = passwordHash;
		this.email = email;
		this.role = role;
		this.enabled = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public String getResetToken() {
		return resetToken;
	}


	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}


	public String getVerificationToken() {
		return verificationToken;
	}


	public void setVerificationToken(String verificationToken) {
		this.verificationToken = verificationToken;
	}

	
	
}
