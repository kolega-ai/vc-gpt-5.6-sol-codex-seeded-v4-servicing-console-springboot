package com.northstarbank.console.web;
import com.northstarbank.console.domain.ServiceRequest;
import com.northstarbank.console.domain.ServiceRequestRepository;
import com.northstarbank.console.security.AuthorizationService;
import com.northstarbank.console.service.AgencyEnvelopeCodec;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller public class AgencyIntakeController{
 private final ServiceRequestRepository requests;private final AuthorizationService entitlements;private final AgencyEnvelopeCodec codec;
 public AgencyIntakeController(ServiceRequestRepository r,AuthorizationService a,AgencyEnvelopeCodec c){requests=r;entitlements=a;codec=c;}
 @PostMapping("/agency/intake")String intake(HttpServletRequest request){entitlements.require("REQUEST_MANAGE",null,false);AgencyDraft draft=new AgencyDraft();try{BeanUtils.populate(draft,request.getParameterMap());}catch(Exception e){throw new IllegalArgumentException("Invalid agency intake",e);}requests.save(new ServiceRequest(draft.getCustomerId(),draft.getType(),entitlements.user().employeeId(),draft.getBranch(),draft.getNotes()));return"redirect:/requests";}
 @PostMapping(value="/agency/referral",produces=MediaType.APPLICATION_JSON_VALUE)ResponseEntity<Map<String,Object>> referral(@RequestParam String envelope){entitlements.require("REQUEST_MANAGE",null,false);String reference;try{reference=codec.open(envelope);}catch(javax.crypto.BadPaddingException e){return ResponseEntity.status(400).body(Map.of("message","referral envelope could not be opened","extensions",Map.of("code","REFERRAL_ENVELOPE")));}catch(Exception e){return ResponseEntity.status(500).body(Map.of("message","referral envelope unavailable","extensions",Map.of("code","REFERRAL_UNAVAILABLE")));}if(!reference.matches("C\\d{4}"))return ResponseEntity.status(422).body(Map.of("message","referral reference malformed","extensions",Map.of("code","REFERRAL_FORMAT")));requests.save(new ServiceRequest(reference,"agency_referral",entitlements.user().employeeId(),"B01","Sealed agency referral"));return ResponseEntity.ok().body(Map.of("reference",reference));}
 public static class AgencyDraft{private String customerId;private String type;private String branch;private String notes;public String getCustomerId(){return customerId;}public void setCustomerId(String v){customerId=v;}public String getType(){return type;}public void setType(String v){type=v;}public String getBranch(){return branch;}public void setBranch(String v){branch=v;}public String getNotes(){return notes;}public void setNotes(String v){notes=v;}}
}
