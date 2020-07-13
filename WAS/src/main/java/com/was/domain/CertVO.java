package com.was.domain;

//tb_certification에서 가져온 정보 관리
public class CertVO {
	private String co_name;
	private String co_active_date;
	private String co_exp_date;
	private String co_cert_pw;
	private String co_cert_der;
	
	public String getCo_name() {
		return co_name;
	}
	public void setCo_name(String co_name) {
		this.co_name = co_name;
	}
	public String getCo_active_date() {
		return co_active_date;
	}
	public void setCo_active_date(String co_active_date) {
		this.co_active_date = co_active_date;
	}
	public String getCo_exp_date() {
		return co_exp_date;
	}
	public void setCo_exp_date(String co_exp_date) {
		this.co_exp_date = co_exp_date;
	}
	public String getCo_cert_pw() {
		return co_cert_pw;
	}
	public void setCo_cert_pw(String co_cert_pw) {
		this.co_cert_pw = co_cert_pw;
	}
	public String getCo_cert_der() {
		return co_cert_der;
	}
	public void setCo_cert_der(String co_cert_der) {
		this.co_cert_der = co_cert_der;
	}
	
	
}
