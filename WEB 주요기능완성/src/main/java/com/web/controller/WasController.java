package com.web.controller;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.consts.WASJSONConsts;
import com.web.domain.CertVO;
import com.web.domain.PersonalInfoVO;
import com.web.domain.SiteVO;
import com.web.exception.WebException;
import com.web.security.ResponseEncryptModule;
import com.web.service.CertService;
import com.web.service.PersonalInfoService;
import com.web.service.SiteService;
import com.web.service.UserService;

@Controller
@RequestMapping(WASJSONConsts.METHOD_PATH)
public class WasController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${response-encrypt-module.key-store}")
	private String keyStorePath;
	
	@Value("${response-encrypt-module.key-store-password}")
	private String keyStorePassword;
	
	@Resource(name="com.web.service.CertService")
	private CertService certService;
	@Resource(name="com.web.service.SiteService")
	private SiteService siteService;
	@Resource(name="com.web.service.UserService")
	private UserService userService;
	@Resource(name="com.web.service.PersonalInfoService")
	private PersonalInfoService piService;
	
	@PostMapping(value = WASJSONConsts.GET_LIST)
	@ResponseBody
    public Map<String, Object> getList(@RequestHeader(value = WASJSONConsts.REQ_HDR_CONTENT_TYPE)String contentType,
			@RequestHeader(value = WASJSONConsts.REQ_HDR_DEVICE_ID)String deviceId,
			@RequestHeader(value = WASJSONConsts.REQ_HDR_SERVER_CERT_ID)String serverCertId,
			@RequestBody Map<String, Object> req,
			HttpServletResponse resp) {
		try {
			String co_username = (String) req.get(WASJSONConsts.STRING_USERNAME);
			String co_android_id = deviceId;
			logger.debug("contentType : " + contentType);
			logger.debug("Device-Id : " + deviceId);
			logger.debug("Server-Cert-Id : " + serverCertId);
			logger.debug("username : " + co_username);
			
			userService.getUserService(co_username, co_android_id);
			//User가 없다면 WebException발생하고 오류 리턴
			
			resp.setContentType(WASJSONConsts.RES_HDR_CONTENT_TYPE_VAL);
			resp.addHeader(WASJSONConsts.RES_HDR_LOCATION,
					WASJSONConsts.RES_HDR_LOCATION_VAL + WASJSONConsts.METHOD_PATH + WASJSONConsts.GET_LIST);
			List<CertVO> certList = certService.getCertList(); //인증서 리스트(CertVO 타입)
			ArrayList<Map<String, Object>> accList = new ArrayList<>(); //account 리스트
			Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
			
			for(CertVO c : certList) {
				Map<String, String> validity = new HashMap<>(); //Validity Map
				validity.put(WASJSONConsts.STRING_NOT_BEFORE, c.getCo_active_date());
				validity.put(WASJSONConsts.STRING_NOT_AFTER, c.getCo_exp_date());
				Map<String, Object> account = new HashMap<>(); //Account Map
				account.put(WASJSONConsts.STRING_SUBJECT, c.getCo_name());
				account.put(WASJSONConsts.JO_VALIDITY, validity);
				accList.add(account);
			}
		
			response.put(WASJSONConsts.STRING_COUNT, certList.size());
			response.put(WASJSONConsts.JO_ACCOUNT, accList);
			
			return response;
		} catch(DataAccessException e) {
			WebException ee = new WebException("Error while Accessing Database", WebException.WSC_GETLIST,e);
			logger.error(ee.toString(),ee);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x",ee.getCode() ));
			error.put(WASJSONConsts.STRING_ERROR_MESSAGE,ee.getMessage());
			return error;
		} catch(WebException e) {
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x", e.getCode()));
			error.put(WASJSONConsts.STRING_ERROR_MESSAGE,e.getMessage());
			logger.debug("error Sent");
			return error;
		} catch (Exception e) {
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x", WebException.WSC_GETLIST));
			error.put(WASJSONConsts.STRING_ERROR_MESSAGE,e.getMessage());
			logger.debug("error Sent");
			return error;
		}
    }
	
	@PostMapping(value = WASJSONConsts.GET_INFO) 
	@ResponseBody
	public Map<String, Object> getInfo(@RequestHeader(value = WASJSONConsts.REQ_HDR_CONTENT_TYPE)String contentType,
			@RequestHeader(value = WASJSONConsts.REQ_HDR_DEVICE_ID)String deviceId,
			@RequestHeader(value = WASJSONConsts.REQ_HDR_SERVER_CERT_ID)String serverCertId,@RequestBody Map<String, Object> req, HttpServletResponse resp) {
		
		try {
			String co_username = (String) req.get(WASJSONConsts.STRING_USERNAME);
			String co_android_id = deviceId;
			logger.debug("contentType : " + contentType);
			logger.debug("Device-Id : " + deviceId);
			logger.debug("Server-Cert-Id : " + serverCertId);
			logger.debug("username : " + co_username);
			
			userService.getUserService(co_username, co_android_id);
			//User가 없다면 WebException발생하고 오류 리턴
			resp.setContentType(WASJSONConsts.RES_HDR_CONTENT_TYPE_VAL);
			resp.addHeader(WASJSONConsts.RES_HDR_LOCATION, WASJSONConsts.RES_HDR_LOCATION_VAL + WASJSONConsts.METHOD_PATH +WASJSONConsts.GET_INFO);
			
			String co_name = (String) req.get(WASJSONConsts.STRING_SUBJECT); //request의 subject값을 co_name에 저장
			
			PersonalInfoVO pv = piService.piSearchService(co_name);//찾은 개인정보
			CertVO cert = certService.certSearchService(co_name); //찾은 인증서(CertVO 타입)
			List<SiteVO> siteList = siteService.siteListService(co_name); //찾은 사이트 리스트(SiteVo 타입)
			ArrayList<Map<String, Object>> countList = new ArrayList<>(); //count 리스트
			Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
			for(SiteVO s : siteList) {
				Map<String, Object> count = new HashMap<>(); //Count Map
				count.put(WASJSONConsts.STRING_DOMAIN, s.getCo_domain());
				count.put(WASJSONConsts.STRING_ID, s.getCo_id());
				count.put(WASJSONConsts.STRING_PW, s.getCo_pw());
				countList.add(count);
			}
			if(cert.getCo_name() == null) {
				WebException e = new WebException("No such name", WebException.WSC_GETINFO_NO_SUCH_NAME);
				Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
				error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x", e.getCode()));
				error.put(WASJSONConsts.STRING_ERROR_MESSAGE,e.getMessage());
				return error;
			}
			logger.info("cert_pw : " + cert.getCo_cert_pw());
			logger.info("cert_type : " + cert.getCo_cert_type());
			logger.info("cert_der : " + cert.getCo_cert_der());
			logger.info("cert_key : " + cert.getCo_cert_key());
			logger.info("cert_pfx : " + cert.getCo_certification());
			
			response.put(WASJSONConsts.JO_ACCOUNT, countList);
			response.put(WASJSONConsts.STRING_COUNT, siteList.size());
			response.put(WASJSONConsts.STRING_CERT_PW, cert.getCo_cert_pw());
			
			Map<String,Object> personalInfo = new HashMap<>();
			personalInfo.put(WASJSONConsts.STRING_KNAME, pv.getCo_kname());
			personalInfo.put(WASJSONConsts.STRING_ENAME, pv.getCo_ename());
			personalInfo.put(WASJSONConsts.BOOLEAN_CORP, pv.isCo_corp());
			personalInfo.put(WASJSONConsts.STRING_ADDR1, pv.getCo_addr1());
			personalInfo.put(WASJSONConsts.STRING_ADDR2, pv.getCo_addr2());
			personalInfo.put(WASJSONConsts.STRING_ADDR3, pv.getCo_addr3());
			personalInfo.put(WASJSONConsts.STRING_CAR, pv.getCo_car());
			personalInfo.put(WASJSONConsts.STRING_HOJUK_NAME, pv.getCo_hojuk_name());
			personalInfo.put(WASJSONConsts.STRING_HOUSE_HOLD, pv.getCo_house_hold());
			personalInfo.put(WASJSONConsts.STRING_RELATION, pv.getCo_relation());
			personalInfo.put(WASJSONConsts.STRING_RELATION_NAME, pv.getCo_relation_name());
			personalInfo.put(WASJSONConsts.STRING_RRN1,pv.getCo_rrn1());
			personalInfo.put(WASJSONConsts.STRING_RRN2,pv.getCo_rrn2());
			personalInfo.put(WASJSONConsts.STRING_SAUPJA_NUM,pv.getCo_saupja_num());
			personalInfo.put(WASJSONConsts.STRING_TEL,pv.getCo_tel());
			response.put(WASJSONConsts.JO_PERSONALINFO, personalInfo);
			
		
			Map<String,String> certification = new HashMap<>();
			String der = cert.getCo_cert_der();
			int cert_type = cert.getCo_cert_type();
			if(cert_type == 1) {
				certification.put(WASJSONConsts.STRING_DER, der);
				certification.put(WASJSONConsts.STRING_KEY, cert.getCo_cert_key());
				certification.put(WASJSONConsts.STRING_PFX, null);
				response.put(WASJSONConsts.JO_CERTIFICATION, certification);
				response.put(WASJSONConsts.INTEGER_CERT_TYPE, cert_type);
				try {
					ResponseEncryptModule.encrypt(keyStorePath,keyStorePassword,
							new String[] {WASJSONConsts.STRING_CERT_PW, 
									WASJSONConsts.JO_CERTIFICATION + "/" + WASJSONConsts.STRING_DER,
									WASJSONConsts.JO_CERTIFICATION + "/" + WASJSONConsts.STRING_KEY,
									WASJSONConsts.JO_ACCOUNT + "/" + WASJSONConsts.STRING_DOMAIN,
									WASJSONConsts.JO_ACCOUNT + "/" + WASJSONConsts.STRING_ID,
									WASJSONConsts.JO_ACCOUNT + "/" + WASJSONConsts.STRING_PW,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_RRN1,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_RRN2,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_TEL,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_SAUPJA_NUM},req,response);
				} catch (WebException e) {
					logger.error(e.toString(),e);
					Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
					error.put("code",String.format("0x%x", e.getCode()));
					error.put("message",e.getMessage());
					return error;
				}
			}
			else if (cert_type == 2){
				certification.put("der", null);
				certification.put("key", null);
				certification.put("pfx", cert.getCo_certification());
				response.put("certification", certification);
				response.put("cert_type", cert_type);
				try {
					ResponseEncryptModule.encrypt(keyStorePath,keyStorePassword,
							new String[] {WASJSONConsts.STRING_CERT_PW, 
									WASJSONConsts.JO_CERTIFICATION + "/" + WASJSONConsts.STRING_PFX,
									WASJSONConsts.JO_ACCOUNT + "/" + WASJSONConsts.STRING_DOMAIN,
									WASJSONConsts.JO_ACCOUNT + "/" + WASJSONConsts.STRING_ID,
									WASJSONConsts.JO_ACCOUNT + "/" + WASJSONConsts.STRING_PW,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_RRN1,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_RRN2,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_TEL,
									WASJSONConsts.JO_PERSONALINFO + "/" + WASJSONConsts.STRING_SAUPJA_NUM},req,response);
				} catch (WebException e) {
					logger.error(e.toString(),e);
					Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
					error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x", e.getCode()));
					error.put(WASJSONConsts.STRING_ERROR_MESSAGE,e.getMessage());
					return error;
				}
			} else {
				WebException ee = new WebException("CertType must 1 or 2", WebException.WSC_GETLIST);
				logger.error(ee.toString(),ee);
				Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
				error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x",ee.getCode() ));
				error.put(WASJSONConsts.STRING_ERROR_MESSAGE,ee.getMessage());
				return error;
			}
			
			logger.debug("send response");
			return response;
		}catch (WebException e){
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>();
			error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x", e.getCode()));
			error.put(WASJSONConsts.STRING_ERROR_MESSAGE,e.getMessage());
			return error;
		}
		catch(DataAccessException e) {
			WebException ee = new WebException("Error while Accessing Database", WebException.WSC_GETLIST,e);
			logger.error(ee.toString(),ee);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x",ee.getCode() ));
			error.put(WASJSONConsts.STRING_ERROR_MESSAGE,ee.getMessage());
			return error;
		} catch (Exception e) {
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put(WASJSONConsts.STRING_ERROR_CODE,String.format("0x%x", WebException.WSC_GETINFO));
			error.put(WASJSONConsts.STRING_ERROR_MESSAGE,e.getMessage());
			logger.debug("error Sent");
			return error;
		}
	}
	
	@GetMapping(value = WASJSONConsts.GET_CERT)
    @ResponseBody
    public Map<String,String> getKey() throws WebException {
        X509Certificate certificate = (X509Certificate) ResponseEncryptModule.loadCertFromKeyStore(keyStorePath,keyStorePassword);
        Map<String,String> ret = new HashMap<>();
        try {
			ret.put(WASJSONConsts.STRING_CERT_BASE64,new String(Base64.getEncoder().encode(certificate.getEncoded())));
		} catch (CertificateEncodingException e) {
			throw new WebException("Failed get encoded certificate", WebException.RENCM_GETKEY_CERT_ENCODING_ERROR,e);
		}
        return ret;
    }
	
//	@PostMapping(value = "/test") 
//	@ResponseBody
//	public Map<String, Object> test() {
//		//테스트용
//			Exception e = new Exception();
//			logger.error(e.toString(),e);
//			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
//			error.put("code",String.format("0x%x", WebException.WSC_GETINFO));
//			error.put("message",e.getMessage());
//			logger.debug("error Sent");
//			return error;
//	}
	
	
}

