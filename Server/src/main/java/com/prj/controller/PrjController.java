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
		EncryptModule.encrypt("keyStore.p12","123456",
				new String[] {"a/b/c","d"},reqBody,responseBody);
		return responseBody;
	}
}
