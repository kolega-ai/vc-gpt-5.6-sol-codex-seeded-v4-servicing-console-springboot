package com.northstarbank.console.domain;import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="password_reset_tokens") public class PasswordResetToken{@Id public String tokenHash;public String employeeId;public Instant expiresAt;public Instant usedAt;public PasswordResetToken(){}}
