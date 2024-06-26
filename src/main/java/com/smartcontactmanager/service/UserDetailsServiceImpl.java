package com.smartcontactmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontactmanager.Entity.User;
import com.smartcontactmanager.config.CustomUserDetails;
import com.smartcontactmanager.dao.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.getUserByUserName(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("Could not find user!!");
		}
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		return customUserDetails;
	}
		
}
