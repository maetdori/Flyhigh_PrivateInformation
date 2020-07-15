package com.web.mapper;

import org.springframework.data.repository.query.Param;

import com.web.domain.CertVO;

public interface CertMapper {

	//search
	public CertVO certSearch(String co_name) throws Exception;
	
	//register
	public void certInsert(CertVO cert) throws Exception;
	
	//modify
	public void certUpdate(@Param("co_cert_pw") String co_cert_pw, @Param("co_name") String co_name) throws Exception;
	
	//delete
	public void certDelete(CertVO cert) throws Exception;
}
