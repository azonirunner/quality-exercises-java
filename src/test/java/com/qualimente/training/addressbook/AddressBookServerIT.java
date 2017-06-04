package com.qualimente.training.addressbook;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AddressBookServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddressBookServerIT {

  @Test
  public void contextLoads() {
  }

}
