package com.northstarbank.entitlements;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="staff_users")
public class StaffUser {
  @Id public String employeeId;
  @Column(unique=true,nullable=false) public String email;
  @Column(nullable=false) public String passwordHash;
  @Enumerated(EnumType.STRING) public Role role;
  public String homeBranch;
  public String region;
  @Column(precision=19,scale=2) public BigDecimal actAloneThreshold;
  @Column(precision=19,scale=2) public BigDecimal approvalLimit;
  public boolean enabled = true;
  protected StaffUser() {}
  StaffUser(String employeeId,String email,Role role,String homeBranch,String region,BigDecimal threshold,BigDecimal limit) {
    this.employeeId=employeeId; this.email=email; this.role=role; this.homeBranch=homeBranch; this.region=region;
    this.actAloneThreshold=threshold; this.approvalLimit=limit;
  }
}
