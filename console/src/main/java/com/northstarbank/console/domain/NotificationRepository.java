package com.northstarbank.console.domain;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationRepository extends JpaRepository<Notification,Long>{List<Notification> findTop10ByRecipientAndReadFalseOrderByCreatedAtDesc(String employee);}
