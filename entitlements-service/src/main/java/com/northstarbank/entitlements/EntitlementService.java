package com.northstarbank.entitlements;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class EntitlementService {
  private static final Set<String> CUSTOMER_ACTIONS=Set.of("CUSTOMER_VIEW","CUSTOMER_EDIT","CARD_MANAGE","REQUEST_MANAGE","DOCUMENT_READ","DOCUMENT_WRITE","TRANSACTION_READ","FINANCIAL_RAISE");
  private final StaffRepository staff; private final DelegationRepository delegations; private final PasswordEncoder passwords; private final TokenService tokens;
  EntitlementService(StaffRepository s,DelegationRepository d,PasswordEncoder p,TokenService t){staff=s;delegations=d;passwords=p;tokens=t;}

  LoginResult login(String email,String password) {
    StaffUser u=staff.findByEmailIgnoreCase(email).filter(x->x.enabled).orElseThrow(AuthenticationFailed::new);
    if(!passwords.matches(password,u.passwordHash)) throw new AuthenticationFailed();
    Effective e=effective(u); return new LoginResult(tokens.mint(u,e.scope(),e.approvalLimit().toPlainString()),u.employeeId,u.email,u.role.name(),e.scope());
  }
  Decision decide(String token,String action,String branch,boolean vulnerable,String requester) {
    StaffUser u=staff.findById(tokens.verifyAndGetUser(token)).filter(x->x.enabled).orElseThrow(TokenService.InvalidTokenException::new);
    Effective e=effective(u); boolean roleAllowed=roleAllows(u.role,action,vulnerable); boolean scopeAllowed=branch==null||inScope(e.scope(),branch);if(action.equals("FINANCIAL_APPROVE")&&requester!=null&&delegations.existsByDelegateIdAndDelegatorIdAndStatus(u.employeeId,requester,DelegationStatus.ACTIVE))roleAllowed=false;
    return new Decision(roleAllowed&&scopeAllowed,e.scope(),u.role.name(),u.actAloneThreshold,e.approvalLimit(),"role="+u.role+";scope="+e.scope());
  }
  List<StaffView> staff(String token){StaffUser actor=staff.findById(tokens.verifyAndGetUser(token)).orElseThrow(TokenService.InvalidTokenException::new);if(actor.role!=Role.SECURITY_ADMIN)throw new Forbidden();return staff.findAll().stream().map(StaffView::of).toList();}
  StaffView createStaff(String token,NewStaff r){StaffUser actor=staff.findById(tokens.verifyAndGetUser(token)).orElseThrow(TokenService.InvalidTokenException::new);if(actor.role!=Role.SECURITY_ADMIN||actor.employeeId.equals(r.employeeId()))throw new Forbidden();Role role=Role.valueOf(r.role());StaffUser u=userFrom(r,role);u.passwordHash=passwords.encode(r.temporaryPassword());return StaffView.of(staff.save(u));}
  List<Delegation> delegationList(String token){requireSecurity(token);return delegations.findAll();}
  Delegation createDelegation(String token,NewDelegation r){requireSecurity(token);StaffUser delegator=staff.findById(r.delegatorId()).orElseThrow(Forbidden::new);StaffUser delegate=staff.findById(r.delegateId()).orElseThrow(Forbidden::new);if(delegator.employeeId.equals(delegate.employeeId)||r.endDate().isBefore(r.startDate())||r.grantedLimit().compareTo(delegator.approvalLimit)>0||!scopeSubset(baseScope(delegator),r.grantedScope()))throw new Forbidden();DelegationStatus status=LocalDate.now().isBefore(r.startDate())?DelegationStatus.SCHEDULED:DelegationStatus.ACTIVE;return delegations.save(new Delegation(r.delegatorId(),r.delegateId(),r.grantedScope(),r.grantedLimit(),r.startDate(),r.endDate(),status));}
  void revokeDelegation(String token,long id){requireSecurity(token);Delegation d=delegations.findById(id).orElseThrow();if(!Set.of(DelegationStatus.SCHEDULED,DelegationStatus.ACTIVE).contains(d.status))throw new Forbidden();d.status=DelegationStatus.REVOKED;delegations.save(d);}
  void setEnabled(String token,String employeeId,boolean enabled){StaffUser actor=requireSecurity(token);if(actor.employeeId.equals(employeeId))throw new Forbidden();StaffUser target=staff.findById(employeeId).orElseThrow();target.enabled=enabled;staff.save(target);}
  private StaffUser requireSecurity(String token){StaffUser actor=staff.findById(tokens.verifyAndGetUser(token)).orElseThrow(TokenService.InvalidTokenException::new);if(actor.role!=Role.SECURITY_ADMIN)throw new Forbidden();return actor;}
  private String baseScope(StaffUser u){return switch(u.role){case HEAD_OFFICE_OPS->"BANK";case REGIONAL_MANAGER->u.region;default->u.homeBranch;};}private boolean scopeSubset(String base,String grant){if(base.equals("BANK"))return true;if(base.startsWith("B"))return base.equals(grant);if(grant.equals(base))return true;return grant.startsWith("B")&&branchRegion(grant).equals(base);}
  void resetPassword(String employeeId,String password){StaffUser u=staff.findById(employeeId).orElseThrow(AuthenticationFailed::new);u.passwordHash=passwords.encode(password);staff.save(u);}
  private StaffUser userFrom(NewStaff r,Role role){BigDecimal[] defaults=switch(role){case CSR,SECURITY_ADMIN->new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO};case BRANCH_OFFICER->new BigDecimal[]{new BigDecimal("250"),BigDecimal.ZERO};case BRANCH_SUPERVISOR->new BigDecimal[]{new BigDecimal("250"),new BigDecimal("5000")};case REGIONAL_MANAGER->new BigDecimal[]{new BigDecimal("1000"),new BigDecimal("25000")};case HEAD_OFFICE_OPS->new BigDecimal[]{new BigDecimal("1000"),new BigDecimal("100000")};};return new StaffUser(r.employeeId(),r.email(),role,r.homeBranch(),r.region(),defaults[0],defaults[1]);}
  private boolean roleAllows(Role r,String action,boolean vulnerable) {
    if(r==Role.SECURITY_ADMIN) return action.startsWith("ADMIN_")||action.startsWith("AUDIT_");
    if(action.startsWith("ADMIN_")||action.startsWith("AUDIT_")) return false;
    if(vulnerable && r.ordinal()<Role.BRANCH_SUPERVISOR.ordinal()) return false;
    if(action.equals("CARD_MANAGE") && r==Role.CSR) return false;
    if(action.equals("FINANCIAL_APPROVE")) return Set.of(Role.BRANCH_SUPERVISOR,Role.REGIONAL_MANAGER,Role.HEAD_OFFICE_OPS).contains(r);
    if(action.equals("POLICY_MANAGE")) return r==Role.HEAD_OFFICE_OPS;
    return CUSTOMER_ACTIONS.contains(action)||Set.of("DASHBOARD","SEARCH","PROFILE").contains(action);
  }
  private Effective effective(StaffUser u) {
    String base=switch(u.role){case HEAD_OFFICE_OPS->"BANK";case REGIONAL_MANAGER->u.region;default->u.homeBranch;};
    BigDecimal limit=u.approvalLimit; Set<String> scopes=new LinkedHashSet<>();scopes.add(base); LocalDate now=LocalDate.now();
    for(Delegation d:delegations.findByDelegateIdAndStatus(u.employeeId,DelegationStatus.ACTIVE)) if(!now.isBefore(d.startDate)&&!now.isAfter(d.endDate)){scopes.add(d.grantedScope);limit=limit.max(d.grantedLimit);}
    return new Effective(String.join(",",scopes),limit);
  }
  private boolean inScope(String effective,String branch){if(effective.contains("BANK"))return true;String region=branchRegion(branch);return Arrays.stream(effective.split(",")).anyMatch(s->s.equals(branch)||s.equals(region));}
  private String branchRegion(String branch){int n=Integer.parseInt(branch.substring(1));return n<=3?"R1":n<=6?"R2":"R3";}
  @Transactional void advance(LocalDate date){for(Delegation d:delegations.findAll()){if(d.status==DelegationStatus.SCHEDULED&&!date.isBefore(d.startDate))d.status=DelegationStatus.ACTIVE;if(d.status==DelegationStatus.ACTIVE&&date.isAfter(d.endDate))d.status=DelegationStatus.EXPIRED;}}
  record Effective(String scope,BigDecimal approvalLimit){}
  record LoginResult(String token,String employeeId,String email,String role,String scope){}
  record Decision(boolean allowed,String scope,String role,BigDecimal actAloneThreshold,BigDecimal approvalLimit,String authorizationBasis){}
  record StaffView(String employeeId,String email,String role,String homeBranch,String region,boolean enabled,BigDecimal actAloneThreshold,BigDecimal approvalLimit){static StaffView of(StaffUser u){return new StaffView(u.employeeId,u.email,u.role.name(),u.homeBranch,u.region,u.enabled,u.actAloneThreshold,u.approvalLimit);}}
  record NewStaff(String employeeId,String email,String role,String homeBranch,String region,String temporaryPassword){}
  record NewDelegation(String delegatorId,String delegateId,String grantedScope,BigDecimal grantedLimit,LocalDate startDate,LocalDate endDate){}
  static class AuthenticationFailed extends RuntimeException {}
  static class Forbidden extends RuntimeException {}
}
