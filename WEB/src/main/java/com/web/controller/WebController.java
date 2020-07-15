package com.web.controller;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.parse.ParseDer;
import com.web.service.CertService;
import com.web.service.SiteService;

@Controller
@RequestMapping("private")
public class WebController {
	
	@Resource(name="com.web.service.CertService")
	CertService certService;
	@Resource(name="com.web.service.SiteService")
	SiteService siteService;
	
	CertVO cv = new CertVO();
	SiteVO sv = new SiteVO();
	
	@RequestMapping("/register") //인증서등록
	private Map<String, Object> certRegister(@RequestBody Map<String, Object> req, HttpServletResponse resp) throws Exception {
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/register");
		
		Map<String, Object> response = new HashMap<>(); //리턴할 hashMap
		
		setVO(req); //CertVO와 SiteVO setting
		
		certService.certInsertService(cv); 
		siteService.siteInsertService(sv);
		
		Map<String, String> validity = new HashMap<>(); //validity map
		validity.put("NotBefore", cv.getCo_active_date());
		validity.put("NotAfter", cv.getCo_exp_date());
		
		response.put("Subject", cv.getCo_name());
		response.put("validity", validity);
		response.put("count", (int) req.get("count"));
		
		return response;
	}
	
	//RequestBody로 들어온 정보를 VO에 저장
	private void setVO(Map<String, Object> req) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException {
		
		byte[] certBytes = ((String) req.get("certification")).getBytes(); //certification StringToByte
		byte[] cert_decoded = base64Decoder(certBytes); //certification decoding
		
		/*
		 * certVO에 정보 저장
		 * private String co_name; //사용자이름
		 * private String co_active_date; //시작일
		 * private String co_exp_date; //만료일
		 * private String co_cert_pw; //인증서비밀번호
		 * private String co_cert_der; //der 인증서
		 * private String co_cert_key; //key 파일
		 * private String co_certification; //pkcs#12 인증서
		 */
		this.cv.setCo_name((String) req.get("Owner")); //co_name
		this.cv.setCo_cert_pw((String) req.get("cert_pw")); //co_cert_pw
		
		@SuppressWarnings("unchecked")
		Map<String, Object> certification = (Map<String, Object>) req.get("certification");
		
		try { //인증서 확장자가 der인 경우 
			ParseDer cert_parsed = new ParseDer(cert_decoded);
			this.cv.setCo_active_date(cert_parsed.getNotBefore()); //co_active_date
			this.cv.setCo_exp_date(cert_parsed.getNotAfter()); //co_exp_date
			this.cv.setCo_cert_der((String)certification.get("der")); //co_cert_der
		} catch(CertificateException e) { //인증서 확장자가 pfx인 경우(pkcs#12 포맷의 파일은 인증서, 개인키 내용을 파일 하나에 모두 담고 있다.)
			System.out.println("Not a der certificate");
		}
		
		/*
		 * siteVO에 정보 저장
		 * 	private String co_name;
		 * private String co_domain;
		 * private byte[] co_id;
		 * private byte[] co_pw;
		 */
		
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, String>> accList = (ArrayList<Map<String, String>>) req.get("account"); //accountList
		
		this.sv.setCo_name((String) req.get("Owner")); //co_name
		
		for(Map<String, String> acc : accList) {
			this.sv.setCo_domain((String)acc.get("site")); //co_domain
			this.sv.setCo_id((String)acc.get("id")); //co_id
			this.sv.setCo_pw((String)acc.get("pw")); //co_pw
		}
	}
	
	public static byte[] base64Decoder(byte[] encoded_bytes) {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes = decoder.decode(encoded_bytes);
		
		return decodedBytes;
	}
}
