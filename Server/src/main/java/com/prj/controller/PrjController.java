package com.prj.controller;

import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.prj.dao.DBMapper;

import com.prj.key_exhange.EncryptModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PrjController {
	@Autowired
	private DBMapper dbMapper;

	@PostMapping(value = "/getList")
	public @ResponseBody Map<String,Object> responseHello(@RequestBody Map<String,Object> param) {
		return null;
	}
	@PostMapping(value = "/test")
	public @ResponseBody Map<String,Object> test(@RequestBody Map<String,Object> reqBody) {
		Map<String,Object> responseBody = reqBody;
		//비대칭키 쌍 생성방법: keytool -genkeypair -keyalg rsa -keystore keystoreName -storetype pkcs12
		//키저장소파일, 비밀번호,암호화 할 요소, requsetBody,responseBody를 넣고 암호화하면 responseBody의 정보가 암호화 된다.
		//responseBody를 return하기전에 한번 호출해주면 됩니다.
		//ex)
		EncryptModule.encrypt("keyStore.p12","123456",
				new String[] {"a/b/c","d"},reqBody,responseBody);
		return responseBody;
	}

}
