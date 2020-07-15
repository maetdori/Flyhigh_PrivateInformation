package com.web.mapper;

import com.web.domain.CertVO;

public interface CertMapper {

	//registerPrivateInfo
	public int certInsert(CertVO cert) throws Exception;
}
