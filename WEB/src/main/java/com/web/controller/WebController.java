package com.web.controller;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.parse.ParseDer;
import com.web.service.CertService;
import com.web.service.SiteService;

@Controller
@RequestMapping("/private")
public class WebController {
	
	@Resource(name="com.web.service.CertService")
	private CertService certService;
	@Resource(name="com.web.service.SiteService")
	private SiteService siteService;
	
	@JsonFormat(pattern = "yyyy-MM-dd") 
	private LocalDate currentDate = LocalDate.now();
	
	private CertVO cv;
	private SiteVO sv;
	
	//인증서등록
	@RequestMapping("/register")
	private Map<String, Object> certRegister(@RequestBody Map<String, Object> req, HttpServletResponse resp) throws Exception {
	
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/register");
		
		
		Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
		
		cv = new CertVO();
		sv = new SiteVO();
		setVO(req, cv, sv); //CertVO와 SiteVO setting
		
		certService.certInsertService(cv); 
		siteService.siteInsertService(sv);
		
		Map<String, String> validity = new HashMap<>(); //validity map
		validity.put("NotBefore", cv.getCo_active_date());
		validity.put("NotAfter", cv.getCo_exp_date());
		
		response.put("registerDate", currentDate);
		response.put("Subject", cv.getCo_name());
		response.put("validity", validity);
		response.put("count", (int) req.get("count"));
		
		return response;
	}
	
	//인증서수정 (수정가능정보: cert_pw, account)
	//Client는 수정하고 싶은 정보만 RequestBody에 담아 보낸다.
	//req.containsKey()를 이용해 RequestBody가 어떤 key를 가지고 있는지 확인하고 key에 해당하는 정보를 수정한다. 
	@RequestMapping("/modify") 
	private Map<String, Object> certModify(@RequestBody Map<String, Object> req, HttpServletResponse resp) throws Exception {
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/modify");
		
		Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
		
		if(req.containsKey("cert_pw")) { //인증서 패스워드 수정하는 경우
			cv = new CertVO();
			certService.certUpdateService((String)req.get("cert_pw"), (String)req.get("owner"));
		
			if(req.containsKey("flag")) { //사이트 정보 수정하는 경우
		
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, String>> accountList = (ArrayList<Map<String, String>>) req.get("account"); //flag와 함께 요청된 수정할 accountList 
				
				updateSite((int)req.get("flag"), accountList, (String)req.get("owner"));
			}
		}
		
		Map<String, String> validity = new HashMap<>(); //validity map
		validity.put("NotBefore", cv.getCo_active_date());
		validity.put("NotAfter", cv.getCo_exp_date());
		
		response.put("Subject", cv.getCo_name());
		response.put("validity", validity);
		response.put("count", (int) req.get("count"));
		
		return response;
	}
	
	//인증서삭제 (인증서로 로그인할 수 있는 사이트 정보도 삭제)
	@RequestMapping("/delete")
	private Map<String, String> certDelete(String subject, HttpServletResponse resp) throws Exception {
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/delete");
		
		Map<String, String> response = new HashMap<>(); //리턴할 HashMap
		
		certService.certDeleteService(subject);
		siteService.siteListDeleteService(subject);
	
		response.put("Subject", cv.getCo_name());
		
		return response;
	}
	
	//certRegister()에서 호출하는 메소드
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
	
	//certModify()에서 호출하는 메소드
	//tb_siteInfo를 update
	private void updateSite(int flag, ArrayList<Map<String, String>> accountList, String co_name) throws Exception {
		
		sv = new SiteVO();
		
		switch(flag) {
		
		//flag==1: account 추가
		case 1: 
			for(Map<String,String> acc : accountList) {
				sv.setCo_name(co_name);
				sv.setCo_domain((String)acc.get("site"));
				sv.setCo_id((String)acc.get("id"));
				sv.setCo_pw((String)acc.get("pw"));
				siteService.siteInsertService(sv);
				break;
			}
		
		//flag==2: 기존 account 수정	- pw만 수정 가능
		case 2:
			for(Map<String, String> acc : accountList) {
				HashMap<String, String> param = new HashMap<>();
				param.put("co_name", co_name);
				param.put("co_domain", (String)acc.get("site"));
				param.put("co_pw", (String)acc.get("pw"));
				siteService.siteUpdateService(param);
				break;
			}
		
		//flag==3: account 삭제 
		case 3:
			for(Map<String, String> acc : accountList) {
				siteService.siteDeleteService(co_name, (String)acc.get("site"));
			break;
			}
		default:
			System.out.println("Invalid flag");
			break;
		}
	}
	
	//setVO()에서 호출하는 메소드
	//base64 decoder
	public static byte[] base64Decoder(byte[] encoded_bytes) {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes = decoder.decode(encoded_bytes);
		
		return decodedBytes;
	}
}