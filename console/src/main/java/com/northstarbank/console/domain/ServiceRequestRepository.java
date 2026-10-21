package com.northstarbank.console.domain;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest,Long>{List<ServiceRequest> findTop50ByOwningBranchInOrderByCreatedAtDesc(Collection<String> branches);long countByStatus(String status);}
