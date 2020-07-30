package com.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.web.domain.UserVO;

@Repository("com.web.mapper.UserMapper")
public @Mapper interface UserMapper {

		public UserVO getUser(@Param("co_username") String co_username,@Param("co_android_id") String android_id) throws DataAccessException;

		public void UserInsert(UserVO user) throws DataAccessException;
		
		public void UserDelete(@Param("co_username") String co_username,@Param("co_android_id") String android_id) throws DataAccessException;
}
