package com.prj.controller;

import java.util.HashMap;
import java.util.Map;
import com.prj.domain.Param;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class PrjController {
	@PostMapping(value = "/post")
	public @ResponseBody Map<String, String> responseHello(@RequestBody Param param) {
		String data = param.getMessage();
		Map map = new HashMap<>();
		map.put("Res", data);
		return map;
	}
}
