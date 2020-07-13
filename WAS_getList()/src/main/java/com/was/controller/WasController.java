package com.was.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.was.domain.WasVO;
import com.was.service.WasService;

@Controller
@RequestMapping("/private")
public class WasController {
	
	@Resource(name="com.was.service.WasService")
	WasService wasService;
	List<WasVO> list;
	
	@PostMapping(value = "/getList")
	@ResponseBody
    public Map<String, Object> getList(@RequestBody String co_name) throws Exception{
		list = wasService.certSearchService(co_name);
		ArrayList<Map<String, Object>> accList = new ArrayList<>();
		
		for(WasVO w : list) {
			Map<String, String> validity = new HashMap<>();
			validity.put("NotBefore", w.getCo_active_date());
			validity.put("NotAfter", w.getCo_exp_date());
			Map<String, Object> account = new HashMap<>();
			account.put("Subject", w.getCo_name());
			account.put("Validity", validity);
			accList.add(account);
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("count", list.size());
		response.put("account", accList);
		
		return response;
    }
}
