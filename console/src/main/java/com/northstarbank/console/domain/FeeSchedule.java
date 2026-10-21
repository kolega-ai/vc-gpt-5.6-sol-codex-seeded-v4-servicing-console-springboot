package com.northstarbank.console.domain;import jakarta.persistence.*;import java.math.BigDecimal;
@Entity @Table(name="fee_schedule") public class FeeSchedule{@Id public String feeCode;public String productType;public String currency;public BigDecimal amount;protected FeeSchedule(){}public FeeSchedule(String c,String p,BigDecimal a){feeCode=c;productType=p;currency="GBP";amount=a;}}
