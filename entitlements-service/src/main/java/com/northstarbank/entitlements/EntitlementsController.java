package com.northstarbank.entitlements;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/internal")
class EntitlementsController {
 private final EntitlementService service;
 EntitlementsController(EntitlementService service){this.service=service;}
 @PostMapping("/login") Object login(@Valid @RequestBody LoginRequest r){return service.login(r.email(),r.password());}
 @PostMapping("/decide") Object decide(@RequestHeader("Authorization") String auth,@Valid @RequestBody DecisionRequest r){return service.decide(auth.replace("Bearer ",""),r.action(),r.branch(),r.vulnerable(),r.requester());}
 @PostMapping("/jobs/delegations") void advance(@RequestParam(required=false) LocalDate asOfDate){service.advance(asOfDate==null?LocalDate.now():asOfDate);}
 @GetMapping("/staff") Object staff(@RequestHeader("Authorization")String auth){return service.staff(auth.replace("Bearer ",""));}
 @PostMapping("/staff") Object staffCreate(@RequestHeader("Authorization")String auth,@Valid @RequestBody StaffRequest r){return service.createStaff(auth.replace("Bearer ",""),new EntitlementService.NewStaff(r.employeeId(),r.email(),r.role(),r.homeBranch(),r.region(),r.temporaryPassword()));}
 @PostMapping("/password-reset") void resetPassword(@Valid @RequestBody PasswordChange r){service.resetPassword(r.employeeId(),r.password());}
 @PatchMapping("/staff/{id}/enabled") void enabled(@RequestHeader("Authorization")String auth,@PathVariable String id,@RequestParam boolean value){service.setEnabled(auth.replace("Bearer ",""),id,value);}
 @GetMapping("/delegations") Object delegations(@RequestHeader("Authorization")String auth){return service.delegationList(auth.replace("Bearer ",""));}
 @PostMapping("/delegations") Object delegationCreate(@RequestHeader("Authorization")String auth,@Valid @RequestBody DelegationRequest r){return service.createDelegation(auth.replace("Bearer ",""),new EntitlementService.NewDelegation(r.delegatorId(),r.delegateId(),r.grantedScope(),r.grantedLimit(),r.startDate(),r.endDate()));}
 @PostMapping("/delegations/{id}/revoke") void delegationRevoke(@RequestHeader("Authorization")String auth,@PathVariable long id){service.revokeDelegation(auth.replace("Bearer ",""),id);}
 record LoginRequest(@Email String email,@NotBlank String password){}
 record DecisionRequest(@NotBlank String action,@Pattern(regexp="B0[1-9]") String branch,boolean vulnerable,@Pattern(regexp="EMP\\d{3,6}")String requester){}
 record StaffRequest(@Pattern(regexp="EMP\\d{3,6}")String employeeId,@Email String email,@Pattern(regexp="CSR|BRANCH_OFFICER|BRANCH_SUPERVISOR|REGIONAL_MANAGER|HEAD_OFFICE_OPS|SECURITY_ADMIN")String role,@Pattern(regexp="B0[1-9]")String homeBranch,@Pattern(regexp="R[1-3]")String region,@Size(min=12,max=80)String temporaryPassword){}
 record PasswordChange(@Pattern(regexp="EMP\\d{3,6}")String employeeId,@Size(min=12,max=80)String password){}
 record DelegationRequest(@Pattern(regexp="EMP\\d{3,6}")String delegatorId,@Pattern(regexp="EMP\\d{3,6}")String delegateId,@Pattern(regexp="BANK|R[1-3]|B0[1-9]")String grantedScope,@DecimalMin("0")java.math.BigDecimal grantedLimit,@NotNull LocalDate startDate,@NotNull LocalDate endDate){}
 @ExceptionHandler({EntitlementService.AuthenticationFailed.class,TokenService.InvalidTokenException.class}) ResponseEntity<Void> unauthorized(){return ResponseEntity.status(401).build();}
 @ExceptionHandler(EntitlementService.Forbidden.class) ResponseEntity<Void> forbidden(){return ResponseEntity.status(403).build();}
}
