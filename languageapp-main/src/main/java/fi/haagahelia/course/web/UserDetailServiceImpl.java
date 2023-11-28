package fi.haagahelia.course.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.DisabledException;

import fi.haagahelia.course.domain.AppUser;
import fi.haagahelia.course.domain.AppUserRepository;

/**
 * This class is used by spring security to authenticate and authorize user
 **/
@Service
public class UserDetailServiceImpl implements UserDetailsService  {
	private final AppUserRepository repository;

	@Autowired
	public UserDetailServiceImpl(AppUserRepository userRepository) {
		this.repository = userRepository;
	}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  
    	
    	AppUser curruser = repository.findByUsername(username);
    	
    	if (!curruser.isEnabled()) {
            throw new DisabledException("User account is not enabled");
        }
    	
        UserDetails user = new org.springframework.security.core.userdetails.User(username, curruser.getPasswordHash(), 
        		AuthorityUtils.createAuthorityList(curruser.getRole()));
        return user;
    }   
} 