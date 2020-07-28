package com.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.web.domain.CertVO;
import com.web.domain.SiteVO;


@Repository("com.web.mapper.WasMapper")
public @Mapper interface WasMapper {
	
	//getList
	public List<CertVO> certList() throws DataAccessException;
	
	//getInfo from tb_certification
	public CertVO certSearch(String co_name) throws DataAccessException;
	
	//getInfo from tb_siteInfo
	public List<SiteVO> siteList(String co_name) throws DataAccessException;
}
