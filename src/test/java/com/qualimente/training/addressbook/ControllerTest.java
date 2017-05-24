package com.qualimente.training.addressbook;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {

  @Test
  public void constructor_should_store_arguments(){
    AddressDAO AddressDAO = makeAddressDAO();
    Controller controller = new Controller(AddressDAO, new LocationDataValidator());
    assertEquals(AddressDAO, controller.getAddressDAO());
  }

  @Test
  public void constructor_should_reject_null_arguments(){
    try {
      new Controller(null, new LocationDataValidator());
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
    AddressDAO AddressDAO = new AddressDAOMemoryImpl(addressesByCustomerId);

    Controller controller = new Controller(AddressDAO, new LocationDataValidator());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> actualAddresses = (List<Address>) body;
    assertEquals(expectedAddresses, actualAddresses);
  }

  @Test
  public void addAddress_should_store_customer_address_in_repository(){
    AddressDAO AddressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());

    String customerId = "abcd-1234";
    Controller controller = new Controller(AddressDAO, new LocationDataValidator());

    Address expectedAddress = AddressTest.makeAddress();
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

    String customerId = "abcd-1234";
    Controller controller = new Controller(addressDAO, new LocationDataValidator());

    Address address = new Address(null, "name", "line 1", "line 2", "city - ", "postal code - ", "state - "
        , "invalid country");

    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, address);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals(address, responseEntity.getBody());
  }

  @Test
  public void addAdress_should_retry_a_retryable_failure() {

    String customerId = "abcd-1234";
    Address originalAddress = AddressTest.makeAddress();

    AddressDAO addressDAO = Mockito.mock(AddressDAO.class);

    AddressDAO.RetryableException retryableException = new AddressDAO.RetryableException();
    Address storedAddress = originalAddress.copyWith("my generated id");
    Mockito.when(addressDAO.addAddress(customerId, originalAddress))
        .thenThrow(retryableException) //first invocation
        .thenReturn(storedAddress); //second invocation


    Controller controller = new Controller(addressDAO, new LocationDataValidator());

    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, originalAddress);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(originalAddress, responseEntity.getBody());
    assertEquals(storedAddress.getId(), responseEntity.getBody().getId());

    Mockito.verify(addressDAO, Mockito.times(2)).addAddress(customerId, originalAddress);

  }

  private AddressDAO makeAddressDAO() {
    return new AddressDAOMemoryImpl();
  }
}