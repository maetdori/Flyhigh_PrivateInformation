package com.was.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.was.domain.CertVO;
import com.was.domain.SiteVO;
import com.was.mapper.WasMapper;

@Service("com.was.service.WasService")
public class WasService {
	
	@Resource(name="com.was.mapper.WasMapper")
	WasMapper wasMapper;
	
	//getList
	public List<CertVO> certListService() throws Exception {
		return wasMapper.certList();
	}
	
	//getInfo from tb_certification
	public CertVO certSearchService(String co_name) throws Exception {
		return wasMapper.certSearch(co_name);
	}
	
	//getInfo from tb_siteInfo
	public List<SiteVO> siteListService(String co_name) throws Exception {
		return wasMapper.siteList(co_name);
	}
	
}
