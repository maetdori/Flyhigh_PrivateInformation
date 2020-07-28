package com.web.service;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.web.domain.KeyVO;
import com.web.mapper.KeyMapper;

@Service("com.web.service.KeyService")
public class KeyService {
	
    @Resource(name="com.web.mapper.KeyMapper")
    KeyMapper keyMapper;
	
	public KeyVO getKeyService(String co_name) throws DataAccessException {
		return keyMapper.getKey(co_name);
	}

	public void keyInsertService(KeyVO key) throws DataAccessException {
		keyMapper.keyInsert(key);
	}
	
	public void keyDeleteService(String co_name) throws DataAccessException {
		keyMapper.keyDelete(co_name);
	}
}
