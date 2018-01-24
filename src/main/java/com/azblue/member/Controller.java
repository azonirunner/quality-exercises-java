package com.azblue.member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller exposes RESTful services for the member domain.
 */
@RestController
@RequestMapping(value = "/members/{memberId}/")
public class Controller {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public ResponseEntity<Summary> getSummary(@PathVariable("memberId") String memberId) {
      log.info("Summary requested for memberId: {}", memberId);
      try {
        if (memberId == null) {
          return ResponseEntity.notFound().build();
        } else {
          Summary summary = new Summary(findMember(memberId));
          log.info("created Summary for memberId: {}", memberId);
          return ResponseEntity.ok(summary);
        }
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
    }

  private Member findMember(String memberId) throws RuntimeException {
    //TODO: lookup from database instead of returning this mock data
    return new Member(memberId, "Homer", "Simpson");
  }

}
