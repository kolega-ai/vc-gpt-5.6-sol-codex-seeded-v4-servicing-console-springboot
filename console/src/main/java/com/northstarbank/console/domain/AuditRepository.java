package com.northstarbank.console.domain;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditRepository extends JpaRepository<AuditEvent,Long>{List<AuditEvent> findTop100ByOrderByOccurredAtDesc();}
