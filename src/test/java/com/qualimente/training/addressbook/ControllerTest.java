package com.qualimente.training.addressbook;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControllerTest {

  @Test
  public void constructor_should_store_arguments(){
    AddressDAO addressDAO = makeAddressDAO();
    LocationDataValidator locationDataValidator = mock(LocationDataValidator.class);

    Controller controller = new Controller(addressDAO, locationDataValidator);
    assertEquals(addressDAO, controller.getAddressDAO());
    assertEquals(locationDataValidator, controller.getLocationDataValidator());
  }

  @Test
  public void constructor_should_reject_null_arguments(){
    try {
      new Controller(null, mock(LocationDataValidator.class));
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
    LocationDataValidator locationDataValidator = mock(LocationDataValidator.class);

    Controller controller = new Controller(addressDAO, locationDataValidator);

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> actualAddresses = (List<Address>) body;
    assertEquals(expectedAddresses, actualAddresses);
  }

  @Test
  public void addAddress_should_store_customer_address_in_repository(){
    AddressDAO addressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());
    LocationDataValidator locationDataValidator = mock(LocationDataValidator.class);

    String customerId = "abcd-1234";
    Controller controller = new Controller(addressDAO, locationDataValidator);

    Address expectedAddress = AddressTest.makeAddress();

    when(locationDataValidator.isCountryCodeValid(expectedAddress.getCountry()))
            .thenReturn(Boolean.TRUE);

    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedAddress, responseEntity.getBody());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> addresses = (List<Address>) body;
    assertTrue(addresses.contains(expectedAddress));
  }

  @Test
  public void addAddress_should_reject_customer_address_with_invalid_country_code(){
    AddressDAO addressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());
    LocationDataValidator locationDataValidator = mock(LocationDataValidator.class);

    String customerId = "abcd-1234";
    Controller controller = new Controller(addressDAO, locationDataValidator);

    Address invalidAddress = new Address(null, "name", "line1", "line2", "city", "85040", "AZ", "BOGUS");

    when(locationDataValidator.isCountryCodeValid(invalidAddress.getCountry()))
        .thenReturn(Boolean.FALSE);

    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, invalidAddress);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  private AddressDAO makeAddressDAO() {
    return new AddressDAOMemoryImpl();
  }
}