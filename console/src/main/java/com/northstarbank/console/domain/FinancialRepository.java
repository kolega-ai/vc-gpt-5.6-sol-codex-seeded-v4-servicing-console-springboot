package com.northstarbank.console.domain;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface FinancialRepository extends JpaRepository<FinancialRequest,String>{List<FinancialRequest> findTop50ByStatusOrderByRequestedAtAsc(String status);}
