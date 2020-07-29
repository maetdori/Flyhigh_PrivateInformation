package com.web.service;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.web.domain.KeyVO;
import com.web.exception.WebException;
import com.web.mapper.KeyMapper;

@Service("com.web.service.KeyService")
public class KeyService {
	
    @Resource(name="com.web.mapper.KeyMapper")
    KeyMapper keyMapper;
	
	public KeyVO getKeyService(String co_name) throws DataAccessException, WebException{
		KeyVO ret =  keyMapper.getKey(co_name);
		if(ret == null) {
			throw new WebException("No such name in tb_key :" + co_name,WebException.KS_GETKS_NO_SUCH_NAME);
		}
		return ret;
		
	}

	public void keyInsertService(KeyVO key) throws DataAccessException, WebException {
		keyMapper.keyInsert(key);
	}
	
	public void keyDeleteService(String co_name) throws DataAccessException, WebException {
		keyMapper.keyDelete(co_name);
	}
}
