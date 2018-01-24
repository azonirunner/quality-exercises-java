package com.azblue.member;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

/**
 * WelcomePageIT is responsible for verifying the homepage works properly.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Server.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WelcomePageIT {

  @Value("${local.server.port}")
  int port;
  private RestTemplate restTemplate;

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
  }

  @Test
  public void welcome_page_should_contain_server_name() {
    String body = this.restTemplate.getForObject(getServerRootUrl(), String.class);

    assertThat(body, containsString("Address Book Server"));
  }

  private String getServerRootUrl() {
    return "http://localhost:" + port + "/";
  }
}
