package com.donations.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.donations.admin.user.UserRepository;
import com.donations.common.entity.User;

public class DonationsUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user with email: " + username);
		} 
		return new DonationsUserDetails(user);
	}

}
