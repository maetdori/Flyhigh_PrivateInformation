package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.SearchVO;

@RestController
@RequestMapping("/api") //localhost:8080/api
public class GetAPIController {
	
	@GetMapping("/GET") //localhost:8080/api/GET?name=userName&sex=userSex&id=userId
	public SearchVO getHttp(SearchVO searchVo) {
		return searchVo;
	}
}
