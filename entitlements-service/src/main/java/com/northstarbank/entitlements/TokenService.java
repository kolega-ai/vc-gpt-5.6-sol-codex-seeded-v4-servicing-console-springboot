package com.northstarbank.entitlements;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class TokenService {
  private final byte[] key;
  TokenService(@Value("${app.token-secret}") String secret) { this.key=secret.getBytes(StandardCharsets.UTF_8); }
  String mint(StaffUser u,String scope,String approvalLimit) {
    String body=String.join("|",u.employeeId,u.role.name(),scope,approvalLimit,String.valueOf(Instant.now().plusSeconds(300).getEpochSecond()));
    return b64(body)+"."+b64(sign(body));
  }
  String verifyAndGetUser(String token) {
    try {
      String[] p=token.split("\\."); String body=new String(Base64.getUrlDecoder().decode(p[0]),StandardCharsets.UTF_8);
      if (!java.security.MessageDigest.isEqual(sign(body),Base64.getUrlDecoder().decode(p[1]))) throw new IllegalArgumentException();
      String[] f=body.split("\\|"); if (Instant.now().getEpochSecond()>Long.parseLong(f[4])) throw new IllegalArgumentException();
      return f[0];
    } catch(Exception e) { throw new InvalidTokenException(); }
  }
  private byte[] sign(String body) { try { Mac mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(key,"HmacSHA256"));return mac.doFinal(body.getBytes(StandardCharsets.UTF_8)); } catch(Exception e) { throw new IllegalStateException("HMAC unavailable",e); } }
  private String b64(String s){return b64(s.getBytes(StandardCharsets.UTF_8));}
  private String b64(byte[] v){return Base64.getUrlEncoder().withoutPadding().encodeToString(v);}
  static class InvalidTokenException extends RuntimeException {}
}
