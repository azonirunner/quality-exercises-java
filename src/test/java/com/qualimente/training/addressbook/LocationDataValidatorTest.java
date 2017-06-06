package com.qualimente.training.addressbook;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationDataValidatorTest {

  @Test
  public void isCountryCode_should_be_true_for_valid_countries() {
    for (String countryCode : new String[]{"CA", "DE", "FR", "IT", "JP", "RU", "US", "GB"}) {
      assertTrue("expected " + countryCode + " to be valid",
          LocationDataValidator.getInstance().isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isCountryCode_should_be_false_for_invalid_countries() {
    for (String countryCode : new String[]{"UK"}) {
      assertFalse("expected " + countryCode + " to be invalid",
          LocationDataValidator.getInstance().isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isPostalCode_should_be_true_for_valid_postal_codes(){
    assertTrue(LocationDataValidator.getInstance().isPostalCodeValid
        ("US", "20500"));
  }


}