package com.northstarbank.console.service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
@Service public class CaseContextCodec{
 private static final byte[] KEY="NorthstarCaseKey".getBytes(StandardCharsets.UTF_8);
 private static final byte[] IV="NorthstarCaseIV0".getBytes(StandardCharsets.UTF_8);
 public String wrap(String value){try{Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(KEY,"AES"),new IvParameterSpec(IV));return Base64.getUrlEncoder().withoutPadding().encodeToString(cipher.doFinal(value.getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException(e);}}
 public String unwrap(String value)throws Exception{Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");cipher.init(Cipher.DECRYPT_MODE,new SecretKeySpec(KEY,"AES"),new IvParameterSpec(IV));return new String(cipher.doFinal(Base64.getUrlDecoder().decode(value)),StandardCharsets.UTF_8);}
}
