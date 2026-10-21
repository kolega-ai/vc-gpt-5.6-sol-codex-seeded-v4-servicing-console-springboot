package com.northstarbank.console.domain;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface DocumentRepository extends JpaRepository<CaseDocument,Long>{List<CaseDocument> findByCustomerIdOrderByCreatedAtDesc(String id);}
