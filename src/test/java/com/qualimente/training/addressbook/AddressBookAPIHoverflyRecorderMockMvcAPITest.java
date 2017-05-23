package com.qualimente.training.addressbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.hoverfly.recorder.HoverflyFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AddressBookAPITest helps define and exercises the API contract of the AddressServer.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//SpringApplicationConfiguration starts-up full application
@SpringApplicationConfiguration(classes = {AddressBookServer.class, ContractValidationSupportConfiguration.class})
@WebIntegrationTest("server.port:0") //set server.port to 0 for random
public class AddressBookAPIHoverflyRecorderMockMvcAPITest {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Rule
  public TestName testName = new TestName();

  @Value("${local.server.port}")
  int port;

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Autowired
  WebApplicationContext webApplicationContext;

  private HoverflyFilter hoverflyFilter;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    log.info("Started server on port: " + port);
    String recordingFile = "target/generated-sources/" + "http-recording." + getClass().getSimpleName() + "." + testName.getMethodName() + ".json";
    log.info("will record http traffic to: " + recordingFile);
    hoverflyFilter = new HoverflyFilter("http://localhost:" + port, recordingFile);

    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilter(hoverflyFilter)
        .build();
  }

  @After
  public void tearDown() {
    if (hoverflyFilter != null) {
      hoverflyFilter.destroy();
    }
  }

  @Test
  public void request_for_addresses_of_unknown_customer_should_respond_404() throws Exception {
    String customerId = "does-not-exist";
    assertAddressBookIsNotFoundForCustomer(customerId);
  }

  @Test
  public void request_to_add_an_address_for_a_new_customer_should_succeed() throws Exception {
    String customerId = makeCustomerId();
    assertAddressBookIsNotFoundForCustomer(customerId);

    Address expected = new Address(null, "Zaphod Beeblebrox", "42 Douglas Adams Way", null, "Phoenix", "85042", "AZ", "US");

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

  private void assertAddressBookIsNotFoundForCustomer(String customerId) throws Exception {
    mockMvc.perform(get(getCustomerAddressesUrl(), customerId))
        .andExpect(status().isNotFound());
  }

  private static String makeCustomerId() {
    return UUID.randomUUID().toString();
  }

  private String getCustomerAddressesUrl() {
    return "http://localhost:" + port + "/customers/{customerId}/addressbook/addresses";
  }

}

