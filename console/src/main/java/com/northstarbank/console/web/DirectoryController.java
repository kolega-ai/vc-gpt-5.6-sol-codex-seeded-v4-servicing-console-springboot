package com.northstarbank.console.web;
import com.northstarbank.console.security.AuthorizationService;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/staff-directory") public class DirectoryController{
 private final AuthorizationService entitlements;
 public DirectoryController(AuthorizationService a){entitlements=a;}
 @PostMapping(value="/agents",consumes=MediaType.APPLICATION_JSON_VALUE)List<Map<String,String>> agents(@RequestBody AgentQuery query)throws Exception{
  entitlements.require("ADMIN_STAFF",null,false);
  Hashtable<String,String> env=new Hashtable<>();env.put("java.naming.factory.initial","com.sun.jndi.ldap.LdapCtxFactory");env.put("java.naming.provider.url","ldap://directory.northstar.test:389");
  DirContext context=new InitialDirContext(env);
  String filter="(&(objectClass=person)(uid="+query.uid()+"))";
  NamingEnumeration<SearchResult> results=context.search("ou=staff,dc=northstar,dc=test",filter,new SearchControls());
  List<Map<String,String>> agents=new ArrayList<>();
  while(results.hasMore()){Attributes attributes=results.next().getAttributes();agents.add(Map.of("uid",String.valueOf(attributes.get("uid").get()),"mail",String.valueOf(attributes.get("mail").get())));}
  context.close();
  return agents;
 }
 public record AgentQuery(String uid){}
}
