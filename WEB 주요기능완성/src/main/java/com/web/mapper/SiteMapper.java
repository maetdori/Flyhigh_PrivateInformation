package com.web.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.domain.SiteVO;

@Repository("com.web.mapper.SiteMapper")
public @Mapper interface SiteMapper {

	//search by co_name(return siteList)
	public List<SiteVO> siteList(String co_name) throws DataAccessException;

	//search by co_name and co_domain(return "a" siteInfo)
	public SiteVO siteSearch(
			@Param("co_name") String co_name, 
			@Param("co_domain") String co_domain) throws DataAccessException;
	
	//register
	public void siteInsert(SiteVO site) throws DataAccessException;
	
	//modify
	public void siteUpdate(HashMap<String, String> param) throws DataAccessException;
	
	//delete "an" element matching with both co_name && co_domain
	public void siteDelete(
			@Param("co_name") String co_name, 
			@Param("co_domain") String co_domain) throws DataAccessException;
	
	//delete elements matching with co_name
	public void siteListDelete(String co_name) throws DataAccessException;
}
