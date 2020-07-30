package com.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.exception.WebException;
import com.web.security.ResponseEncryptModule;
import com.web.service.CertService;
import com.web.service.SiteService;

@Controller
@RequestMapping("/plogrivate")
public class WasController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name="com.web.service.CertService")
	private CertService certService;
	@Resource(name="com.web.service.SiteService")
	private SiteService siteService;
	
	@PostMapping(value = "/getList")
	@ResponseBody
    public Map<String, Object> getList(HttpServletResponse resp) {
		try {
			resp.setContentType("application/json");
			resp.addHeader("Location", "http://localhost:8080/private/getList");
			List<CertVO> certList = certService.getCertList(); //인증서 리스트(CertVO 타입)
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
		} catch(DataAccessException e) {
			WebException ee = new WebException("Error while Accessing Database", WebException.WSC_GETLIST,e);
			logger.error(ee.toString(),ee);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x",ee.getCode() ));
			error.put("message",ee.getMessage());
			return error;
		} catch(WebException e) {
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x", e.getCode()));
			error.put("message",e.getMessage());
			logger.debug("error Sent");
			return error;
		} catch (Exception e) {
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x", WebException.WSC_GETLIST));
			error.put("message",e.getMessage());
			logger.debug("error Sent");
			return error;
		}
    }
	
	@PostMapping(value = "/getInfo") 
	@ResponseBody
	public Map<String, Object> getInfo(@RequestBody Map<String, Object> req, HttpServletResponse resp) {
		try {
			resp.setContentType("application/json");
			resp.addHeader("Location", "http://localhost:8080/private/getInfo");
			
			String co_name = (String) req.get("subject"); //request의 subject값을 co_name에 저장
			
			CertVO cert = certService.certSearchService(co_name); //찾은 인증서(CertVO 타입)
			List<SiteVO> siteList = siteService.siteListService(co_name); //찾은 사이트 리스트(SiteVo 타입)
			ArrayList<Map<String, Object>> countList = new ArrayList<>(); //count 리스트
			Map<String, Object> response = new HashMap<>(); //리턴할 HashMap
			for(SiteVO s : siteList) {
				Map<String, Object> count = new HashMap<>(); //Count Map
				count.put("site", s.getCo_domain());
				count.put("id", s.getCo_id());
				count.put("pw", s.getCo_pw());
				countList.add(count);
			}
			if(cert.getCo_name() == null) {
				WebException e = new WebException("No such name", WebException.WSC_GETINFO_NO_SUCH_NAME);
				Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
				error.put("code",String.format("0x%x", e.getCode()));
				error.put("message",e.getMessage());
				return error;
			}
			logger.info("cert_pw : " + cert.getCo_cert_pw());
			logger.info("cert_type : " + cert.getCo_cert_type());
			logger.info("cert_der : " + cert.getCo_cert_der());
			logger.info("cert_key : " + cert.getCo_cert_key());
			logger.info("cert_pfx : " + cert.getCo_certification());
			
			response.put("account", countList);
			response.put("count", siteList.size());
			response.put("cert_pw", cert.getCo_cert_pw());
			Map<String,String> certification = new HashMap<>();
			String der = cert.getCo_cert_der();
			int cert_type = cert.getCo_cert_type();
			if(cert_type == 1) {
				certification.put("der", der);
				certification.put("key", cert.getCo_cert_key());
				certification.put("pfx", null);
				response.put("certification", certification);
				response.put("cert_type", cert_type);
				try {
					ResponseEncryptModule.encrypt("keystore2.p12","123456",
							new String[] {"cert_pw", "certification/der","certification/key", "account/site", "account/id", "account/pw"},req,response);
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
					ResponseEncryptModule.encrypt("keystore2.p12","123456",
							new String[] {"cert_pw", "certification/pfx", "account/site", "account/id", "account/pw"},req,response);
				} catch (WebException e) {
					logger.error(e.toString(),e);
					Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
					error.put("code",String.format("0x%x", e.getCode()));
					error.put("message",e.getMessage());
					return error;
				}
			} else {
				WebException ee = new WebException("CertType must 1 or 2", WebException.WSC_GETLIST);
				logger.error(ee.toString(),ee);
				Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
				error.put("code",String.format("0x%x",ee.getCode() ));
				error.put("message",ee.getMessage());
				return error;
			}
			
			logger.debug("send response");
			return response;
		}catch (WebException e){
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>();
			error.put("code",String.format("0x%x", e.getCode()));
			error.put("message",e.getMessage());
			return error;
		}
		catch(DataAccessException e) {
			WebException ee = new WebException("Error while Accessing Database", WebException.WSC_GETLIST,e);
			logger.error(ee.toString(),ee);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x",ee.getCode() ));
			error.put("message",ee.getMessage());
			return error;
		} catch (Exception e) {
			logger.error(e.toString(),e);
			Map<String, Object> error = new HashMap<>(); //리턴할 HashMap
			error.put("code",String.format("0x%x", WebException.WSC_GETINFO));
			error.put("message",e.getMessage());
			logger.debug("error Sent");
			return error;
		}
	}
	
	
}

