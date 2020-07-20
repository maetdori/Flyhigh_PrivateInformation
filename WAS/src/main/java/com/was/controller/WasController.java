package com.was.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.was.domain.CertVO;
import com.was.domain.SiteVO;
import com.was.key_exchange.EncryptModule;
import com.was.service.WasService;

@Controller
@RequestMapping("/private")
public class WasController {
	
	@Resource(name="com.was.service.WasService")
	private WasService wasService;
	
	@GetMapping(value = "/getList")
	@ResponseBody
    public Map<String, Object> getList(HttpServletResponse resp) throws Exception {
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/getList");
		
		List<CertVO> certList = wasService.certListService(); //certification list
		ArrayList<Map<String, Object>> accList = new ArrayList<>(); //account list
		Map<String, Object> response = new HashMap<>(); //return value
		
		for(CertVO c : certList) {
			Map<String, String> validity = new HashMap<>(); //Validity Map
			validity.put("notBefore", c.getCo_active_date());
			validity.put("notAfter", c.getCo_exp_date());
			Map<String, Object> account = new HashMap<>(); //Account Map
			account.put("subject", c.getCo_name());
			account.put("validity", validity);
			accList.add(account);
		}
	
		response.put("count", certList.size());
		response.put("account", accList);
		
		return response;
    }
	
	@PostMapping(value = "/getInfo") 
	@ResponseBody
	public Map<String, Object> getInfo(@RequestBody Map<String, Object> req, HttpServletResponse resp) throws Exception {
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/getInfo");
		
		String co_name = (String) req.get("subject"); 
		
		CertVO cert = wasService.certSearchService(co_name); //certificate requested
		List<SiteVO> siteList = wasService.siteListService(co_name); //site list
		ArrayList<Map<String, String>> accList = new ArrayList<>(); //new account list
		Map<String, Object> response = new HashMap<>(); //return value 
		
		for(SiteVO s : siteList) {
			Map<String, String> account = new HashMap<>(); //Account Map
			account.put("site", s.getCo_domain());
			account.put("id", s.getCo_id());
			account.put("pw", s.getCo_pw());
			accList.add(account);
		}
		
		response.put("cert_pw", cert.getCo_cert_pw());
		
		try {
			response.put("certification", cert.getCo_cert_der()); //assume the certificate is of type der
		}
		catch(NullPointerException e) {
			System.out.println("Not a der certificate");
		}
		
		response.put("count", siteList.size());
		response.put("account", accList);
		
		EncryptModule.encrypt("keystore2.p12","123456",
				new String[] {"cert_pw", "certification", "account/site", "account/id", "account/pw"},req,response);
		
		return response;
	}
	
	
}
