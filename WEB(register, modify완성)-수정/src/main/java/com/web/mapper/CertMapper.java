package com.web.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.domain.CertVO;

@Repository("com.web.mapper.CertMapper")
public @Mapper interface CertMapper {
	
	//list
	public ArrayList<CertVO> certList();

	//search
	public CertVO certSearch(String co_name) throws Exception;
	
	//register
	public void certInsert(CertVO cert) throws Exception;
	
	//modify
	public void certUpdate(CertVO cert) throws Exception;
	
	//delete
	public void certDelete(String co_name) throws Exception;
}
