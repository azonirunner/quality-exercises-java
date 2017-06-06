package com.qualimente.training.addressbook;

import java.util.List;

/**
 * AddressBookDAO defines an interface for loading and storing Addresses for an Address Book.
 */
public interface AddressDAO {

  /**
   * Find the addresses for a given customer id
   * @param customerId the customer id, not null
   * @return return the customer's list of addresses, null when customer does not exist
   */
  List<Address> findAddressesForCustomerId(String customerId);

  /**
   * Add an address to a customer's list of addresses.  Location data will be validated prior to storage.
   *  @param customerId the customer id, not null
   * @param address the address to add, not null
   */
  Address addAddress(String customerId, Address address);
  
}
