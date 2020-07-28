package com.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.web.domain.KeyVO;

@Repository("com.web.mapper.KeyMapper")
public @Mapper interface KeyMapper {
	public KeyVO getKey(String co_name) throws DataAccessException;

	public void keyInsert(KeyVO key) throws DataAccessException;
	
	public void keyDelete(String co_name) throws DataAccessException;
}
