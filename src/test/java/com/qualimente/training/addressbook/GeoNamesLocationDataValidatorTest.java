package com.qualimente.training.addressbook;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeoNamesLocationDataValidatorTest {

  private GeoNamesLocationDataValidator geoNamesLocationDataValidator;

  @Before
  public void setUp(){
    geoNamesLocationDataValidator = new GeoNamesLocationDataValidator();
  }

  @Test
  public void isCountryCodeValid_should_be_true_for_valid_country_codes() {
    for (String countryCode : new String[]{
        "CA", "DE", "FR", "IT", "JP", "RU", "US"
    }) {
      assertTrue("Expected " + countryCode + " to be valid, but was not",
          geoNamesLocationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isCountryCodeValid_should_be_false_for_invalid_country_codes() {
    for (String countryCode : new String[]{
        null
        , ""
        , "UK"
        , "MARS"
        , "United States"
        , "USA"
    }) {
      assertFalse("Expected " + countryCode + " to be invalid, but was not",
          geoNamesLocationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isPostalCodeValid_should_be_true_when_valid(){
    String countryCode = "US";
    String postalCode = "20500";

    assertTrue("Expected ", geoNamesLocationDataValidator
        .isPostalCodeValid(countryCode, postalCode));
  }

}