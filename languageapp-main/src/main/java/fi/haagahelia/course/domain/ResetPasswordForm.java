package fi.haagahelia.course.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ResetPasswordForm {
    @NotEmpty
    @Size(min=7, max=30)
    private String password = "";
    
    @NotEmpty
    @Size(min=7, max=30)
    private String passwordCheck = "";

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordCheck() {
		return passwordCheck;
	}

	public void setPasswordCheck(String passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

    
}
