package com.northstarbank.core;
import java.time.LocalDate;import java.util.*;import org.springframework.data.jpa.repository.JpaRepository;
interface CustomerRepo extends JpaRepository<CoreCustomer,String>{List<CoreCustomer> findByFullNameContainingIgnoreCaseOrIdContainingIgnoreCaseOrPhoneContaining(String n,String id,String phone);}
interface AccountRepo extends JpaRepository<CoreAccount,String>{List<CoreAccount> findByCustomerId(String id);}
interface TransactionRepo extends JpaRepository<CoreTransaction,Long>{List<CoreTransaction> findTop100ByAccountNumberAndBookingDateBetweenOrderByBookingDateDesc(String account,LocalDate from,LocalDate to);}
interface CardRepo extends JpaRepository<CoreCard,String>{List<CoreCard> findByCustomerId(String id);}
interface PostingRepo extends JpaRepository<CorePosting,Long>{Optional<CorePosting> findByRequestIdAndOperation(String r,String o);}
