package com.example.demo.Controller;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Entity.UserDto;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.WebsiteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.Service.AdminService;

@Controller
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
	@Autowired
	private AdminService adminService;
	@Autowired
	private WebsiteRepository websiteRepository;

    @GetMapping("/all")
    public String listUser(Model model, HttpServletRequest request) {
        List<UserDto> userDtos = userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getType()))
                .collect(Collectors.toList());
        model.addAttribute("users", userDtos);
		String currentUrl = request.getRequestURI();
		model.addAttribute("currentUrl", currentUrl);
		model.addAttribute("webs", websiteRepository.findAll());
        return "userTables";
    }

	@GetMapping("/default")
	public String listNormalUser(Model model, HttpServletRequest request) {
		List<User> users = userRepository.findAll().stream().toList();
		model.addAttribute("users", users);
		String currentUrl = request.getRequestURI();
		model.addAttribute("currentUrl", currentUrl);
		model.addAttribute("webs", websiteRepository.findAll());
		return "userTables";
	}

	@GetMapping("/create")
	public String createUser() {
		return "userCreateForm";
	}
	@PostMapping("/create")
	public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		if (userRepository.findByUsername(user.getUsername())!= null){
			redirectAttributes.addFlashAttribute("errorMessage", "User already exists!");
		}else{
			User newUser = new User(user.getUsername(), user.getPassword(), user.getType());
			userRepository.save(newUser);
			redirectAttributes.addFlashAttribute("successMessage", "success!");
		}
		return "redirect:/admin/user/default";
	}
	@GetMapping("/delete")
	public String deleteUser(@RequestParam("userId") Integer id, RedirectAttributes redirectAttributes) {
		userRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("successMessage", "success!");
		return "redirect:/admin/user/default";
	}
	@GetMapping("/update")
	public String updateUser(@RequestParam("userId") Integer id, Model model) {
		User user = userRepository.findById(id).orElse(null);
		model.addAttribute("user", user);
		return "userUpdateForm";
	}
	@PostMapping("/update")
	public String updateUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		User existUser = userRepository.findById(user.getId()).orElse(null);
		if (existUser != null){
			existUser.setUsername(user.getUsername());
			existUser.setPassword(user.getPassword());
			existUser.setType(user.getType());
			userRepository.save(existUser);
			redirectAttributes.addFlashAttribute("successMessage", "success!");
		}else {
			redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
		}
		return "redirect:/admin/user/default";
	}
}
	

