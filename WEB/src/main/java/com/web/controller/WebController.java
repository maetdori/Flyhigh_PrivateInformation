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
	
	CertVO cv;
	SiteVO sv;
	
	@RequestMapping("/register") //인증서등록
	private Map<String, Object> certRegister(@RequestBody Map<String, Object> req, HttpServletResponse resp) throws Exception {
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/register");
		
		Map<String, Object> response = new HashMap<>(); //리턴할 hashMap
		
		cv = new CertVO();
		sv = new SiteVO();
		setVO(req, cv, sv); //CertVO와 SiteVO setting
		
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
	
	@RequestMapping("/modify") //인증서수정 (수정가능정보: cert_pw, account)
	private Map<String, Object> certModify(@RequestBody Map<String, Object> req, HttpServletResponse resp) throws Exception{
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/modify");
		
		Map<String, Object> response = new HashMap<>(); //리턴할 hashMap
		
		cv = new CertVO();
		sv = new SiteVO();
		
		if(req.containsKey("cert_pw")) { //인증서 패스워드 수정하는 경우
			certService.certUpdateService((String)req.get("cert_pw"), (String)req.get("owner"));
			if(req.containsKey("flag")) { //사이트 정보 수정하는 경우
				ArrayList<Map<String, String>> accountList = (ArrayList<Map<String, String>>) req.get("account"); //flag와 함께 요청된 수정할 accountList 
				switch((int)req.get("flag")) {
				//flag==1: account 추가
				case 1: 
					for(Map<String,String> acc : accountList) {
						sv.setCo_name((String)acc.get("owner"));
						sv.setCo_domain((String)acc.get("site"));
						sv.setCo_id((String)acc.get("id"));
						sv.setCo_pw((String)acc.get("pw"));
						siteService.siteInsertService(sv);
					}
				//flag==2: 기존 account 수정	- pw만 수정 가능
				case 2:
					
				}
			}
		}
		
		return response;
	}
	
	
	//RequestBody로 들어온 정보를 VO에 저장
	private void setVO(Map<String, Object> req, CertVO cv, SiteVO sv) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException {
		
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
		cv.setCo_name((String) req.get("owner")); //co_name
		cv.setCo_cert_pw((String) req.get("cert_pw")); //co_cert_pw
		
		@SuppressWarnings("unchecked")
		Map<String, Object> certification = (Map<String, Object>) req.get("certification");
		
		try { //인증서 확장자가 der인 경우 
			ParseDer cert_parsed = new ParseDer(cert_decoded);
			cv.setCo_active_date(cert_parsed.getNotBefore()); //co_active_date
			cv.setCo_exp_date(cert_parsed.getNotAfter()); //co_exp_date
			cv.setCo_cert_der((String)certification.get("der")); //co_cert_der
		} catch(CertificateException e) { //인증서 확장자가 pfx인 경우(pkcs#12 포맷의 파일은 인증서, 개인키 내용을 파일 하나에 모두 담고 있다.)
			System.out.println("Not a der certificate");
		}
		
		/*
		 * siteVO에 정보 저장
		 * private String co_name;
		 * private String co_domain;
		 * private byte[] co_id;
		 * private byte[] co_pw;
		 */
		
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, String>> accList = (ArrayList<Map<String, String>>) req.get("account"); //accountList
		
		sv.setCo_name((String) req.get("Owner")); //co_name
		
		for(Map<String, String> acc : accList) {
			sv.setCo_domain((String)acc.get("site")); //co_domain
			sv.setCo_id((String)acc.get("id")); //co_id
			sv.setCo_pw((String)acc.get("pw")); //co_pw
		}
	}
	
	public static byte[] base64Decoder(byte[] encoded_bytes) {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes = decoder.decode(encoded_bytes);
		
		return decodedBytes;
	}
}
