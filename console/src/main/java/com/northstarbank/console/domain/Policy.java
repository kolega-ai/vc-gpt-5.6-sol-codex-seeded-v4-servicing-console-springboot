package com.northstarbank.console.domain;import jakarta.persistence.*;import java.math.BigDecimal;
@Entity @Table(name="policy") public class Policy{@Id public Long id=1L;public String currency="GBP";public BigDecimal policyCeiling;public String escalationEndpoint;public String noticeTemplate;protected Policy(){}public Policy(BigDecimal c){policyCeiling=c;}}
