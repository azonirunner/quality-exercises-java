package com.qualimente.training.addressbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
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

import static org.junit.Assert.*;

/**
 * AddressBookAPITest helps define and exercises the API contract of the AddressServer.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AddressBookServer.class)
@WebIntegrationTest("server.port:0")
public class AddressBookAPITest {

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
    String customerId = "does-not-exist";
    assertAddressBookIsNotFoundForCustomer(customerId);
  }

  @Test
  public void request_to_add_an_address_for_a_new_customer_should_succeed() throws Exception {
    String customerId = makeCustomerId();
    assertAddressBookIsNotFoundForCustomer(customerId);

    Address expected = new Address(null, "Ford Prefect", "42 Douglas Adams Way", null, "Phoenix", "85042", "AZ", "US");

    URI uri = new UriTemplate(getCustomerAddressesUrl()).expand(customerId);
    final RequestEntity<String> addAddressRequest = RequestEntity.post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(MAPPER.writeValueAsString(expected));

    ResponseEntity<Address> responseEntity = restTemplate.exchange(addAddressRequest, Address.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    Address actual = responseEntity.getBody();
    assertNotNull(actual.getId());
    assertEquals(expected, actual);
  }

  /**
   * Verify an address can be addeded for a new customer successfully using only
   * the minimal data required by the API and no automatic domain model serialization magic.
   *
   * @throws Exception when something unexpected happens, meaning test should fail
   */
  @Test
  public void request_to_add_an_address_for_a_new_customer_should_succeed_no_magic() throws Exception {
    String customerId = makeCustomerId();
    assertAddressBookIsNotFoundForCustomer(customerId);

    String expectedLine1 = "42 Douglas Adams Way";
    String expectedCity = "Phoenix";
    String expectedPostalCode = "85042";
    String expectedState = "AZ";
    String expectedCountry = "US";
    String addressJson = String.format("{" +
                            "\"line1\": \"%s\"," +
                            "\"city\": \"%s\"," +
                            "\"postalCode\": \"%s\"," +
                            "\"state\": \"%s\"," +
                            "\"country\": \"%s\"" +
                            "}", expectedLine1, expectedCity, expectedPostalCode, expectedState, expectedCountry);

    URI uri = new UriTemplate(getCustomerAddressesUrl()).expand(customerId);
    final RequestEntity<String> addAddressRequest = RequestEntity.post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(addressJson);

    ResponseEntity<Address> responseEntity = restTemplate.exchange(addAddressRequest, Address.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    Address actual = responseEntity.getBody();
    assertNotNull(actual.getId());
    assertEquals(expectedLine1, actual.getLine1());
    assertNull(actual.getLine2());
    assertEquals(expectedCity, actual.getCity());
    assertEquals(expectedPostalCode, actual.getPostalCode());
    assertEquals(expectedState, actual.getState());
    assertEquals(expectedCountry, actual.getCountry());
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
    return "http://localhost:" + port + "/customers/{customerId}/addressbook/addresses";
  }

}

