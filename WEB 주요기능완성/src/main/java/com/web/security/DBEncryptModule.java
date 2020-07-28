package com.web.security;

import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.exception.WebException;
import com.web.service.KeyService;

public class DBEncryptModule {
	
	private static final Logger logger = LoggerFactory.getLogger(DBEncryptModule.class);
	
	public static void encryptCert(CertVO cert,String key) throws WebException {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		AES256Util aes = new AES256Util(keyBytes);
		String pw = cert.getCo_cert_pw();
		int type = cert.getCo_cert_type();
		String cert_der;
		String cert_key;
		String cert_pfx;
		if(type == 1) {
			byte[] cert_der_bytes = Base64.getDecoder().decode(cert.getCo_cert_der());
			cert_der = Base64.getEncoder().encodeToString(aes.encrypt(cert_der_bytes));
			cert.setCo_cert_der(cert_der);
			byte[] cert_key_bytes = Base64.getDecoder().decode(cert.getCo_cert_key());
			cert_key = Base64.getEncoder().encodeToString(aes.encrypt(cert_key_bytes));
			cert.setCo_cert_key(cert_key);
			
		} else if(type == 2){
			byte[] cert_pfx_bytes = Base64.getDecoder().decode(cert.getCo_certification());
			cert_pfx = Base64.getEncoder().encodeToString(aes.encrypt(cert_pfx_bytes));
			cert.setCo_certification(cert_pfx);
		}
		cert.setCo_cert_pw(Base64.getEncoder().encodeToString(aes.encrypt(pw.getBytes())));
	}
	
	public static void decryptCert(CertVO cert, String key) throws WebException {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		AES256Util aes = new AES256Util(keyBytes);
		String pw = cert.getCo_cert_pw();
		int type = cert.getCo_cert_type();
		String cert_der;
		String cert_key;
		String cert_pfx;
		if(type == 1) {
			logger.debug("Encrypted der : " + cert.getCo_cert_der());
			byte[] cert_der_bytes = Base64.getDecoder().decode(cert.getCo_cert_der());
			cert_der = Base64.getEncoder().encodeToString(aes.decrypt(cert_der_bytes));
			logger.debug("Decrypted der : " + cert_der);
			cert.setCo_cert_der(cert_der);
			byte[] cert_key_bytes = Base64.getDecoder().decode(cert.getCo_cert_key());
			cert_key = Base64.getEncoder().encodeToString(aes.decrypt(cert_key_bytes));
			cert.setCo_cert_key(cert_key);
			
		} else if(type == 2){
			byte[] cert_pfx_bytes = Base64.getDecoder().decode(cert.getCo_certification());
			cert_pfx = Base64.getEncoder().encodeToString(aes.decrypt(cert_pfx_bytes));
			cert.setCo_certification(cert_pfx);
		}
		cert.setCo_cert_pw(new String(aes.decrypt(Base64.getDecoder().decode(pw))));
	}
	
	public static void encryptSite(SiteVO site,String key) throws WebException {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		AES256Util aes = new AES256Util(keyBytes);
		String id = site.getCo_id();
		String pw = site.getCo_pw();
		site.setCo_id(Base64.getEncoder().encodeToString(aes.encrypt(id.getBytes())));
		
		site.setCo_pw(Base64.getEncoder().encodeToString(aes.encrypt(pw.getBytes())));
		
	}
	
	public static void decryptSite(SiteVO site,String key) throws WebException {
		byte[] keyBytes = Base64.getDecoder().decode(key);
		AES256Util aes = new AES256Util(keyBytes);
		String id = site.getCo_id();
		String pw = site.getCo_pw();
		site.setCo_id(new String(aes.decrypt(Base64.getDecoder().decode(id))));
		logger.debug("Decrypted id : " + site.getCo_id());
		site.setCo_pw(new String(aes.decrypt(Base64.getDecoder().decode(pw))));
		logger.debug("Decrypted pw : " + site.getCo_id());
	}
}
