package com.qualimente.training.addressbook;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationDataValidatorTest {

  private LocationDataValidator locationDataValidator;

  @Before
  public void before(){
    this.locationDataValidator = new LocationDataValidator();
  }

  @Test
  public void should_return_true_for_valid_country_codes() {
    for (String countryCode : new String[]{
        "CA", "DE", "FR", "IT", "JP", "RU", "US", "GB"
    }) {
      assertTrue(locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void should_return_false_for_valid_country_codes_that_are_not_for_some_reason() {
    for (String countryCode : new String[]{
        "CN"
    }) {
      assertFalse(locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isCountry_code_should_return_false_for_invalid_country_codes() {
    for (String countryCode : new String[]{"UK"}) {
      assertFalse(locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isPostalCode_retuns_true_for_valid_postal_codes(){
    locationDataValidator.isPostalCodeValid("US", "20500");
  }
  
}