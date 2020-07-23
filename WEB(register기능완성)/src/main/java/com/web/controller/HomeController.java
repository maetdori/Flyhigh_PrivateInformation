package com.web.controller;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.domain.CertVO;
import com.web.service.CertService;

@Controller
public class HomeController {
	@Resource(name="com.web.service.CertService")
	CertService certService;
	
	    @RequestMapping(value="/")
	    public String home(Model model) {
	    	model.addAttribute("getList",certService.getCertList());
	        return "Home";
	    }
	    
	    @RequestMapping(value="/registerPage")
	    public String registerPage() {
	    	System.out.println("reigister");
	        return "Register";
	    }
	    
	    @RequestMapping(value="/modifyPage")
	    public String modifyPage() {
	    	System.out.println("modify");
	    	
	        return "Modify";
	    }
}
