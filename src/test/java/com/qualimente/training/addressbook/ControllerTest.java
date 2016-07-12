package com.qualimente.training.addressbook;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {

  @Test
  public void constructor_should_store_arguments(){
    AddressDAO addressDAO = makeAddressDAO();
    LocationDataValidator locationDataValidator = makeLocationDataValidator();

    Controller controller = new Controller(addressDAO, locationDataValidator);

    assertEquals(addressDAO, controller.getAddressDAO());
    assertEquals(locationDataValidator, controller.getLocationDataValidator());
  }

  @Test
  public void constructor_should_reject_null_arguments(){
    try {
      new Controller(null, makeLocationDataValidator());
      fail("expected AssertionError");
    } catch (AssertionError e) {
      assertTrue("not-null assertion was triggered, as expected", true);
    }

    try {
      new Controller(makeAddressDAO(), null);
      fail("expected AssertionError");
    } catch (AssertionError e) {
      assertTrue("not-null assertion was triggered, as expected", true);
    }
  }

  @Test
  public void getAddressesForCustomer_should_find_customer_addresses_in_repository(){
    HashMap<String, List<Address>> addressesByCustomerId = new HashMap<>();
    String customerId = "abcd-1234";
    List<Address> expectedAddresses = AddressTest.makeAddresses();
    addressesByCustomerId.put(customerId, expectedAddresses);
    AddressDAO addressDAO = new AddressDAOMemoryImpl(addressesByCustomerId);

    Controller controller = new Controller(addressDAO, makeLocationDataValidator());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> actualAddresses = (List<Address>) body;
    assertEquals(expectedAddresses, actualAddresses);
  }

  @Test
  public void addAddress_should_store_customer_address_in_repository(){
    AddressDAO addressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());

    String customerId = "abcd-1234";
    Controller controller = new Controller(addressDAO, makeLocationDataValidator());

    Address expectedAddress = AddressTest.makeAddress();
    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedAddress, responseEntity.getBody());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> addresses = (List<Address>) body;
    assertTrue(addresses.contains(expectedAddress));
  }

  @Test
  public void addAddress_should_reject_invalid_country_codes(){
    String customerId = "abcd-1234";
    Controller controller = new Controller(makeAddressDAO(), makeLocationDataValidator());

    for(String invalidCountryCode : LocationDataValidatorImplTest.SAMPLE_INVALID_COUNTRY_CODES){
      Address expectedAddress = new Address(null, "name", "line1", null, "city", "85042", "state", invalidCountryCode);
      ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

      assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }
  }

  @Test
  public void addAddress_should_reject_invalid_postal_codes(){
    String customerId = "abcd-1234";
    Controller controller = new Controller(makeAddressDAO(), makeLocationDataValidator());

    Address expectedAddress = new Address(null, "name", "line1", null, "city", "24", "state", "US");
    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  private AddressDAO makeAddressDAO() {
    return new AddressDAOMemoryImpl();
  }

  private LocationDataValidator makeLocationDataValidator(){
    return LocationDataValidatorImpl.getInstance();
  }
}