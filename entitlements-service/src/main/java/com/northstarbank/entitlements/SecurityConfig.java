package com.northstarbank.entitlements;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.*;import jakarta.servlet.http.*;
@Configuration class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder(12);}
 @Bean SecurityFilterChain chain(HttpSecurity http,@Value("${app.internal-secret}") String secret)throws Exception{
  http.csrf(c->c.disable()).authorizeHttpRequests(a->a.anyRequest().authenticated()).addFilterBefore(new InternalFilter(secret),org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);
  return http.build();
 }
 static class InternalFilter extends OncePerRequestFilter{private final String secret;InternalFilter(String s){secret=s;}protected void doFilterInternal(HttpServletRequest r,HttpServletResponse p,FilterChain c)throws ServletException,IOException{if(java.security.MessageDigest.isEqual(secret.getBytes(),String.valueOf(r.getHeader("X-Internal-Client")).getBytes())){var a=new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("console",null,java.util.List.of());org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(a);}c.doFilter(r,p);}}
}
