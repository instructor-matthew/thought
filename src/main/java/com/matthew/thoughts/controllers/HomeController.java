package com.matthew.thoughts.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.matthew.thoughts.models.Thought;
import com.matthew.thoughts.models.User;
import com.matthew.thoughts.services.ThoughtService;
import com.matthew.thoughts.services.UserService;
import com.matthew.thoughts.validators.UserValidator;



@Controller
public class HomeController {
	@Autowired
	private UserService uService;
	@Autowired
	private UserValidator validator;
	@Autowired
	private ThoughtService tService;
	
	@GetMapping("/")
	public String baseRoute(Model viewModel, @ModelAttribute("user") User user) {
		return "index.jsp";
	}
	
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
		validator.validate(user, result);
		if(result.hasErrors()) {
			return "index.jsp";
		}
		User newUser = this.uService.registerUser(user);
		session.setAttribute("user__id", newUser.getId());
		return "redirect:/dashboard";
	}
	
	@PostMapping("/login")
	public String login(@RequestParam("loginEmail") String email, @RequestParam("loginPass") String password, RedirectAttributes redirectAttr, HttpSession session) {
		if(!this.uService.authenticateUser(email, password)) {
			redirectAttr.addFlashAttribute("loginError", "Invalid Credentials");
			return "redirect:/";
		}
		User user = this.uService.getByEmail(email);
		session.setAttribute("user__id", user.getId());
		return "redirect:/dashboard";
	}
	
	@GetMapping("/logout")
	private String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	@GetMapping("/dashboard")
	private String dashboard(@ModelAttribute("thought") Thought thought, Model viewModel, HttpSession session) {
		Long userId = (Long)session.getAttribute("user__id");
		viewModel.addAttribute("user", this.uService.find(userId));
		viewModel.addAttribute("thoughts", this.tService.getThoughts());
		return "dashboard.jsp";
	}
	
	@PostMapping("/addThought")
	public String addThought(@Valid @ModelAttribute("thought") Thought thought, BindingResult result, HttpSession session, Model viewModel) {
		if(result.hasErrors()) {
			Long userId = (Long)session.getAttribute("user__id");
			viewModel.addAttribute("user", this.uService.find(userId));
			return "dashboard.jsp";
		}
		this.tService.create(thought);
		return "redirect:/dashboard";
	}
	
	@GetMapping("/like/{id}")
	public String like(HttpSession session, @PathVariable("id") Long id) {
		Long userId = (Long)session.getAttribute("user__id");
		User userToLike = this.uService.find(userId);
		Thought likedThought = this.tService.getById(id);
		this.tService.likeThought(likedThought, userToLike);
		return "redirect:/dashboard";
	}
	
	@GetMapping("/unlike/{id}")
	public String unlike(HttpSession session, @PathVariable("id") Long id) {
		Long userId = (Long)session.getAttribute("user__id");
		User userToLike = this.uService.find(userId);
		Thought likedThought = this.tService.getById(id);
		this.tService.unlikeThought(likedThought, userToLike);
		return "redirect:/dashboard";
	}
}
