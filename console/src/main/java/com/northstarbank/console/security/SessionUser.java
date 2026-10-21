package com.northstarbank.console.security;
import java.util.*;import org.springframework.security.core.*;import org.springframework.security.core.userdetails.UserDetails;
public record SessionUser(String employeeId,String email,String role,String scope,String token) implements UserDetails{
 public Collection<? extends GrantedAuthority> getAuthorities(){return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_"+role));}public String getPassword(){return "";}public String getUsername(){return email;}
}
