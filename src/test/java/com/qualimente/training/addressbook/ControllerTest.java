package com.qualimente.training.addressbook;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(JUnitQuickcheck.class)
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

  @Property
  public void getAddressesForCustomer_should_find_customer_addresses_in_repository(UUID customerUUID){
    HashMap<String, List<Address>> addressesByCustomerId = new HashMap<>();
    String customerId = customerUUID.toString();
    List<Address> expectedAddresses = AddressTest.makeAddresses();
    addressesByCustomerId.put(customerId, expectedAddresses);
    AddressDAO addressDAO = new AddressDAOMemoryImpl(addressesByCustomerId);

    Controller controller = new Controller(addressDAO, makeLocationDataValidator());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> actualAddresses = (List<Address>) body;
    assertEquals(expectedAddresses, actualAddresses);
  }

  @Property
  public void addAddress_should_store_customer_address_in_repository
      (UUID customerUUID, @From(AddressGenerator.class) Address expectedAddress){
    AddressDAO addressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());

    String customerId = customerUUID.toString();
    //System.out.println("customerId: " + customerId);
    Controller controller = new Controller(addressDAO, makeLocationDataValidator());

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
    LocationDataValidator locationDataValidator = mock(LocationDataValidator.class);
    when(locationDataValidator.isCountryCodeValid(anyString())).thenReturn(Boolean.FALSE);

    Controller controller = new Controller(makeAddressDAO(), locationDataValidator);
    Address expectedAddress = AddressTest.makeAddress();
    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    verify(locationDataValidator).isCountryCodeValid(expectedAddress.getCountry());
    verifyNoMoreInteractions(locationDataValidator);
  }

  @Test
  public void addAddress_should_reject_invalid_postal_codes(){
    String customerId = "abcd-1234";
    Address expectedAddress = AddressTest.makeAddress();

    LocationDataValidator locationDataValidator = mock(LocationDataValidator.class);
    when(locationDataValidator.isCountryCodeValid(expectedAddress.getCountry())).thenReturn(Boolean.TRUE);
    when(locationDataValidator.isPostalCodeValid(eq(expectedAddress.getCountry()), anyString()))
        .thenReturn(Boolean.FALSE);

    Controller controller = new Controller(makeAddressDAO(), locationDataValidator);

    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    verify(locationDataValidator).isCountryCodeValid(expectedAddress.getCountry());
    verify(locationDataValidator).isPostalCodeValid(expectedAddress.getCountry(), expectedAddress.getPostalCode());
    verifyNoMoreInteractions(locationDataValidator);
  }

  private AddressDAO makeAddressDAO() {
    return new AddressDAOMemoryImpl();
  }

  private LocationDataValidator makeLocationDataValidator(){
    return LocationDataValidatorImpl.getInstance();
  }
}