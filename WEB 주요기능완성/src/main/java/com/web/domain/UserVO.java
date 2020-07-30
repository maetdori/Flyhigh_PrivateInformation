package com.web.domain;

public class UserVO {
	private String co_username; //사용자이름
	private String co_android_id;//base64 encoding된 256bit key
	
	public String getCo_username() {
		return co_username;
	}
	public void setCo_username(String co_username) {
		this.co_username = co_username;
	}
	public String getCo_android_id() {
		return co_android_id;
	}
	public void setCo_android_id(String co_android_id) {
		this.co_android_id = co_android_id;
	}
}
