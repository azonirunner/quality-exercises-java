package com.azblue.member;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AddressDAOMemoryImplTest {

  private AddressDAOMemoryImpl addressBookDAO;
  private Map<String, List<Address>> addressesByCustomerId;

  @Before
  public void setUp() {
    this.addressesByCustomerId = makeAddressesByCustomerId();
    this.addressBookDAO = new AddressDAOMemoryImpl(addressesByCustomerId);
  }

  static Map<String, List<Address>> makeAddressesByCustomerId() {
    int numCustomers = 1000;
    Map<String, List<Address>> addressesByCustomerId = new HashMap<>(numCustomers);

    for (int i = 0; i < numCustomers; i++) {
      addressesByCustomerId.put(makeCustomerId(), AddressTest.makeAddresses());
    }
    return addressesByCustomerId;
  }

  private static String makeCustomerId() {
    return UUID.randomUUID().toString();
  }

  @Test
  public void findAddressesForCustomerId_should_return_expected_addresses_for_existing_customers() throws Exception {
    for (Map.Entry<String, List<Address>> customerIdAndAddresses : this.addressesByCustomerId.entrySet()) {
      assertEquals(customerIdAndAddresses.getValue(), addressBookDAO.findAddressesForCustomerId(customerIdAndAddresses.getKey()));
    }
  }

  @Test
  public void findAddressesForCustomerId_should_return_null_when_customer_does_not_have_an_address_book() {
    for (int i = 0; i < 10; i++) {
      assertNull(addressBookDAO.findAddressesForCustomerId(UUID.randomUUID().toString()));
    }
  }

  @Test
  public void addAddress_should_add_an_address_to_existing_customers_addresses() {

    for (Map.Entry<String, List<Address>> customerIdAndAddresses : this.addressesByCustomerId.entrySet()) {
      Address newAddress = AddressTest.makeAddress();
      String customerId = customerIdAndAddresses.getKey();

      this.addressBookDAO.addAddress(customerId, newAddress);

      assertTrue(addressBookDAO.findAddressesForCustomerId(customerId).contains(newAddress));
    }

  }

  @Test
  public void addAddress_should_add_an_address_to_new_customers_addresses() {

    int numAddresses = 100;
    Set<String> ids = new HashSet<>(numAddresses);
    for (int i = 0; i < numAddresses; i++) {
      Address newAddress = AddressTest.makeAddress();
      String customerId = makeCustomerId();
      assertNull(newAddress.getId());

      Address storedAddress = this.addressBookDAO.addAddress(customerId, newAddress);

      assertEquals(newAddress, storedAddress);
      assertNotNull(storedAddress.getId());
      ids.add(storedAddress.getId());

      assertTrue(addressBookDAO.findAddressesForCustomerId(customerId).contains(newAddress));
    }

    assertEquals(numAddresses, ids.size());
  }

}