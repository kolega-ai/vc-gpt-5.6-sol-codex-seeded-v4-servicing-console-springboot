package com.northstarbank.console.web;
import com.northstarbank.console.domain.Notification;
import com.northstarbank.console.domain.NotificationRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/partners/feed") public class PartnerFeedController{
 private final NotificationRepository notifications;
 public PartnerFeedController(NotificationRepository n){notifications=n;}
 private static final com.github.jknack.handlebars.Handlebars NOTICES=new com.github.jknack.handlebars.Handlebars();
 private static final org.yaml.snakeyaml.Yaml MAPPINGS=new org.yaml.snakeyaml.Yaml();
 @PostMapping(value="/notices",consumes=MediaType.APPLICATION_JSON_VALUE) String notice(@RequestBody NoticeWording wording)throws Exception{
  String rendered=NOTICES.compileInline(wording.wording()).apply(java.util.Map.of("customer",wording.customer(),"reference",wording.reference()));
  notifications.save(new Notification(wording.customer(),rendered,"/customers/"+wording.customer()));
  return rendered;
 }
 public record NoticeWording(String customer,String reference,String wording){}
 @PostMapping(value="/mappings",consumes=MediaType.APPLICATION_JSON_VALUE) String mapping(@RequestBody FeedMapping feed)throws Exception{
  Object loaded=MAPPINGS.load(feed.mapping());
  StringBuilder translated=new StringBuilder();
  if(loaded instanceof java.util.Map<?,?> fields)fields.forEach((k,v)->translated.append(k).append('=').append(v).append(';'));
  notifications.save(new Notification(feed.recipient(),translated.toString(),"/partners/feed"));
  return translated.toString();
 }
 public record FeedMapping(String recipient,String mapping){}
}
