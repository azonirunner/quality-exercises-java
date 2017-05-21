package com.qualimente.training.addressbook;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ControllerTest {

  private LocationDataValidator locationDataValidator;

  @Before
  public void setUp(){
    locationDataValidator = mock(LocationDataValidator.class);
  }

  @Test
  public void constructor_should_store_arguments(){
    AddressDAO addressDAO = makeAddressDAO();
    Controller controller = new Controller(addressDAO, locationDataValidator);
    assertEquals(addressDAO, controller.getAddressDAO());
    assertEquals(locationDataValidator, controller.getLocationDataValidator());
  }

  @Test
  public void constructor_should_reject_null_arguments(){
    try {
      new Controller(null, locationDataValidator);
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

    Controller controller = new Controller(addressDAO, locationDataValidator);

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> actualAddresses = (List<Address>) body;
    assertEquals(expectedAddresses, actualAddresses);

    verifyZeroInteractions(locationDataValidator);
  }

  @Test
  public void addAddress_should_store_customer_address_in_repository(){
    Address expectedAddress = AddressTest.makeAddress();
    AddressDAO addressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());
    when(locationDataValidator.isCountryCodeValid(expectedAddress.getCountry()))
        .thenReturn(Boolean.TRUE);

    String customerId = "abcd-1234";
    Controller controller = new Controller(addressDAO, locationDataValidator);

    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedAddress, responseEntity.getBody());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> addresses = (List<Address>) body;
    assertTrue(addresses.contains(expectedAddress));

    verify(locationDataValidator).isCountryCodeValid(expectedAddress.getCountry());
    verifyNoMoreInteractions(locationDataValidator);
  }

  @Test
  public void addAddress_should_reject_address_with_invalid_countryCode(){
    String invalidCountryCode = "USA";

    AddressDAO addressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());
    when(locationDataValidator.isCountryCodeValid(invalidCountryCode))
        .thenReturn(Boolean.FALSE);

    String customerId = "abcd-1234";
    Controller controller = new Controller(addressDAO, locationDataValidator);

    Address expectedAddress = new Address(null, "invalid country code", "line1", "line2", "Tempe", "85044", "AZ", invalidCountryCode);

    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(expectedAddress, responseEntity.getBody());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> addresses = (List<Address>) body;
    assertNull(addresses);

    verify(locationDataValidator).isCountryCodeValid(invalidCountryCode);
    verifyNoMoreInteractions(locationDataValidator);
  }

  private AddressDAO makeAddressDAO() {
    return new AddressDAOMemoryImpl();
  }
}