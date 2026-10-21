package com.northstarbank.console.domain;import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="telephone_credentials") public class TelephoneCredential{@Id public String customerId;public String salt;public String digest;public Instant updatedAt=Instant.now();public TelephoneCredential(){}}
