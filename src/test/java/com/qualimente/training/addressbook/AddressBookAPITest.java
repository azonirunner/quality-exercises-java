package com.qualimente.training.addressbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
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

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
  }

  @Test
  public void request_for_addresses_of_unknown_customer_should_respond_404() {
    String customerId = "does-not-exist";
    assertAddressBookIsNotFoundForCustomer(customerId);
  }

  @Test
  public void request_to_add_an_address_for_a_new_customer_should_succeed() throws Exception {
    String customerId = makeCustomerId();
    assertAddressBookIsNotFoundForCustomer(customerId);

    Address expected = new Address(null, "42 Douglas Adams Way", null, "Phoenix", "85225", "AZ", "US");

    URI uri = new UriTemplate(getCustomerAddressesUrl()).expand(customerId);
    final RequestEntity<String> addAddressRequest = RequestEntity.post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MAPPER.writeValueAsString(expected));

    ResponseEntity<Address> responseEntity = restTemplate.exchange(addAddressRequest, Address.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    Address actual = responseEntity.getBody();
    assertEquals(expected, actual);
  }

  private void assertAddressBookIsNotFoundForCustomer(String customerId) {
    try {
      restTemplate.getForEntity(getCustomerAddressesUrl(),
          Address[].class,
          customerId);
      fail("Expected REST client to throw an exception");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }
  }

  private static String makeCustomerId() {
    return UUID.randomUUID().toString();
  }

  private String getCustomerAddressesUrl() {
    return BASE_REQUEST_URI + "/customers/{customerId}/addressbook/addresses";
  }

}

