package com.web.controller;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.service.CertService;
import com.web.service.SiteService;


import exception.WebException;

@Controller
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
	        return "Register";
	    }
	    
	    @RequestMapping(value="/modifyPage")
	    public String modifyPage(String co_name,Model model) {
	    	try {
		    	CertVO cv = certService.certSearchService(co_name);
		    	List<SiteVO> svs = siteService.siteListService(co_name);
		    	if(cv == null || svs == null) {
		    		WebException e = new WebException("no such name in DB : " + co_name,WebException.MODIFY | WebException.NO_SUCH_NAME);
		    		logger.error(e.toString(),e);
		    		return "error/" + 404;
		    	}
		    	
		    	
		    	model.addAttribute("getCert",cv);
		    	model.addAttribute("getSiteList",svs);
		        return "Modify";
	    	} catch(DataAccessException e2) {
	    		WebException ee = new WebException("Error while Accessing DB : " + co_name,WebException.MODIFY | WebException.DATABASE_ERROR,e2);
	    		logger.error(ee.toString(),ee);
	    		return "error/" + 500;
	    	}
	    	catch(Exception e) {
	    		WebException ee = new WebException("Unknown error" + co_name,WebException.MODIFY,e);
	    		logger.error(ee.toString(),ee);
	    		return "error/" + 500;
	    	}
	    }
}
