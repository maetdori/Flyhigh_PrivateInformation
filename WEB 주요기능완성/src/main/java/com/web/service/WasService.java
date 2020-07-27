package com.web.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.web.domain.CertVO;
import com.web.domain.SiteVO;
import com.web.mapper.WasMapper;



@Service("com.web.service.WasService")
public class WasService {
	
	@Resource(name="com.web.mapper.WasMapper")
	WasMapper wasMapper;
	
	//getList
	public List<CertVO> certListService() throws DataAccessException {
		return wasMapper.certList();
	}
	
	//getInfo from tb_certification
	public CertVO certSearchService(String co_name) throws DataAccessException{
		return wasMapper.certSearch(co_name);
	}
	
	//getInfo from tb_siteInfo
	public List<SiteVO> siteListService(String co_name) throws DataAccessException {
		return wasMapper.siteList(co_name);
	}
	
}
