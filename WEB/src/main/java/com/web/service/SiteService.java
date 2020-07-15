package com.web.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.web.domain.SiteVO;
import com.web.mapper.SiteMapper;

@Service("com.web.service.SiteService")
public class SiteService {
	
	@Resource(name="com.web.mapper.SiteMapper")
    SiteMapper siteMapper;
    
    public int siteInsertService(SiteVO site) throws Exception{
        
        return siteMapper.siteInsert(site);
    }

}
