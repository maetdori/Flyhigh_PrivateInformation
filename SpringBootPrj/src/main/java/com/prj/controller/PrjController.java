package com.prj.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PrjController {
	
	@GetMapping("/")
	public @ResponseBody Map<String, String> getInfo() {
		Map map = new HashMap<>();
		map.put("name", "HaeIn");
		map.put("age", "23");
		map.put("ID", "980605-2xxxxxx");
		return map;
	}
}
