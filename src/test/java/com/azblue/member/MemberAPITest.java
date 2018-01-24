package com.azblue.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Server.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberAPITest {

  @Value("${local.server.port}")
  int port;
  private RestTemplate restTemplate;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
  }

  @Test
  public void request_for_addresses_of_unknown_customer_should_respond_404() {
    assertMemberIsNotFound("does-not-exist");
  }


  @Test
  public void request_for_summary_of_user_that_exists_should_succeed() throws Exception {
    String memberId = makeMemberId();

    URI uri = new UriTemplate(getMemberSummaryUrlTemplate()).expand(memberId);
    System.out.println("uri: " + uri);
    final ResponseEntity<Summary> responseEntity =  restTemplate.getForEntity(uri, Summary.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    Summary actualSummary = responseEntity.getBody();
    System.out.println("Summary: " + MAPPER.writeValueAsString(actualSummary));

    assertEquals(memberId, actualSummary.getMember().getId());

    assertEquals("Homer", actualSummary.getMember().getFirstName());
    assertEquals("Simpson", actualSummary.getMember().getLastName());
    assertEquals("Homer Simpson", actualSummary.getMember().getFullName());
  }

  private String getMemberSummaryUrlTemplate() {
    return getBaseMemberUrlTemplate() + "/summary";
  }


  private void assertMemberIsNotFound(String memberId) {
    try {
      restTemplate.getForEntity(getBaseMemberUrlTemplate(),
          Member[].class,
          memberId);
      fail("Expected REST client to throw an exception");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }
  }

  private String getBaseMemberUrlTemplate() {
    return "http://localhost:" + port + "/members/{memberId}/";
  }

  private static String makeMemberId() {
      return UUID.randomUUID().toString();
    }
}
