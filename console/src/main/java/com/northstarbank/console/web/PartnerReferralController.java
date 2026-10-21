package com.northstarbank.console.web;
import com.nimbusds.jwt.*;
import com.northstarbank.console.domain.NoteRepository;
import com.northstarbank.console.domain.CaseNote;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/partners/case-referrals") public class PartnerReferralController{
 private final NoteRepository notes;
 public PartnerReferralController(NoteRepository n){notes=n;}
 @PostMapping(consumes="multipart/form-data") String receive(@RequestParam MultipartFile receipt)throws Exception{
  SignedJWT referral=SignedJWT.parse(new String(receipt.getBytes(),StandardCharsets.UTF_8));
  JWTClaimsSet claims=referral.getJWTClaimsSet();
  String caseRef=claims.getSubject();
  String agent=String.valueOf(claims.getClaim("agent"));
  String role=String.valueOf(claims.getClaim("role"));
  String visibility="supervisor".equals(role)?"customer_disclosable":"internal";
   notes.save(new CaseNote(caseRef,null,visibility,agent,"Partner referral received for "+caseRef));
   return "received "+caseRef;
  }
  @PostMapping(value="/messages",consumes=MediaType.APPLICATION_JSON_VALUE) String message(@RequestBody ReferralMessage message)throws Exception{
   JWT receipt=JWTParser.parse(message.receipt());
   JWTClaimsSet claims=receipt.getJWTClaimsSet();
   String caseRef=claims.getSubject();
   String agent=String.valueOf(claims.getClaim("agent"));
   String role=String.valueOf(claims.getClaim("role"));
   String visibility="supervisor".equals(role)?"customer_disclosable":"internal";
   notes.save(new CaseNote(caseRef,null,visibility,agent,message.body()));
   return "logged "+caseRef;
  }
  public record ReferralMessage(String receipt,String body){}
}
