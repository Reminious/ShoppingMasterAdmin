package com.example.demo.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	
@GetMapping("/home")
public String HomePage(HttpSession session) {
	System.out.println("Session Username: " + session.getAttribute("username"));
	return "home";
}





}
