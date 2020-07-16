package com.web.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.mapper.SiteMapper;

@Service("com.web.service.SiteService")
public class SiteService {
	
	@Resource(name="com.web.mapper.SiteMapper")
    SiteMapper siteMapper;
	
	public List<SiteVO> siteList(String co_name) throws Exception {
		
		return siteMapper.siteList(co_name);
	}
    
	public SiteVO siteSearchService(String co_name, String co_domain) throws Exception {
    	
    	return siteMapper.siteSearch(co_name, co_domain);
    }

    public void siteInsertService(SiteVO site) throws Exception {
 
    	siteMapper.siteInsert(site);
    }
    
    public void siteUpdateService(HashMap<String, String> param) throws Exception {
    	
    	siteMapper.siteUpdate(param);
    }
    
    public void siteDeleteService(String co_name, String co_domain) throws Exception {
    	siteMapper.siteDelete(co_name, co_domain);
    }
    
    public void siteListDeleteService(String co_name) throws Exception {
    	siteMapper.siteListDelete(co_name);
    }
}
