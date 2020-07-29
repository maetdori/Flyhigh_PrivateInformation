package com.web.service;
 
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
 
import com.web.domain.CertVO;
import com.web.domain.KeyVO;
import com.web.exception.WebException;
import com.web.mapper.CertMapper;
import com.web.security.DBEncryptModule;
 
@Service("com.web.service.CertService")
public class CertService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
    @Resource(name="com.web.mapper.CertMapper")
    CertMapper certMapper;
    
	@Resource(name="com.web.service.KeyService")
	KeyService keyService;
    
    
    public List<CertVO> getCertList() throws DataAccessException{
    	
    	return certMapper.certList();
    }
    
    public CertVO certSearchService(String co_name) throws DataAccessException, WebException{
		KeyVO key = keyService.getKeyService(co_name);
		CertVO ret = certMapper.certSearch(co_name);
		logger.debug("co_name : " + co_name);
		logger.debug("ret : " + ret);
		logger.debug("key : " + key);
		DBEncryptModule.decryptCert(ret, key.getCo_key());
    	return ret;
    }
    
    public void certInsertService(CertVO cert) throws DataAccessException, WebException{
    	KeyVO key = new KeyVO();
    	byte[] keyBytes = new byte[32];
    	SecureRandom rand = new SecureRandom();
    	rand.nextBytes(keyBytes);
    	key.setCo_key(Base64.getEncoder().encodeToString(keyBytes));
    	key.setCo_name(cert.getCo_name());
    	keyService.keyInsertService(key);
    	
		DBEncryptModule.encryptCert(cert, key.getCo_key());
		
        certMapper.certInsert(cert);
    }
     
    public void certUpdateService(CertVO cert) throws DataAccessException, WebException {
    	KeyVO key = keyService.getKeyService(cert.getCo_name());
    	DBEncryptModule.encryptCert(cert, key.getCo_key());
    	
    	
    	certMapper.certUpdate(cert);
    }
    
    public void certDeleteService(String co_name) throws DataAccessException {
    	keyService.keyDeleteService(co_name);
    	certMapper.certDelete(co_name);
    }
}
 