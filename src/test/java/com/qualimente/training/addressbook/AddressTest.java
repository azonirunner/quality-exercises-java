package com.qualimente.training.addressbook;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class AddressTest {

  private static final Random RANDOM = new Random();

  static Address makeAddress() {
    return new Address(null, "line 1", "line 2", "city", "postal code", "state", "country");
  }

  @Test
  public void constructor_stores_arguments() {

    String id = anyString();
    String line1 = anyString();
    String line2 = anyString();
    String city = anyString();
    String postalCode = anyString();
    String state = anyString();
    String country = anyString();

    Address address = new Address(id, line1, line2, city, postalCode, state, country);

    assertEquals(id, address.getId());
    assertEquals(line1, address.getLine1());
    assertEquals(line2, address.getLine2());
    assertEquals(city, address.getCity());
    assertEquals(postalCode, address.getPostalCode());
    assertEquals(state, address.getState());
    assertEquals(country, address.getCountry());
  }

  @Test
  public void constructor_permits_null_id(){
    new Address(null, anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
  }

  @Test
  public void copyWith_copies_instance_properly() {
    Address original = makeAddress();

    String id = anyString();
    Address copy = original.copyWith(id);

    assertEquals(original, copy);
  }

  @Test
  public void implement_equals_for_domain() {
    String id = anyString();
    String line1 = anyString();
    String line2 = anyString();
    String city = anyString();
    String postalCode = anyString();
    String state = anyString();
    String country = anyString();

    Address address1 = new Address(id, line1, line2, city, postalCode, state, country);
    Address address2 = new Address(id, line1, line2, city, postalCode, state, country);

    //noinspection ObjectEqualsNull
    assertFalse(address1.equals(null));

    //noinspection EqualsWithItself
    assertTrue(address1.equals(address1));

    assertTrue(address1.equals(address2));
    assertTrue(address2.equals(address1));

  }

  @Test
  public void implement_hashCode_for_domain() {
    String id = anyString();
    String line1 = anyString();
    String line2 = anyString();
    String city = anyString();
    String postalCode = anyString();
    String state = anyString();
    String country = anyString();

    Address address1 = new Address(id, line1, line2, city, postalCode, state, country);
    Address address2 = new Address(id, line1, line2, city, postalCode, state, country);

    assertEquals(address1.hashCode(), address2.hashCode());

    assertNotEquals(address1.hashCode(), new Address(null, anyString(), line2, city, postalCode, state, country));
    assertNotEquals(address1.hashCode(), new Address(null, line1, anyString(), city, postalCode, state, country));
    assertNotEquals(address1.hashCode(), new Address(null, line1, line2, anyString(), postalCode, state, country));
    assertNotEquals(address1.hashCode(), new Address(null, line1, line2, city, anyString(), state, country));
    assertNotEquals(address1.hashCode(), new Address(null, line1, line2, city, postalCode, anyString(), country));
    assertNotEquals(address1.hashCode(), new Address(null, line1, line2, city, postalCode, state, anyString()));
  }

  private String anyString() {
    return String.valueOf(RANDOM.nextFloat());
  }
}