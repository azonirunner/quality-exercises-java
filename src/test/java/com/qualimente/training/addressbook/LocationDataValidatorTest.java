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
public class LocationDataValidatorTest {

  static final Set<String> SAMPLE_VALID_COUNTRY_CODES = Collections.unmodifiableSet(
      new LinkedHashSet<String>() {
        {
          add("US");
          add("GB");
          add("FR");
          add("ES");
          add("IT");
          add("RU");
          add("JP");
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
    locationDataValidator = LocationDataValidator.getInstance();
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
}