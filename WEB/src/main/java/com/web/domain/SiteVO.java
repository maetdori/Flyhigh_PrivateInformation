package com.web.domain;

//tb_siteInfo에서 가져온 정보 관리
public class SiteVO {
	private String co_name;
	private String co_domain;
	private byte[] co_id;
	private byte[] co_pw;
	
	public String getCo_name() {
		return co_name;
	}
	public void setCo_name(String co_name) {
		this.co_name = co_name;
	}
	public String getCo_domain() {
		return co_domain;
	}
	public void setCo_domain(String co_domain) {
		this.co_domain = co_domain;
	}
	public byte[] getCo_id() {
		return co_id;
	}
	public void setCo_id(byte[] co_id) {
		this.co_id = co_id;
	}
	public byte[] getCo_pw() {
		return co_pw;
	}
	public void setCo_pw(byte[] co_pw) {
		this.co_pw = co_pw;
	}
	
	
}

