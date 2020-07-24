package com.web.controller;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.domain.CertVO;
import com.web.service.CertService;
import com.web.service.SiteService;

@Controller
public class HomeController {
	@Resource(name="com.web.service.CertService")
	CertService certService;
	@Resource(name="com.web.service.SiteService")
	SiteService siteService;
	
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
	    public String modifyPage(String co_name,Model model) throws Exception {
	    	System.out.println("modify : " + co_name);
	    	
	    	model.addAttribute("getCert",certService.certSearchService(co_name));
	    	model.addAttribute("getSiteList",siteService.siteListService(co_name));
	        return "Modify";
	    }
}
