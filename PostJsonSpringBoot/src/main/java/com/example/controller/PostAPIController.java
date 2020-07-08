package com.example.controller;

import com.example.model.SearchVO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostAPIController {
	
	//http 통신할 떄 post는 body에다 data를 집어넣어서 받아오겠다.
	//@RequestBody에 SearchVO에 있는 값을 매칭해서 보내줘라
	@RequestMapping(method = RequestMethod.POST, path = "/postRequest")
	public SearchVO postRequest(@RequestBody SearchVO searchVo) {
		return searchVo;
	}
	
	@PostMapping(value = "/postMapping")
	public SearchVO postMapping(@RequestBody SearchVO searchVo) {
		return searchVo;
	}
}
