package com.northstarbank.entitlements;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
interface StaffRepository extends JpaRepository<StaffUser,String> { Optional<StaffUser> findByEmailIgnoreCase(String email); }
