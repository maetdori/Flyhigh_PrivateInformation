package com.was.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.was.domain.WasVO;

@Repository("com.was.mapper.WasMapper")
public @Mapper interface WasMapper {
	
	//인증서 개수
	//public Integer certCount(String co_name) throws Exception;
	//인증서 목록 
	public List<WasVO> certSearch(String co_name) throws Exception;
	
}
