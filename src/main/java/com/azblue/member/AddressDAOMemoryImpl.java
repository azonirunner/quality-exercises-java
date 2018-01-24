package com.azblue.member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AddressDAOMemoryImpl implements AddressBookDAO using an in-memory store.
 */
@Repository
public class AddressDAOMemoryImpl implements AddressDAO {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final Map<String, List<Address>> addressesByCustomerId;

  public AddressDAOMemoryImpl(@NotNull Map<String, List<Address>> addressesByCustomerId){
    this.addressesByCustomerId = new ConcurrentHashMap<>(addressesByCustomerId);
    log.info("creating DAO with {} customers", this.addressesByCustomerId.size());
  }

  public AddressDAOMemoryImpl(){
    this(new HashMap<>());
  }

  @Override
  public List<Address> findAddressesForCustomerId(String customerId) {
    log.debug("finding addresses for customer: {}", customerId);

    return addressesByCustomerId.getOrDefault(customerId, null);
  }

  @Override
  public Address addAddress(@NotNull String customerId, @NotNull Address address) {

    List<Address> addresses = this.addressesByCustomerId.get(customerId);

    if(addresses == null){
      addresses = new ArrayList<>();
      List<Address> oldAddresses = this.addressesByCustomerId.putIfAbsent(customerId, addresses);
      if(oldAddresses != null){
        addresses.addAll(oldAddresses);
      }
    }

    Address copy = address.copyWith(UUID.randomUUID().toString());
    addresses.add(copy);

    return copy;
  }

}
