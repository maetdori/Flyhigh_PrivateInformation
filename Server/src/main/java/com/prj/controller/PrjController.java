package com.prj.controller;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.prj.dao.DBMapper;

import com.prj.domain.Response;
import com.prj.key_exhange.AES128Util;
import com.prj.key_exhange.RSAModule;
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
	private KeyStore loadKeyStore(String keyStorePath,String passWord) {
		KeyStore keystore = null;
		try {
			keystore = KeyStore.getInstance("PKCS12");
		} catch (KeyStoreException e) {
			System.err.println("keyStoreInstance Error!");
			return null;
		}
		InputStream is = null;
		try {
			is = new FileInputStream(new File(keyStorePath));
		} catch (FileNotFoundException e) {
			System.err.println("KeyStore File not found!");
			return null;
		}
		try {
			keystore.load(is, passWord.toCharArray());
		} catch (Exception e) {
			System.err.println("Could not load keyStore!");
			return null;
		}
		return keystore;
	}
	private Certificate loadCertFromKeyStore(String keyStorePath,String passWord) {
		KeyStore keystore = loadKeyStore(keyStorePath,passWord);
		try {
			String alias = keystore.aliases().nextElement();
			Certificate certificate =  keystore.getCertificate(alias);
			return certificate;
		} catch (KeyStoreException e) {
			System.err.println("Could not loadCertificate From Keystore");
			return null;
		}

	}
	private byte[] getPrivateKeyFromKeyStore(String keyStorePath,String passWord) {
		KeyStore keystore = loadKeyStore(keyStorePath,passWord);
		try{
			String alias = keystore.aliases().nextElement();
			PrivateKey key = (PrivateKey)keystore.getKey(alias, "123456".toCharArray());
			return key.getEncoded();
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GetMapping(value = "/getCert")
	public @ResponseBody Map<String,String> getKey(
			@RequestHeader(value="Device-id", required = true) String userAgent)
			throws KeyStoreException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException,
			BadPaddingException, NoSuchProviderException, InvalidKeyException, CertificateEncodingException {
		X509Certificate certificate = (X509Certificate) loadCertFromKeyStore("keystore.p12","123456");
		Map<String,String> ret = new HashMap<>();
		ret.put("Cert-base64",new String(Base64.getEncoder().encode(certificate.getEncoded())));
		return ret;
	}

	@PostMapping(value = "/insert")
	public @ResponseBody Response responseHello(@RequestBody Map<String,String> param) throws GeneralSecurityException {
		dbMapper.insertRow(param);
		Map<String,String> idMsg = dbMapper.selectById(param.get("id"));
		Response ret = new Response();
		Map<String,String>[] pairs = new Map[2];
		pairs[0] = new HashMap<>();
		pairs[1] = new HashMap<>();
		pairs[0].put("name","id");
		pairs[1].put("name","message");
		ret.setEncryptedElements(pairs);
		SecureRandom rand = new SecureRandom();
		System.out.println("cKey base64:" + param.get("cKey"));
		byte[] cKey = Base64.getDecoder().decode(param.get("cKey"));
		System.out.println("Size of cKey:" + cKey.length);
		byte[] privKey = getPrivateKeyFromKeyStore("keystore.p12","123456");
		cKey = RSAModule.decryptRSA(privKey,cKey);
		System.out.println("cKeyBase64 : " + Base64.getEncoder().encodeToString(cKey));
		byte[] sKey = new byte[16];
		rand.nextBytes(sKey);
		System.out.println("sKeyBase64 : " + Base64.getEncoder().encodeToString(sKey));
		byte[] key = new byte[32];
		System.arraycopy(cKey,0,key,0,16);
		System.arraycopy(sKey,0,key,16,16);
		AES128Util aes = new AES128Util(key);
		System.out.println("keyBase64 : " + Base64.getEncoder().encodeToString(key));
		ret.setId(Base64.getEncoder().encodeToString(aes.encrypt(idMsg.get("id"))));
		ret.setMessage(Base64.getEncoder().encodeToString(aes.encrypt(idMsg.get("message"))));
		ret.setsKey(Base64.getEncoder().encodeToString(RSAModule.encryptRSA(privKey,sKey)));
		return ret;
	}
}
