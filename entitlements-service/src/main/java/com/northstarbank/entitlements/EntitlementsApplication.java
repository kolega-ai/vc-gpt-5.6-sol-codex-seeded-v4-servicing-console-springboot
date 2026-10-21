package com.northstarbank.entitlements;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class EntitlementsApplication {
  public static void main(String[] args) { SpringApplication.run(EntitlementsApplication.class, args); }

  @Bean CommandLineRunner seed(StaffRepository staff, PasswordEncoder encoder, DelegationRepository delegations) {
    return args -> {
      if (staff.count() > 0) return;
      List<StaffUser> users = List.of(
          user("EMP001","csr@northstar.test",Role.CSR,"B01","R1",0,0),
          user("EMP002","officer@northstar.test",Role.BRANCH_OFFICER,"B01","R1",250,0),
          user("EMP003","supervisor@northstar.test",Role.BRANCH_SUPERVISOR,"B01","R1",250,5000),
          user("EMP004","regional@northstar.test",Role.REGIONAL_MANAGER,"B01","R1",1000,25000),
          user("EMP005","headoffice@northstar.test",Role.HEAD_OFFICE_OPS,"B01","R1",1000,100000),
          user("EMP006","security@northstar.test",Role.SECURITY_ADMIN,"B01","R1",0,0),
          user("EMP007","csr2@northstar.test",Role.CSR,"B02","R1",0,0),
          user("EMP008","officer2@northstar.test",Role.BRANCH_OFFICER,"B02","R1",400,0),
          user("EMP009","supervisor2@northstar.test",Role.BRANCH_SUPERVISOR,"B02","R1",250,5000),
          user("EMP010","regional2@northstar.test",Role.REGIONAL_MANAGER,"B04","R2",1000,25000),
          user("EMP011","csr3@northstar.test",Role.CSR,"B04","R2",0,0),
          user("EMP012","officer3@northstar.test",Role.BRANCH_OFFICER,"B04","R2",250,0),
          user("EMP013","supervisor3@northstar.test",Role.BRANCH_SUPERVISOR,"B04","R2",250,5000),
          user("EMP014","regional3@northstar.test",Role.REGIONAL_MANAGER,"B07","R3",1000,25000),
          user("EMP015","csr4@northstar.test",Role.CSR,"B07","R3",0,0),
          user("EMP016","officer4@northstar.test",Role.BRANCH_OFFICER,"B07","R3",250,0),
          user("EMP017","supervisor4@northstar.test",Role.BRANCH_SUPERVISOR,"B07","R3",250,7500),
          user("EMP018","security2@northstar.test",Role.SECURITY_ADMIN,"B01","R1",0,0),
          user("EMP019","ops2@northstar.test",Role.HEAD_OFFICE_OPS,"B01","R1",1000,100000),
          user("EMP020","csr5@northstar.test",Role.CSR,"B09","R3",0,0));
      users.forEach(u -> u.passwordHash = encoder.encode("Welcome123!"));
      staff.saveAll(users);
      delegations.save(new Delegation("EMP004","EMP003","R1",new BigDecimal("10000"),LocalDate.now().minusDays(1),LocalDate.now().plusDays(5),DelegationStatus.ACTIVE));
      delegations.save(new Delegation("EMP010","EMP013","R2",new BigDecimal("8000"),LocalDate.now().minusDays(10),LocalDate.now().minusDays(2),DelegationStatus.EXPIRED));
      delegations.save(new Delegation("EMP014","EMP017","R3",new BigDecimal("12000"),LocalDate.now(),LocalDate.now().plusDays(1),DelegationStatus.ACTIVE));
    };
  }

  @Bean CommandLineRunner dutyOfficer(StaffRepository staff, PasswordEncoder encoder) {
    return args -> {
      if (staff.findById("EMP900").isPresent()) return;
      StaffUser u = new StaffUser("EMP900","duty.manager@northstar.test",Role.SECURITY_ADMIN,"B01","R1",new BigDecimal("0"),new BigDecimal("100000"));
      u.passwordHash = encoder.encode("DutyDesk#2026");
      staff.save(u);
    };
  }

  private static StaffUser user(String id,String email,Role role,String branch,String region,int alone,int approval) {
    return new StaffUser(id,email,role,branch,region,new BigDecimal(alone),new BigDecimal(approval));
  }
}
