package com.northstarbank.console.domain;import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="outbox") public class Outbox{@Id @GeneratedValue(strategy=GenerationType.IDENTITY)public Long id;public String recipient;public String subject;public String body;public String status;public Instant createdAt=Instant.now();public Outbox(){}}
