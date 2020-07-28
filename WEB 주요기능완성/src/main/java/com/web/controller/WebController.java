package com.web.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.exception.WebException;
import com.web.parse.ParseDer;
import com.web.service.CertService;
import com.web.service.SiteService;

@Controller
@RequestMapping("/private")
public class WebController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final static int INSERT = 0;
	private final static int MODIFY = 1;
	
	@Resource(name="com.web.service.CertService")
	private CertService certService;
	@Resource(name="com.web.service.SiteService")
	private SiteService siteService;
	
	@JsonFormat(pattern = "yyyyMMdd") 
	private LocalDate currentDate = LocalDate.now();
	
	private CertVO cv;
	private SiteVO sv;
	
	
	//인증서등록
	@PostMapping("/register")
	@ResponseBody
	private Map<String, Object> certRegister(@RequestBody Map<String, Object> req, HttpServletResponse resp) {
		try {
			resp.setContentType("application/json");
			resp.addHeader("Location", "http://localhost:8080/private/register");
			
			Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
			
			try {
				insertOrModify(req,INSERT); //데이터베이스에 req를 저장
			} catch (WebException e){
				logger.error(e.toString(),e);
				if(e.getCode() == WebException.WC_IOM_DATABASE_ERROR)
					resp.setStatus(500);
				else
					resp.setStatus(400);
				Map<String, Object> error = new HashMap<>();
				error.put("code",String.format("0x%x", e.getCode()));
				error.put("message",e.getMessage());
				return error;
			}
			
			Map<String, String> validity = new HashMap<>(); //validity map
			
			String activeDate = cv.getCo_active_date();
			
			validity.put("notBefore", activeDate);
			validity.put("notAfter", cv.getCo_exp_date());
			
			response.put("registerDate", currentDate);
			response.put("subject", cv.getCo_name());
			response.put("validity", validity);
			response.put("count", (int) req.get("count"));
			
			return response;
			
		} catch(Exception e) {
			logger.error(e.toString(),e);
			resp.setStatus(500);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x", WebException.WC_REGISTER));
			error.put("message","Unknown Error");
			return error;
		}
	}
	
	//인증서수정 (수정가능정보: cert_pw, account)
	//
	@PostMapping("/modify") 
	@ResponseBody
	private Map<String, Object> certModify(@RequestBody Map<String, Object> req, HttpServletResponse resp) {
		try {
			resp.setContentType("application/json");
			resp.addHeader("Location", "http://localhost:8080/private/modify");
			
			
			Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
			
			try {
				insertOrModify(req,MODIFY); //데이터베이스에 req를 저장
			} catch (WebException e){
				logger.error(e.toString(),e);
				if(e.getCode() == WebException.WC_IOM_DATABASE_ERROR)
					resp.setStatus(500);
				else
					resp.setStatus(400);
				Map<String, Object> error = new HashMap<>();
				error.put("code",String.format("0x%x", e.getCode()));
				error.put("message",e.getMessage());
			}
			
			
			Map<String, String> validity = new HashMap<>(); //validity map
			validity.put("notBefore", cv.getCo_active_date());
			validity.put("notAfter", cv.getCo_exp_date());
			
			response.put("registerDate", currentDate);
			response.put("subject", cv.getCo_name());
			response.put("validity", validity);
			response.put("count", (int) req.get("count"));
			
			return response;
		}  catch(Exception e) {
			logger.error(e.toString(),e);
			resp.setStatus(500);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x", WebException.WC_MODIFY));
			error.put("message","Unknown Error");
			return error;
		}
	}
	
	@PostMapping("/delete")
	@ResponseBody
	private Map<String, Object> certDelete(@RequestBody Map<String, Object> req, HttpServletResponse resp) {
		try {
			resp.setContentType("application/json");
			resp.addHeader("Location", "http://localhost:8080/private/delete");
			
			String co_name = (String)req.get("subject");
			
			if(co_name == null) {
				WebException e = new WebException("Invalid Name", WebException.WC_DELETE_INV_NAME);
				logger.error(e.toString(),e);
				resp.setStatus(400);
				Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
				error.put("code",String.format("0x%x", WebException.WC_DELETE_INV_NAME));
				error.put("message","Invalid Name");
				return error;
			}
			
			Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
			try {
				certService.certDeleteService(co_name);
				siteService.siteListDeleteService(co_name);
			} catch(DataAccessException e) {
				resp.setStatus(500);
				Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
				error.put("code",String.format("0x%x", WebException.WC_DELETE_DATABASE_ERROR));
				error.put("message","Error while Accessing Database");
				return error;
			}
			
			response.put("subject",co_name);
			return response;
		} catch(Exception e) {
			logger.error(e.toString(),e);
			resp.setStatus(500);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x", WebException.WC_DELETE));
			error.put("message","Unknown Error");
			return error;
		}
	}

	//certRegister()에서 호출하는 메소드
	//RequestBody로 들어온 정보를 VO에 저장
	private void insertOrModify(Map<String, Object> req,int mode) throws WebException {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> certification = (Map<String, String>) req.get("certification");
			cv = new CertVO();
			sv = new SiteVO();
			
			String der = certification.get("der");
			String key = certification.get("key");
			String pfx = certification.get("pfx");
			cv.setCo_name((String) req.get("subject")); //co_name
			cv.setCo_cert_pw((String) req.get("cert_pw")); //co_cert_pw
			
			cv.setCo_cert_der(der); //co_cert_der
			cv.setCo_cert_key(key);
			cv.setCo_certification(pfx);
			
			if(cv.getCo_name() == null || cv.getCo_cert_pw() == null) {
				throw new WebException("Invalid name or pw", WebException.WC_IOM_INV_NAME_PW);
			}
			
			logger.info("co_name : "  + cv.getCo_name());
			logger.info("co_cert_pw : "  + cv.getCo_cert_pw());
			
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
			
			
			if(certification.get("der") != null) { //인증서 확장자가 der인 경우 
				try { 
					byte[] certBytes = ((String) certification.get("der")).getBytes(); //certification StringToByte
					byte[] cert_decoded = base64Decoder(certBytes); //certification decoding
					ParseDer cert_parsed = new ParseDer(cert_decoded);
					cv.setCo_active_date(cert_parsed.getNotBefore()); //co_active_date
					cv.setCo_exp_date(cert_parsed.getNotAfter()); //co_exp_date
					cv.setCo_cert_type(1);
					
				} catch(CertificateException | IllegalArgumentException e) { 
					throw new WebException("Invalid DER format", WebException.WC_IOM_INV_DER,e);//catch caluse
				}
				
				if(cv.getCo_cert_key() == null) {
					throw new WebException("null key for given der",WebException.WC_IOM_NO_KEY);
				}
				
			} else if(certification.get("pfx") != null) {
				try {
					byte[] bpfx = base64Decoder(cv.getCo_certification().getBytes());
					KeyStore keystore = KeyStore.getInstance("PKCS12");
			        InputStream is = new ByteArrayInputStream(bpfx);
			        keystore.load(is, cv.getCo_cert_pw().toCharArray());
			        String alias = keystore.aliases().nextElement();
			        X509Certificate certificate = (X509Certificate) keystore.
			                getCertificate(alias);
			        ParseDer cert_parsed = new ParseDer(certificate);
			        cv.setCo_cert_type(2);
			        cv.setCo_active_date(cert_parsed.getNotBefore()); //co_active_date
					cv.setCo_exp_date(cert_parsed.getNotAfter()); //co_exp_date
				} catch (CertificateException | IllegalArgumentException e) {
					throw new WebException("Invalid PFX format",WebException.WC_IOM_INV_PFX,e);//catch caluse
				}
			} else {
				if(mode == INSERT) {
					throw new WebException("no der or pfx",WebException.WC_IOM_NO_CERT);
				}
			}
			logger.info("co_cert_der : " + cv.getCo_cert_der());
			logger.info("co_cert_key : " + cv.getCo_cert_key());
			logger.info("co_certification : " + cv.getCo_certification());
			logger.info("co_notBefore : "  + cv.getCo_active_date());
			logger.info("co_notAfter : "  + cv.getCo_exp_date());
			logger.info("co_cert_type : "  + cv.getCo_cert_type());
	
			
			try {
			if(mode == INSERT)
				certService.certInsertService(cv);
			else if(mode == MODIFY)
				certService.certUpdateService(cv);
			} catch (DataAccessException e) {
				throw new WebException("Error while Accessing Database",WebException.WC_IOM_DATABASE_ERROR ,e);
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
			if(accList == null) {
				throw new WebException("Invalid_Account",WebException.WC_IOM_INV_SITE_INFO);
			}
			
			sv.setCo_name((String) req.get("subject")); //co_name
			try {
				siteService.siteListDeleteService(sv.getCo_name());
				for(Map<String, String> acc : accList) {
					sv.setCo_domain((String)acc.get("site")); //co_domain
					sv.setCo_id((String)acc.get("id")); //co_id
					sv.setCo_pw((String)acc.get("pw")); //co_pw
					if(sv.getCo_id().equals("") || sv.getCo_domain().equals("") || sv.getCo_pw().equals("")) {
						throw new WebException("Invalid_Account",WebException.WC_IOM_INV_SITE_INFO);
					}
					siteService.siteInsertService(sv);
				} 
			} catch (DataAccessException e) {
				throw new WebException("Error while Accessing Database",WebException.WC_IOM_DATABASE_ERROR ,e);
			}
		} catch(WebException ee) {
			throw ee;
		} catch(Exception e) {
			throw new WebException("Unknown Error", WebException.INSERT_OR_MODIFY, e);
		}
	}
	
	//certModify()에서 호출하는 메소드
	//tb_siteInfo를 update
	/*private void updateSite(int flag, ArrayList<Map<String, String>> accountList, String co_name) throws Exception {
		
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
			}
			break;
		
		//flag==2: 기존 account 수정	- pw만 수정 가능
		case 2:
			for(Map<String, String> acc : accountList) {
				HashMap<String, String> param = new HashMap<>();
				param.put("co_name", co_name);
				param.put("co_domain", (String)acc.get("site"));
				param.put("co_pw", (String)acc.get("pw"));
				siteService.siteUpdateService(param);
			}
			break;
		
		//flag==3: account 삭제 
		case 3:
			for(Map<String, String> acc : accountList) {
				siteService.siteDeleteService(co_name, (String)acc.get("site"));
			}
			break;
			
		default:
			System.out.println("Invalid flag");
			break;
		}
	}*/
	
	//setVO()에서 호출하는 메소드
	//base64 decoder
	public static byte[] base64Decoder(byte[] encoded_bytes) throws IllegalArgumentException {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes = decoder.decode(encoded_bytes);
		
		return decodedBytes;
	}
}
