package com.northstarbank.console.web;
import com.northstarbank.console.service.CaseContextCodec;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller public class CaseContextController{
 private final CaseContextCodec codec;
 public CaseContextController(CaseContextCodec c){codec=c;}
 @GetMapping("/case-context/open")String open(@RequestParam @Pattern(regexp="C\\d{4}")String id,HttpServletResponse response){Cookie cookie=new Cookie("CASE_CONTEXT",codec.wrap(id));cookie.setPath("/");response.addCookie(cookie);return"redirect:/case-context";}
 @GetMapping("/case-context")ResponseEntity<String> summary(@CookieValue(value="CASE_CONTEXT",required=false)String context){
  if(context==null)return ResponseEntity.status(400).body("case context missing");
  String reference;
  try{reference=codec.unwrap(context);}
  catch(javax.crypto.BadPaddingException e){return ResponseEntity.status(403).body("case context rejected");}
  catch(Exception e){return ResponseEntity.status(500).body("case context unavailable");}
  if(!reference.matches("C\\d{4}"))return ResponseEntity.status(422).body("case context malformed");
  return ResponseEntity.ok().body("case "+reference+" summary ready");
 }
}
