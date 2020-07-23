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
	public Map<String, Object> getInfo(@RequestBody Map<String, Object> req) throws Exception {
		
		String co_name = (String) req.get("subject"); //request의 subject값을 co_name에 저장
		
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
		System.out.println("cert_pw : " + cert.getCo_cert_pw());
		response.put("account", countList);
		response.put("count", siteList.size());
		response.put("cert_pw", cert.getCo_cert_pw());
		Map<String,String> certification = new HashMap<>();
		String der = cert.getCo_cert_der();
		int cert_type = 0;
		if(der != "") {
			cert_type = 1;
			certification.put("der", der);
			certification.put("key", cert.getCo_cert_key());
			certification.put("pfx", null);
			response.put("certification", certification);
			response.put("cert_type", cert_type);
			EncryptModule.encrypt("keystore2.p12","123456",
					new String[] {"cert_pw", "certification/der","certification/key", "account/site", "account/id", "account/pw"},req,response);
		}
		else {
			cert_type = 2;
			certification.put("der", null);
			certification.put("key", null);
			certification.put("pfx", cert.getCo_certification());
			response.put("certification", certification);
			response.put("cert_type", cert_type);
			EncryptModule.encrypt("keystore2.p12","123456",
					new String[] {"cert_pw", "certification/pfx", "count/site", "count/id", "count/pw"},req,response);
		}
		
		
		
		return response;
	}
	
	
}
