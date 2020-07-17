package com.web.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

//der형식 인증서를 parsing
//register RequestBody에서 얻은 der 인증서에서 필요한 정보(Validity)를 뽑아와 DB에 저장하기 위함  
public class ParseDer{
	private String notBefore;
	private String notAfter;
	
	public ParseDer(byte[] certBytes) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
		
		InputStream is = new ByteArrayInputStream(certBytes);
		CertificateFactory factory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) factory.generateCertificate(is); //generate X509Certificate from byte[]
		
		this.notBefore = certificate.getNotBefore().toString();
		this.notAfter = certificate.getNotAfter().toString();	
		
	}

	public String getNotBefore() {
		return notBefore;
	}

	public void setNotBefore(String notBefore) {
		this.notBefore = notBefore;
	}

	public String getNotAfter() {
		return notAfter;
	}

	public void setNotAfter(String notAfter) {
		this.notAfter = notAfter;
	}
}
