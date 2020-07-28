package com.web.domain;

public class KeyVO {
	private String co_name; //사용자이름
	private String co_key;//base64 encoding된 256bit key
	public String getCo_name() {
		return co_name;
	}
	public void setCo_name(String co_name) {
		this.co_name = co_name;
	}
	public String getCo_key() {
		return co_key;
	}
	public void setCo_key(String co_key) {
		this.co_key = co_key;
	}
	
}
