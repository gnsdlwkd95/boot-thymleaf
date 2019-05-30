package idu.cs.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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
		return "index";
	}
	@GetMapping("/login-form")
	public String getLoginForm(Model model) {
		return "login";
	}
	@PostMapping("/login")
	//실제 로그인 처리 , user : 입력한 내용의 객체
	public String loginUser(@Valid User user, HttpSession session) {
		System.out.println("login process : ");
		User sessionUser = userRepo.findByUserId(user.getUserId());
		//db뒤져보기
		if(sessionUser == null) {
			//유저가 없으면 에러
			System.out.println("id error : ");
			return "redirect:/login-form";
		}
		if(!sessionUser.getUserPw().equals(user.getUserPw())) {
			//입력한 패스워드와 저장된 패스워드가 같은지 비교.
			System.out.println("pw error : ");
			return "redirect:/login-form";
		}
		session.setAttribute("user", sessionUser);		
		return "redirect:/";
	}
	@GetMapping("/logout")
	public String logoutUser(HttpSession session) {
		//session.invalidate();
		session.removeAttribute("user");
		return "redirect:/";
	}
	
	@GetMapping("/users")
	public String getAllUser(Model model) {
		model.addAttribute("users", userRepo.findAll());
		return "userlist";
	}	
	@GetMapping("/users/byname") //byname?name=***, ***값이 name 변수
	public String getUsersByname(@Param(value = "name") String name, Model model) {
		List<User> users = userRepo.findByName(name);
		model.addAttribute("users", users);
		return "userlist";
	}
	@GetMapping("/users/bynameasc") //byname?name=***, ***값이 name 변수
	public String getUsersBynameAsc(@Param(value = "name") String name, Model model) {
		List<User> users = userRepo.findByNameOrderByIdAsc(name);
		model.addAttribute("users", users);
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
	@GetMapping("/register-form")
	public String loadRegForm(Model model) {		
		return "register";
	}	    
	@PostMapping("/users")
	public String createUser(@Valid User user, Model model) {
		userRepo.save(user);
		model.addAttribute("users", userRepo.findAll());
		return "redirect:/users";
	}
	@PutMapping("/users/{id}") 
	//@RequestMapping(value=""/users/{id}" method=RequestMethod.UPDATE)
	public String updateUserById(@PathVariable(value = "id") Long userId, @Valid User userDetails, Model model) throws ResourceNotFoundException {
		// userDetails 폼을 통해 전송된 객체, user는 id로 jpa를 통해서 가져온 객체
		User user = userRepo.findById(userId)//userDetails.getId())
				.orElseThrow(() -> 
				new ResourceNotFoundException("not found " + userId ));
		user.setName(userDetails.getName());
		user.setCompany(userDetails.getCompany());
		User userUpdate = userRepo.save(user); // 객체 삭제 -> jpa : record 삭제로 적용
		//model.addAttribute("user", userUpdate);
		return "redirect:/users"; // 업데이트가 성공하면 users 자원을 get 방식으로 접근하되 model에 user 어트리뷰트를 전달한다.
		//return ResponseEntity.ok(userUpdate);
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
