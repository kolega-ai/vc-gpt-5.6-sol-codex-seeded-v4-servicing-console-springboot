package com.northstarbank.console.domain;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface NoteRepository extends JpaRepository<CaseNote,Long>{List<CaseNote> findByCustomerIdOrderByCreatedAtDesc(String id);}
