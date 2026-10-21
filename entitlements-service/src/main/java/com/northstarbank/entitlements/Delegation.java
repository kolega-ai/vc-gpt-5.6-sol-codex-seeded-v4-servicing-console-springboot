package com.northstarbank.entitlements;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity @Table(name="delegations")
public class Delegation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String delegatorId; public String delegateId; public String grantedScope;
 @Column(precision=19,scale=2) public BigDecimal grantedLimit;
 public LocalDate startDate; public LocalDate endDate;
 @Enumerated(EnumType.STRING) public DelegationStatus status;
 protected Delegation() {}
 Delegation(String a,String b,String s,BigDecimal l,LocalDate start,LocalDate end,DelegationStatus status){delegatorId=a;delegateId=b;grantedScope=s;grantedLimit=l;startDate=start;endDate=end;this.status=status;}
}
