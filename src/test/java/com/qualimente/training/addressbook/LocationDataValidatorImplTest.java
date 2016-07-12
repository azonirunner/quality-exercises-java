package com.qualimente.training.addressbook;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Validate location data per:
 * <ol>
 *   <li>https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2</li>
 * </ol>
 */
public class LocationDataValidatorImplTest {

  static final Set<String> SAMPLE_VALID_COUNTRY_CODES = Collections.unmodifiableSet(
      new LinkedHashSet<String>() {
        {
          add("CA");
          add("DE");
          add("ES");
          add("GB");
          add("FR");
          add("IT");
          add("JP");
          add("RU");
          add("US");
        }
      });

  static final Set<String> SAMPLE_INVALID_COUNTRY_CODES = Collections.unmodifiableSet(
      new LinkedHashSet<String>() {
        {
          add("");
          add("United States");
          add("USA");
          add("AB");
          add("AH");
          add("RQ");
          add("RR");
          add("ZX");
          add("XY");
        }
      });

  static final Set<String> COUNTRY_CODES_CONSIDERED_INVALID_WEIRD_CASES = Collections.unmodifiableSet(
      new LinkedHashSet<String>() {
        {
          add("UK"); //should be valid for United Kingdom
          add("CN");
        }
      });

  private LocationDataValidator locationDataValidator;

  @Before
  public void setUp(){
    locationDataValidator = LocationDataValidatorImpl.getInstance();
  }

  @Test
  public void isCountryCodeValid_should_detect_valid_countryCodes() {
    for(String countryCode : SAMPLE_VALID_COUNTRY_CODES){
      assertTrue("expected " + countryCode + " to be valid",
          locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isCountryCodeValid_should_detect_invalid_countryCodes() {
    for(String countryCode : SAMPLE_INVALID_COUNTRY_CODES){
      assertFalse("expected " + countryCode + " to be invalid",
          locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void document_existing_country_code_handling_though_possibly_incorrect() {
    for(String countryCode : COUNTRY_CODES_CONSIDERED_INVALID_WEIRD_CASES){
      assertFalse("expected " + countryCode + " to be invalid",
          locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isPostalCodeValid_should_detect_valid_postal_codes(){
    assertTrue(locationDataValidator.isPostalCodeValid("CA", "M5V" /*+ " 2T6"*/));

    assertTrue(locationDataValidator.isPostalCodeValid("US", "10118"));
    assertTrue(locationDataValidator.isPostalCodeValid("US", "20500"));
    assertTrue(locationDataValidator.isPostalCodeValid("US", "85042"));
    assertTrue(locationDataValidator.isPostalCodeValid("US", "94065"));
  }

  @Test
  public void isPostalCodeValid_should_detect_invalid_postal_codes(){
    assertFalse(locationDataValidator.isPostalCodeValid("CA", "A00"));
    assertFalse(locationDataValidator.isPostalCodeValid("CA", "A0A 042"));

    assertFalse(locationDataValidator.isPostalCodeValid("US", "42"));
    assertFalse(locationDataValidator.isPostalCodeValid("US", "8504242"));
    assertFalse(locationDataValidator.isPostalCodeValid("US", "85424"));
  }
}