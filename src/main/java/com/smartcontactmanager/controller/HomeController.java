package com.smartcontactmanager.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartcontactmanager.Entity.User;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.helper.Message;




@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "SignUp");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
//			@RequestParam("enabled") boolean enabled,
			Model model, 
			RedirectAttributes redirectAttributes) {
		
		try {
//			if(!enabled) {
//				System.out.println("You have not agreed the terms and conditions");
//				throw new Exception("You have not agreed the terms and conditions");
//			}
			System.out.println(result.toString());
			if(result.hasErrors()) {
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("USER");
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepository.save(user);
			
			System.out.println(user);
			model.addAttribute("user", user);
			redirectAttributes.addFlashAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "redirect:/signup";
		} catch(Exception e) {
			model.addAttribute("user", user);
			redirectAttributes.addFlashAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
			return "redirect:/signup";
		}
		
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "SignIn");
		return "login";
	}
	
	
	
}
