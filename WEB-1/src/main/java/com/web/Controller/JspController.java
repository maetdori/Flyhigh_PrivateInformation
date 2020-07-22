package com.web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JspController {
	
	@GetMapping("/home")
	public String home() {
		return "Home"; // JSP파일명
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
}
