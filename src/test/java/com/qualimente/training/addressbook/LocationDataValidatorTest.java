package com.qualimente.training.addressbook;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationDataValidatorTest {

  private LocationDataValidator locationDataValidator;

  @Before
  public void before(){
    locationDataValidator = LocationDataValidator.getInstance();
  }

  @Test
  public void isCountryCode_should_be_true_for_valid_countries() {
    for (String countryCode : new String[]{"CA", "DE", "FR", "IT", "JP", "RU", "US", "GB", "CH"}) {
      assertTrue("expected " + countryCode + " to be valid",
          locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void weird_situation_where_isCountryCode_should_be_true_but_is_not() {
    String country_code_china = "CN";
    for (String countryCode : new String[]{country_code_china}) {
      assertFalse("expected " + countryCode + " to be invalid",
          locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isCountryCode_should_be_false_for_invalid_countries() {
    for (String countryCode : new String[]{"UK"}) {
      assertFalse("expected " + countryCode + " to be invalid",
          locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isPostalCode_should_be_true_for_valid_postal_codes(){
    assertTrue(LocationDataValidator.getInstance().isPostalCodeValid
        ("US", "20500"));
  }


}