package com.web.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.web.domain.SiteVO;
import com.web.mapper.SiteMapper;

@Service("com.web.service.SiteService")
public class SiteService {
	
	@Resource(name="com.web.mapper.SiteMapper")
    SiteMapper siteMapper;
	
	public List<SiteVO> siteListService(String co_name) throws DataAccessException {
		
		return siteMapper.siteList(co_name);
	}
    
	public SiteVO siteSearchService(String co_name, String co_domain) throws DataAccessException {
    	
    	return siteMapper.siteSearch(co_name, co_domain);
    }

    public void siteInsertService(SiteVO site) throws DataAccessException {
 
    	siteMapper.siteInsert(site);
    }
    
    public void siteUpdateService(HashMap<String, String> param) throws DataAccessException {
    	
    	siteMapper.siteUpdate(param);
    }
    
    public void siteDeleteService(String co_name, String co_domain) throws DataAccessException {
    	siteMapper.siteDelete(co_name, co_domain);
    }
    
    public void siteListDeleteService(String co_name) throws DataAccessException {
    	siteMapper.siteListDelete(co_name);
    }
}
