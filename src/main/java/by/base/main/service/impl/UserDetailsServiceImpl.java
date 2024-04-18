package by.base.main.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import by.base.main.dao.UserDAO;
import by.base.main.model.User;
import by.base.main.service.util.MailService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserDAO userDAO;
	
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String loginfirst) {
		String login = loginfirst.toLowerCase();
		User user = userDAO.getUserByLogin(login);
		UserBuilder userBuilder = null;
		if (user != null) {
			userBuilder = org.springframework.security.core.userdetails.User.withUsername(login);
			userBuilder.disabled(!user.isEnablet());
			userBuilder.password(user.getPassword());
			String[] authorities = user.getRoles().stream().map(a-> a.getAuthority()).toArray(String[]::new);
			userBuilder.authorities(authorities);
		}else {
			System.out.println(SecurityContextHolder.getContext());
			System.out.println("User not found.");
			throw new UsernameNotFoundException("User not found.");
		}
		return userBuilder.build();
		
	}

}
