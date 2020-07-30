package com.web.service;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.web.domain.UserVO;
import com.web.exception.WebException;
import com.web.mapper.UserMapper;

@Service("com.web.service.UserService")
public class UserService {
	@Resource(name="com.web.mapper.UserMapper")
    UserMapper userMapper;
	
	public UserVO getUserService(String co_username,String co_android_id) throws DataAccessException, WebException{
		UserVO ret =  userMapper.getUser(co_username,co_android_id);
		if(ret == null) {
			throw new WebException("No such user in tb_device [name: " + co_username + "] [id : " + co_android_id + "]",WebException.US_GETUSER_NO_SUCH_USER);
		}
		return ret;
		
	}

	public void userInsertService(UserVO user) throws DataAccessException, WebException {
		UserVO ret = userMapper.getUser(user.getCo_username(),user.getCo_android_id());
		if(ret != null) {
			throw new WebException("Duplicate pair [name: " + user.getCo_username() + "] [id : " + user.getCo_android_id() + "]",WebException.US_INSERT_DUPLICATE_ROW);
		}
		userMapper.UserInsert(user);
	}
	
	public void userDeleteService(String co_name,String co_android_id) throws DataAccessException, WebException {
		userMapper.UserDelete(co_name,co_android_id);
	}
	
}
