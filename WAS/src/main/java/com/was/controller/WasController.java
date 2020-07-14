package com.was.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

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
	WasService wasService;
	
	@PostMapping(value = "/getList")
	@ResponseBody
    public Map<String, Object> getList() throws Exception {
		
		List<CertVO> certList = wasService.certListService(); //인증서 리스트(CertVO 타입)
		ArrayList<Map<String, Object>> accList = new ArrayList<>(); //account 리스트
		Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
		
		for(CertVO c : certList) {
			Map<String, String> validity = new HashMap<>(); //Validity Map
			validity.put("NotBefore", c.getCo_active_date());
			validity.put("NotAfter", c.getCo_exp_date());
			Map<String, Object> account = new HashMap<>(); //Account Map
			account.put("Subject", c.getCo_name());
			account.put("Validity", validity);
			accList.add(account);
		}
	
		response.put("count", certList.size());
		response.put("account", accList);
		
		return response;
    }
	
	@PostMapping(value = "/getInfo") 
	@ResponseBody
	public Map<String, Object> getInfo(@RequestBody Map<String, Object> req) throws Exception {
		
		String co_name = (String) req.get("Subject"); //request의 subject값을 co_name에 저장
		
		CertVO cert = wasService.certSearchService(co_name); //찾은 인증서(CertVO 타입)
		List<SiteVO> siteList = wasService.siteListService(co_name); //찾은 사이트 리스트(SiteVo 타입)
		ArrayList<Map<String, Object>> countList = new ArrayList<>(); //count 리스트
		Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
		
		for(SiteVO s : siteList) {
			Map<String, Object> count = new HashMap<>(); //Count Map
			count.put("site", s.getSite());
			count.put("id", s.getId());
			count.put("pw", s.getPw());
			countList.add(count);
		}
		
		response.put("cert_pw", cert.getCo_cert_pw());
		response.put("certification", cert.getCo_cert_der());
		response.put("count", countList);
		
		EncryptModule.encrypt("keystore2.p12","123456",
				new String[] {"cert_pw", "certification", "count/site", "count/id", "count/pw"},req,response);
		
		return response;
	}
	
	
}
