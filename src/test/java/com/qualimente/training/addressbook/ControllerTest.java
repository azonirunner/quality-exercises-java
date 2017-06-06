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
    Controller controller = new Controller(AddressDAO);
    assertEquals(AddressDAO, controller.getAddressDAO());
  }

  @Test
  public void constructor_should_reject_null_arguments(){
    try {
      new Controller(null);
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

    Controller controller = new Controller(AddressDAO);

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> actualAddresses = (List<Address>) body;
    assertEquals(expectedAddresses, actualAddresses);
  }

  @Test
  public void addAddress_should_store_customer_address_in_repository(){
    AddressDAO AddressDAO = new AddressDAOMemoryImpl(AddressDAOMemoryImplTest.makeAddressesByCustomerId());

    String customerId = "abcd-1234";
    Controller controller = new Controller(AddressDAO);

    Address expectedAddress = AddressTest.makeAddress();
    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, expectedAddress);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedAddress, responseEntity.getBody());

    Object body = controller.getAddressesForCustomer(customerId).getBody();
    @SuppressWarnings("unchecked") List<Address> addresses = (List<Address>) body;
    assertTrue(addresses.contains(expectedAddress));
  }

  @Test
  public void addAddress_should_reject_addresses_with_invalid_country_codes(){
    //given
    String customerId = "abcd-1234";
    Address invalidAddress = new Address(null, "line1", "line2", "city", "85224", "AZ", "invalid");
    AddressDAO addressDAO = Mockito.mock(AddressDAO.class);
    Mockito.when(addressDAO.addAddress(customerId, invalidAddress))
        .thenThrow(new IllegalArgumentException("Invalid address " + invalidAddress.getCountry()));

    //when
    Controller controller = new Controller(addressDAO);
    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, invalidAddress);

    //then
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    Mockito.verify(addressDAO, Mockito.times(1))
        .addAddress(customerId, invalidAddress);
  }

  @Test
  public void addAddress_should_retry_adding_address_once_on_retryable_failure(){
    //given:
    //create valid address and controller
    String customerId = "abcd-1234";
    Address address = AddressTest.makeAddress();

    //make a mock DAO that will fail first time and pass the second
    AddressDAO addressDAO = Mockito.mock(AddressDAO.class);

    //defines multiple/consecutive calls to addAddress
    Mockito.when(addressDAO.addAddress(customerId, address))
        .thenThrow(new AddressDAO.RetryableException())
        .thenReturn(address);

    Controller controller = new Controller(addressDAO);

    //when:
    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, address);

    //then:
    //status code
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(address, responseEntity.getBody());
    
    //verify DAO's add Address called twice
    Mockito.verify(addressDAO, Mockito.times(2))
        .addAddress(customerId, address);
    Mockito.verifyNoMoreInteractions(addressDAO);
  }
  
  @Test
  public void addAddress_should_retry_adding_address_once_on_retryable_failure_eventual_failure(){
    //given:
    //create valid address and controller
    //make a mock DAO that will fail first time and pass the second
    AddressDAO addressDAO = Mockito.mock(AddressDAO.class);

    String customerId = "abcd-1234";
    Address address = AddressTest.makeAddress();

    //defines multiple/consecutive calls to addAddress
    Mockito.when(addressDAO.addAddress(customerId, address))
        .thenThrow(new AddressDAO.RetryableException())
        .thenThrow(new AddressDAO.RetryableException());

    Controller controller = new Controller(addressDAO);

    //when:
    ResponseEntity<Address> responseEntity = controller.addAddress(customerId, address);

    //then:
    //status code
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    //verify DAO's add Address called twice
    Mockito.verify(addressDAO, Mockito.times(2))
        .addAddress(customerId, address);
    Mockito.verifyNoMoreInteractions(addressDAO);
  }

// invalid - tries to define behavior of addAddress multiple times
//    Mockito.when(addressDAO.addAddress(customerId, address))
//        .thenThrow(new RuntimeException("retryable failure"));
//    Mockito.when(addressDAO.addAddress(customerId, address))
//        .thenReturn(address);

  private AddressDAO makeAddressDAO() {
    return new AddressDAOMemoryImpl();
  }
}