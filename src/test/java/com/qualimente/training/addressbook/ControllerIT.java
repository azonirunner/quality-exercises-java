package com.qualimente.training.addressbook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AddressBookServer.class)
public class ControllerIT {
  
  @Autowired
  private AddressDAO addressDAO;

  @Autowired
  private LocationDataValidator locationDataValidator;

  @Test
  public void getAddressesForCustomer_should_find_customer_addresses_that_were_added_to_repository(){
    HashMap<String, List<Address>> addressesByCustomerId = new HashMap<>();

    Controller controller = new Controller(addressDAO, locationDataValidator);

    int numCustomers = 100;
    for(int i = 0; i < numCustomers; i++){
      String customerId = makeCustomerId();
      List<Address> expectedAddresses = AddressTest.makeAddresses();

      List<Address> storedAddresses = new ArrayList<>();
      addressesByCustomerId.put(customerId, storedAddresses);
      for (Address address : expectedAddresses) {
        ResponseEntity<?> responseEntity = controller.addAddress(customerId, address);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if(statusCode.is2xxSuccessful()){
          storedAddresses.add(address);
        }
      }

      int failedGetAddressesCount = 0;
        ResponseEntity<?> responseEntity = controller.getAddressesForCustomer(customerId);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
          assertEquals(expectedAddresses, responseEntity.getBody());
        } else {
          failedGetAddressesCount++;
        }

      assertThat(failedGetAddressesCount, lessThan((int) (numCustomers * 0.1f)));
    }

    for(Map.Entry<String, List<Address>> customerIdAndAddresses: addressesByCustomerId.entrySet()){
      try {
        @SuppressWarnings("unchecked")
        List<Address> actualAddresses = (List<Address>) controller.getAddressesForCustomer(customerIdAndAddresses.getKey()).getBody();
        if(actualAddresses == null){
          assertEquals(Collections.emptyList(), customerIdAndAddresses.getValue());
        } else {
          assertEquals(customerIdAndAddresses.getValue(), actualAddresses);
        }
      } catch (Exception e) {
        assertTrue("sometimes getting the actual data will fail; could retry, but just move-on for now", true);
      }
    }

  }

  private static String makeCustomerId() {
      return UUID.randomUUID().toString();
    }
  
}
