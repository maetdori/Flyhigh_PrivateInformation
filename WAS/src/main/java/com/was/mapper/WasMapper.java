package com.was.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.was.domain.CertVO;
import com.was.domain.SiteVO;

@Repository("com.was.mapper.WasMapper")
public @Mapper interface WasMapper {
	
	//getList
	public List<CertVO> certList() throws Exception;
	
	//getInfo from tb_certification
	public CertVO certSearch(String co_name) throws Exception;
	
	//getInfo from tb_siteInfo
	public List<SiteVO> siteList(String co_name) throws Exception;
}
