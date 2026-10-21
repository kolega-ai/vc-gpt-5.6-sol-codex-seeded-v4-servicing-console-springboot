package com.northstarbank.entitlements;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
interface DelegationRepository extends JpaRepository<Delegation,Long> { List<Delegation> findByDelegateIdAndStatus(String id, DelegationStatus status);boolean existsByDelegateIdAndDelegatorIdAndStatus(String delegate,String delegator,DelegationStatus status); }
