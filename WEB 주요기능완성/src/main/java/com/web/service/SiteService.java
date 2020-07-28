package com.web.service;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.web.domain.KeyVO;
import com.web.domain.SiteVO;
import com.web.exception.WebException;
import com.web.mapper.SiteMapper;
import com.web.security.DBEncryptModule;

@Service("com.web.service.SiteService")
public class SiteService {
	
	@Resource(name="com.web.mapper.SiteMapper")
    SiteMapper siteMapper;
	
	@Resource(name="com.web.service.KeyService")
	KeyService keyService;
	
	public List<SiteVO> siteListService(String co_name) throws DataAccessException, WebException {
		KeyVO key = keyService.getKeyService(co_name);
		List<SiteVO> ret = siteMapper.siteList(co_name);
		for(SiteVO site : ret) {
			DBEncryptModule.decryptSite(site, key.getCo_key());
		}
		return ret;
	}
    
	public SiteVO siteSearchService(String co_name, String co_domain) throws DataAccessException, WebException {
    	KeyVO key = keyService.getKeyService(co_name);
    	SiteVO ret = siteMapper.siteSearch(co_name, co_domain);
    	DBEncryptModule.decryptSite(ret, key.getCo_key());
    	return ret;
    }

    public void siteInsertService(SiteVO site) throws DataAccessException, WebException {
    	KeyVO key = keyService.getKeyService(site.getCo_name());
    	DBEncryptModule.encryptSite(site, key.getCo_key());
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
