package com.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByUserName(@Param("email") String email);
}
