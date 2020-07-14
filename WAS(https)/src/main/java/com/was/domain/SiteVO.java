package com.was.domain;

//tb_siteInfo에서 가져온 정보 관리
public class SiteVO {
	private String co_domain;
	private String co_id;
	private String co_pw;
	public String getSite() {
		return co_domain;
	}
	public void setSite(String site) {
		this.co_domain = site;
	}
	public String getId() {
		return co_id;
	}
	public void setId(String id) {
		this.co_id = id;
	}
	public String getPw() {
		return co_pw;
	}
	public void setPw(String pw) {
		this.co_pw = pw;
	}
}
