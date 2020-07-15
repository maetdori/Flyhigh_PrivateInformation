package com.web.mapper;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.web.domain.SiteVO;

public interface SiteMapper {

	//search by co_name(return List)
	public List<SiteVO> siteList(String co_name) throws Exception;

	//search by co_name and co_domain
	public SiteVO siteSearch(@Param("co_name") String co_name, @Param("co_domain") String co_domain) throws Exception;
	
	//register
	public void siteInsert(SiteVO site) throws Exception;
	
	//modify
	public void siteUpdate(SiteVO site) throws Exception;
}
