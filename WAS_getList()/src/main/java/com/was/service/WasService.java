package com.was.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.was.domain.WasVO;
import com.was.mapper.WasMapper;

@Service("com.was.service.WasService")
public class WasService {
	
	@Resource(name="com.was.mapper.WasMapper")
	WasMapper wasMapper;
	
	/*public int certCountService(String co_name) throws Exception {
		return wasMapper.certCount(co_name);
	}*/
	
	public List<WasVO> certSearchService(String co_name) throws Exception {
		return wasMapper.certSearch(co_name);
	}
	
}
