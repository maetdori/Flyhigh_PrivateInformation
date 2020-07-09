package com.prj.controller;

import java.util.HashMap;
import java.util.Map;

import com.prj.dao.DBMapper;
import com.prj.dto.Param;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PrjController {
	@Autowired
	private DBMapper dbMapper;



	@PostMapping(value = "/insert")
	public @ResponseBody Param responseHello(@RequestBody Param param) {
		dbMapper.insertRow(param);
		Param ret = dbMapper.selectById(param.getId());
		return ret;
	}
}
