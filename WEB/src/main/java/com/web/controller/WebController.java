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
import com.web.parse.ParseX509;
import com.web.service.CertService;

@Controller
@RequestMapping("private")
public class WebController {
	
	@Resource(name="com.web.service.CertService")
	CertService certService;
	
	CertVO cv = new CertVO();
	SiteVO sv = new SiteVO();
	
	@RequestMapping("/register") //인증서등록
	private Map<String, Object> certRegister(@RequestBody Map<String, Object> req, HttpServletResponse resp) throws Exception {
		
		resp.setContentType("application/json");
		resp.addHeader("Location", "http://localhost:8080/private/register");
		
		Map<String, Object> response = new HashMap(); //리턴할 hashMapi
		
		setVO(req);
		
		return response;
	}
	
	//RequestBody로 들어온 정보를 VO에 저장
	private void setVO(Map<String, Object> req) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, IOException {
		
		byte[] cert_decoded = base64Decoder((byte[]) req.get("certification")); //certification decoding
		
		/*
		 * certVO에 정보 저장
		 * private String co_name; //사용자이름
		 * private String co_active_date; //시작일
		 * private String co_exp_date; //만료일
		 * private byte[] co_cert_pw; //인증서비밀번호
		 * private byte[] co_cert_der; //der 인증서
		 * private byte[] co_cert_key; //key 파일
		 * private byte[] co_certification; //pkcs#12 인증서
		 */
		this.cv.setCo_name((String) req.get("Owner")); //co_name
		this.cv.setCo_cert_pw((byte[]) req.get("cert_pw")); //co_cert_pw
		
		try { //인증서가 der파일인 경우 
			ParseX509 cert_parsed = new ParseX509(cert_decoded);
			this.cv.setCo_active_date(cert_parsed.getNotBefore()); //co_active_date
			this.cv.setCo_exp_date(cert_parsed.getNotAfter()); //co_exp_date
			this.cv.setCo_cert_der((byte[]) req.get("certification")); //co_cert_der
		} catch(CertificateException e) {
			
		}
		
		int count = (int) req.get("count"); //로그인정보개수
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, Object>> accList = (ArrayList<Map<String, Object>>) req.get("account"); //accountList
		
		/*
		 * siteVO에 정보 저장
		 * 	private String co_name;
		 * private String co_domain;
		 * private byte[] co_id;
		 * private byte[] co_pw;
		 */
		this.sv.setCo_name((String) req.get("Owner")); //co_name
		
		for(Map<String, Object> acc : accList) {
			this.sv.setCo_domain((String)acc.get("site")); //co_domain
			this.sv.setCo_id((byte[])acc.get("id")); //co_id
			this.sv.setCo_pw((byte[])acc.get("pw")); //co_pw
		}
	}
	
	public static byte[] base64Decoder(byte[] encoded_bytes) {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes = decoder.decode(encoded_bytes);
		
		return decodedBytes;
	}
}
