package com.smartcontactmanager.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartcontactmanager.Entity.Contact;
import com.smartcontactmanager.Entity.User;
import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println(userName);
		// Get the user using the email or the user name
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "Profile");
		return "/normal/user_dashboard";
	}
	
	// Handler for adding the contacts
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	// Processing add contact form
	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact,
			@RequestParam("profile-image") MultipartFile image,
			Principal principal,
			RedirectAttributes redirectAttributes) {
		
		try {
			
			// Processing the image uploaded
			if(image.isEmpty()) {
				contact.setImage("user.png");
			} else {
				// Updating the image information
				contact.setImage(image.getOriginalFilename());
				
				// Saving file uploaded using the file system
				File file = new ClassPathResource("static/Image").getFile();
				Files.copy(
						image.getInputStream(),
						Paths.get(file.getAbsolutePath() + File.separator + image.getOriginalFilename()),
						StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded successfully..");
			}
			
			// Updating the contact object by adding the current user details
			String userName = principal.getName();
			User user = this.userRepository.getUserByUserName(userName);
			
			
			// Bidirectional mapping 
			contact.setUser(user);
			user.getContacts().add(contact);
	
			
			this.userRepository.save(user);
			
			redirectAttributes.addFlashAttribute("message", new Message("Contact added successfully !!", "alert-success"));
			return "redirect:/user/add-contact";
			
		} catch(Exception e) {
			redirectAttributes.addFlashAttribute("message", new Message("Contact added successfully !!", "alert-danger"));
			return "redirect:/user/add-contact";
		}
		
	}
	
	// Handler to show the contact
	// Show five contacts in per page
	// currentPage = 0th index
	@GetMapping("/show-contact/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		// First method to fetch the contacts of the user, but it is not the best way because we need to implement the update and delete funtionalityt on the contacts and the changes should be directly reflected on the database
//		String userName = principal.getName();
//		User user = this.userRepository.getUserByUserName(userName);
//		List<Contact> contacts = user.getContacts();\
		
		// Second method using the ContactRepository object
		
		String userName = principal.getName();
		int userId = this.userRepository.getUserByUserName(userName).getId();
		PageRequest pageRequest = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactByUser(userId, pageRequest);
		
		
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		model.addAttribute("contacts", contacts);
		model.addAttribute("title", "ViewContacts");
		return "normal/show-contacts";
	}
	
	// Showing specific contact detail
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Contact contact = this.contactRepository.findById(cId).get();
		if(user.getId() != contact.getUser().getId()) {
			model.addAttribute("message", new Message("You are not allowed to view this contact !!", "alert-danger"));
		} else {
			model.addAttribute("contact", contact);
		}
		model.addAttribute("title", " Contact");
		return "normal/contact-detail";
	}
	
	@RequestMapping("/delete/{cId}")
	public String deleteContact(
			@PathVariable("cId") Integer cId,
			Principal principal,
			Model model
			) {
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Contact contact = this.contactRepository.findById(cId).get();
		if(user.getId() == contact.getUser().getId()) {
			this.contactRepository.delete(contact);
		} else {
			model.addAttribute("message", new Message("Permission Denied !!", "alert-danger"));
		}
		
		
		return "redirect:/user/show-contact/0";
	}
	
	
	// Showing the profile of the user logged in
	
	@RequestMapping("/show-profile")
	public String showProfile(Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		
		model.addAttribute("user", user);
		model.addAttribute("title", "Profile");
		return "normal/show-profile";
	}
	
	// Updating a particular contact detail
	
	@RequestMapping("/update/{cId}")
	public String updateContact(
			@PathVariable("cId") Integer cId,
			Principal principal,
			Model model) {
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		Contact contact = this.contactRepository.findById(cId).get();
		
		if(user.getId()  != contact.getUser().getId()) {
			// show error message of permission denied
		} else {
			model.addAttribute("contact", contact);
		}
		
		model.addAttribute("title", "Update");
		return "normal/update-contact";
	}
	
	
	@PostMapping("/update-contact-process/{cId}")
	public String updateContactProcess(
			@PathVariable("cId") Integer cId,
			@ModelAttribute Contact contact,
			@RequestParam("profile-image") MultipartFile image,
			Principal principal,
			Model model
			) throws IOException {
		
		String userName = principal.getName();
		User user =  this.userRepository.getUserByUserName(userName);
		Contact c = this.contactRepository.findById(cId).get();
		
		if(user.getId() != c.getUser().getId()) {
			// Handle if the user wants to update another contact which he/she is not authorised for
		} else {
			
			// Updating the contact information in the contacts object that we get from the database
			
			c.setName(contact.getName());
			c.setSecondName(contact.getSecondName());
			c.setEmail(contact.getEmail());
			c.setPhone(contact.getPhone());
			c.setDescription(contact.getDescription());
			
			if(image != null) {
				
				File saveFile = new ClassPathResource("static/Image/" + c.getImage()).getFile();
				
				// Deleting the old image that is saved
				
				File oldFile = new File(saveFile, c.getImage());
				oldFile.delete();
				
				// Udating the new image that we got
				
				Files.copy(
						image.getInputStream(),
						Paths.get(saveFile.getAbsolutePath() + File.separator + image.getOriginalFilename()),
						StandardCopyOption.REPLACE_EXISTING);
				c.setImage(image.getOriginalFilename());
			}
			
			// Updating the contact object that is being stored in the User object
			
			user.setContacts(user.getContacts().stream().map((item)-> {
				if(item.getcId() == cId) {
					return c;
				} else {
					 return item;
				}
			}).collect(Collectors.toList()));
			
		}
		
		this.contactRepository.save(c);
		this.userRepository.save(user);
		model.addAttribute("contact", c);
		
		return "redirect:/user/" + cId + "/contact";
	}
}
