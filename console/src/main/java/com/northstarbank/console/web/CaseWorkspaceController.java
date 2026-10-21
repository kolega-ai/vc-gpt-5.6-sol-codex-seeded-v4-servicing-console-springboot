package com.northstarbank.console.web;
import com.northstarbank.console.domain.ServiceRequestRepository;
import com.northstarbank.console.service.AuditService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@Controller public class CaseWorkspaceController{
 private static final AtomicLong WORKSPACE_SEQUENCE=new AtomicLong();
 private final ConcurrentMap<String,Long> openWorkspaces=new ConcurrentHashMap<>();
 private final ServiceRequestRepository requests;private final AuditService audit;
 public CaseWorkspaceController(ServiceRequestRepository r,AuditService a){requests=r;audit=a;}
 @PostMapping("/cases/{id}/workspace")String open(@PathVariable long id,HttpServletResponse response){var r=requests.findById(id).orElseThrow();String sessionId="CASE-"+WORKSPACE_SEQUENCE.incrementAndGet();openWorkspaces.put(sessionId,id);Cookie cookie=new Cookie("CASE_WORKSPACE",sessionId);cookie.setPath("/");response.addCookie(cookie);audit.record("CASE_WORKSPACE_OPENED","service_request",1,r.customerId,"session",null,sessionId);return"redirect:/cases/"+id;}
 @GetMapping("/cases/{id}")String view(@PathVariable long id,@CookieValue(value="CASE_WORKSPACE",required=false)String sessionId,Model m){if(sessionId==null||!Long.valueOf(id).equals(openWorkspaces.get(sessionId)))throw new ResponseStatusException(HttpStatus.NOT_FOUND);var r=requests.findById(id).orElseThrow();m.addAttribute("request",r);m.addAttribute("workspace",sessionId);return"request-detail";}
}
