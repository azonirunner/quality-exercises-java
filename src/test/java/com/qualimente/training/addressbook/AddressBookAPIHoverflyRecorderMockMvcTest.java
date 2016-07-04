package com.qualimente.training.addressbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AddressBookAPITest helps define and exercises the API contract of the AddressServer.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//SpringApplicationConfiguration starts-up full application
@SpringApplicationConfiguration(classes = { AddressBookServer.class, ContractValidationSupportConfiguration.class})
@WebIntegrationTest("server.port:8080") //change port to 0 for random
@ActiveProfiles("ContractValidationSupport")
public class AddressBookAPIHoverflyRecorderMockMvcTest {

  @Value("${local.server.port}")
  int port;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Autowired
  WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .build();
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

    Address expected = new Address(null, "42 Douglas Adams Way", null, "Phoenix", "85042", "AZ", "US");

    MvcResult result = mockMvc.perform(post(getCustomerAddressesUrl(), customerId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(MAPPER.writeValueAsString(expected)))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    Address actual = MAPPER.readValue(result.getResponse().getContentAsString(), Address.class);
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

    MvcResult result = mockMvc.perform(post(getCustomerAddressesUrl(), customerId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(addressJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    Address actual = MAPPER.readValue(result.getResponse().getContentAsString(), Address.class);
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
      mockMvc.perform(get(getCustomerAddressesUrl(), customerId))
          .andExpect(status().isNotFound());
    } catch (Exception e) {
      fail("Expected addressbook for customer to not be found, but exception occurred:" + e.getMessage());
    }
  }

  private static String makeCustomerId() {
    return UUID.randomUUID().toString();
  }

  private String getCustomerAddressesUrl() {
    return "http://localhost:" + port + "/customers/{customerId}/addressbook/addresses";
  }

}

