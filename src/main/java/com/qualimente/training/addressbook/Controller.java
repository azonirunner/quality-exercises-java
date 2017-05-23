package com.qualimente.training.addressbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Endpoint defines the customer-specific RESTful endpoints of the AddressBook service.
 */
@RestController
@RequestMapping(value = "/customers/{customerId}/addressbook")
public class Controller {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final AddressDAO addressDAO;
  private final LocationDataValidator locationDataValidator;

  @Autowired
  public Controller(@NotNull AddressDAO AddressDAO, @NotNull LocationDataValidator locationDataValidator) {
    this.addressDAO = AddressDAO;
    this.locationDataValidator = locationDataValidator;
  }

  AddressDAO getAddressDAO() {
    return addressDAO;
  }

  @RequestMapping(value = "/addresses", method = RequestMethod.GET)
  public ResponseEntity<?> getAddressesForCustomer(@PathVariable("customerId") String customerId) {
    log.info("addresses requested for customerId: {}", customerId);
    try {
      List<Address> addressesForCustomerId = addressDAO.findAddressesForCustomerId(customerId);
      if (addressesForCustomerId == null) {
        return ResponseEntity.notFound().build();
      } else {
        return ResponseEntity.ok(addressesForCustomerId);
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @RequestMapping(value = "/addresses", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Address> addAddress(@PathVariable("customerId") String customerId, @RequestBody Address address) {
    log.info("adding address for customerId: {} address: {}", customerId, address);

    try {
      if(!locationDataValidator.isCountryCodeValid(address.getCountry())){
        return ResponseEntity.badRequest().body(address);
      }
      
      Address storedAddress = addressDAO.addAddress(customerId, address);
      return ResponseEntity.ok(storedAddress);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
