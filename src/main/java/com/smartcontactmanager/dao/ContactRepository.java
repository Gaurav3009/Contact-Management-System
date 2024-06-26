package com.smartcontactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.Entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	// pagination...
	// Pageable stores the contacts per page and the currentContact index
	// Page will store the Contacts to be returned
	@Query("from Contact as c where c.user.id = :userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageRequest);
	
	
	
}
