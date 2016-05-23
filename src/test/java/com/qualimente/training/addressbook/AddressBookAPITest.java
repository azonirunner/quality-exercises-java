package com.qualimente.training.addressbook;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * AddressBookAPITest helps define and exercises the API contract of the AddressServer.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AddressBookServer.class)
@WebIntegrationTest
public class AddressBookAPITest {

  private static final String BASE_REQUEST_URI = "http://localhost:8080";
  private RestTemplate restTemplate;

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
  }

  @Test
  public void request_for_addresses_of_unknown_customer_should_respond_404() {
    String customerId = "does-not-exist";

    try {
      restTemplate.getForEntity(getCustomerAddressesURI(),
          Address[].class,
          customerId);
      fail("Expected REST client to throw an exception");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }
  }

  private String getCustomerAddressesURI() {
    return BASE_REQUEST_URI + "/customers/{customerId}/addressbook/addresses";
  }

}

