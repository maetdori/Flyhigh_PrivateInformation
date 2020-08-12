package com.web.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.web.domain.PersonalInfoVO;

@Repository("com.web.mapper.PersonalInfoMapper")
public @Mapper interface PersonalInfoMapper {
	//list
	public ArrayList<PersonalInfoVO> piList() throws DataAccessException;

	//search
	public PersonalInfoVO piSearch(String co_name) throws DataAccessException;
	
	//register
	public void piInsert(PersonalInfoVO pi) throws DataAccessException;
	
	//modify
	public void piUpdate(PersonalInfoVO pi) throws DataAccessException;
	
	//delete
	public void piDelete(String co_name) throws DataAccessException;

}
