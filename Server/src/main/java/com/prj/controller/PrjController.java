package com.prj.controller;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.prj.dao.DBMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

@RestController
@RequestMapping("/api")
public class PrjController {
	@Autowired
	private DBMapper dbMapper;

	@GetMapping(value = "/getCert")
	public @ResponseBody Map<String,String> getKey(
			@RequestHeader(value="Device-id", required = true) String userAgent)
			throws KeyStoreException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException,
			BadPaddingException, NoSuchProviderException, InvalidKeyException, CertificateEncodingException {
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		InputStream is = null;
		try {
			is = new FileInputStream(new File("keystore.p12"));
		} catch (FileNotFoundException e) {
			System.err.println("KeyStore File not found!");
			return null;
		}
		try {
			keystore.load(is, "123456".toCharArray());
		} catch (Exception e) {
			System.err.println("Could not load keyStore!");
			return null;
		}
		String alias = keystore.aliases().nextElement();
		X509Certificate certificate = (X509Certificate) keystore.
				getCertificate(alias);

		Map<String,String> ret = new HashMap<>();
		ret.put("Cert-base64",new String(Base64.getEncoder().encode(certificate.getEncoded())));
		return ret;
	}

	@PostMapping(value = "/insert")
	public @ResponseBody Map<String,String> responseHello(@RequestBody Map<String,String> param) {
		dbMapper.insertRow(param);
		Map<String,String> ret = dbMapper.selectById(param.get("id"));
		return ret;
	}
}
