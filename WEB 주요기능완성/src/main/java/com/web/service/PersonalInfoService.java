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
 
import com.web.domain.KeyVO;
import com.web.domain.PersonalInfoVO;
import com.web.exception.WebException;
import com.web.mapper.PersonalInfoMapper;
import com.web.security.DBEncryptModule;
 
@Service("com.web.service.PersonalInfoService")
public class PersonalInfoService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
    @Resource(name="com.web.mapper.PersonalInfoMapper")
    PersonalInfoMapper piMapper;
    
	@Resource(name="com.web.service.KeyService")
	KeyService keyService;
    
    
    public List<PersonalInfoVO> getPiList() throws DataAccessException, WebException{
    	
    	return piMapper.piList();
    }
    
    //데이터베이스에 name에 해당하는 정보가 존재하는지 여부 검사
    public boolean ifThereIsService(String co_name) throws DataAccessException, WebException {
    	if(piMapper.piSearch(co_name)!=null)
    		return true;
    	else 
    		return false;
    }
    
    public boolean validCheck(PersonalInfoVO pi) throws WebException{
    	if(pi.getCo_name() == null || pi.getCo_name().equals("")) {
    		throw new WebException("Name is wrong",WebException.PI_VALIDCHECK);
    	}
    	if(pi.getCo_kname() == null || pi.getCo_kname().equals("")) {
    		throw new WebException("kName is wrong",WebException.PI_VALIDCHECK);
    	}
    	
    	if(!pi.isCo_corp()) { // 개인사용자
    		if(pi.getCo_rrn1() == null || pi.getCo_rrn1().equals("")) {
        		throw new WebException("rrn1 is null",WebException.PI_VALIDCHECK);
        	}
        	if(pi.getCo_relation() == null || pi.getCo_relation().equals("") ||
        			(!pi.getCo_relation().equals("조부") && !pi.getCo_relation().equals("조모") &&
        			!pi.getCo_relation().equals("부") && !pi.getCo_relation().equals("모") &&
        			!pi.getCo_relation().equals("자녀") && !pi.getCo_relation().equals("형제") && 
        			!pi.getCo_relation().equals("배우자")) ||
        			pi.getCo_relation_name() == null || pi.getCo_relation().equals("")) {
        		throw new WebException("relation is wrong",WebException.PI_VALIDCHECK);
        	}
        	if(pi.getCo_house_hold() == null || pi.getCo_house_hold().equals("")) {
        		throw new WebException("household is null", WebException.PI_VALIDCHECK);
        	}
        	
    	} else {
    		if(pi.getCo_saupja_num() == null || pi.getCo_saupja_num().equals("")) {
    			throw new WebException("saupja_num is null", WebException.PI_VALIDCHECK);
    		}
    	}
    	
    	return true;
    }
    
    public PersonalInfoVO piSearchService(String co_name) throws DataAccessException, WebException{
		KeyVO key = keyService.getKeyService(co_name);
		PersonalInfoVO ret = piMapper.piSearch(co_name);
		if(ret == null) {
			throw new WebException("No such Name in tb_PERSON :" + co_name ,WebException.CS_CERT_SEARCH_NO_SUCH_NAME);
		}
		

		logger.debug("co_name : " + co_name);
		logger.debug("ret : " + ret);
		logger.debug("key : " + key);
		
		DBEncryptModule.decryptPi(ret, key.getCo_key());
		
		validCheck(ret);
    	return ret;
    }
    
    public void piInsertService(PersonalInfoVO pi) throws DataAccessException, WebException{
    	validCheck(pi);
    	
    	KeyVO key = keyService.getKeyService(pi.getCo_name());
    	
		DBEncryptModule.encryptPi(pi, key.getCo_key());
		
        piMapper.piInsert(pi);
    }
     
    public void piUpdateService(PersonalInfoVO pi) throws DataAccessException, WebException {
    	validCheck(pi);
    	
    	KeyVO key = keyService.getKeyService(pi.getCo_name());
    	DBEncryptModule.encryptPi(pi, key.getCo_key());
    	
    	
    	piMapper.piUpdate(pi);
    }
    
    public void piDeleteService(String co_name) throws DataAccessException, WebException {
    	keyService.keyDeleteService(co_name);
    	piMapper.piDelete(co_name);
    }
}
 