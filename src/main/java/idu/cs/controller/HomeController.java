package idu.cs.controller;

import java.util.Optional;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

import idu.cs.domain.User;
import idu.cs.exception.ResourceNotFoundException;
import idu.cs.repository.UserRepository;

@Controller
public class HomeController {
	@Autowired UserRepository userRepo; // Dependency Injection
	
	
	/*@GetMapping("/test")
	public String home(Model model) {
		model.addAttribute("test", "인덕 컴소");
		model.addAttribute("egy", "유응구");
		return "index";
	}*/
	
	@GetMapping("/")
	public String loadWelcome(Model model) {
		return "welcome";
	}	
	
	@GetMapping("/users")
	public String getAllUser(Model model) {
		model.addAttribute("users", userRepo.findAll());
		return "userlist";
	}	
	@GetMapping("/users/{id}")
	public String getUserById(@PathVariable(value = "id") Long userId,  
	Model model) throws ResourceNotFoundException {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> 
				new ResourceNotFoundException("not found " + userId ));		
		model.addAttribute("user",user);
		/*model.addAttribute("id", "" + userId);
		model.addAttribute("name", user.getName());
		model.addAttribute("company", user.getCompany());*/
		return "user";
	}
	@GetMapping("/regform")
	public String loadRegForm(Model model) {		
		return "regform";
	}	    
	@PostMapping("/users")
	public String createUser(@Valid User user, Model model) {
		userRepo.save(user);
		model.addAttribute("users", userRepo.findAll());
		return "redirect:/users";
	}
	@PutMapping("/users/{id}") 
	//@RequestMapping(value="/users/{id}" , method =Request.DELETE) 
	public String updateUserById(@PathVariable(value = "id") Long userId,  
			@Valid @RequestBody User userDetails, Model model)
				throws ResourceNotFoundException {
					User user = userRepo.findById(userId)
							.orElseThrow(() -> 
							new ResourceNotFoundException("not found " + userId ));
					user.setName(userDetails.getName());
					user.setCompany(userDetails.getCompany());
					User userUpdate = userRepo.save(user);
					
					 //model.addAttribute("user",userUpdate);
					return "redirect:/users";	//업데이트가 성공하면 users에 get방식으로 moel에
					// user어트리뷰트를 전달하면서 리다이렉션
	}
	@DeleteMapping("/users/{id}")
	public String DeleteUserById(@PathVariable(value = "id") Long userId,  
	Model model) throws ResourceNotFoundException {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> 
				new ResourceNotFoundException("not found " + userId ));		
		userRepo.delete(user);
		model.addAttribute("name", user.getName());
		
		return "disjoin";
	}
}
