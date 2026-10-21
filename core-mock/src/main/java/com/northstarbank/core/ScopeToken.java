package com.northstarbank.core;
import java.nio.charset.StandardCharsets;import java.security.MessageDigest;import java.time.Instant;import java.util.*;import javax.crypto.Mac;import javax.crypto.spec.SecretKeySpec;import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Component;
@Component class ScopeToken{
 private final byte[] key;ScopeToken(@Value("${app.token-secret}")String s){key=s.getBytes(StandardCharsets.UTF_8);}
 Claims verify(String auth){try{String t=auth.replace("Bearer ","");String[] p=t.split("\\.");String body=new String(Base64.getUrlDecoder().decode(p[0]),StandardCharsets.UTF_8);Mac m=Mac.getInstance("HmacSHA256");m.init(new SecretKeySpec(key,"HmacSHA256"));if(!MessageDigest.isEqual(m.doFinal(body.getBytes(StandardCharsets.UTF_8)),Base64.getUrlDecoder().decode(p[1])))throw new Exception();String[] f=body.split("\\|");if(Instant.now().getEpochSecond()>Long.parseLong(f[4]))throw new Exception();return new Claims(f[0],f[1],Set.of(f[2].split(",")));}catch(Exception e){throw new InvalidScopeToken();}}
 void require(Claims c,String branch){if(c.role.equals("SECURITY_ADMIN")||c.scopes.stream().noneMatch(s->s.equals("BANK")||s.equals(branch)||s.equals(region(branch))))throw new OutOfScope();}
 private String region(String b){int n=Integer.parseInt(b.substring(1));return n<4?"R1":n<7?"R2":"R3";}
 record Claims(String employee,String role,Set<String> scopes){} static class InvalidScopeToken extends RuntimeException{} static class OutOfScope extends RuntimeException{}
}
