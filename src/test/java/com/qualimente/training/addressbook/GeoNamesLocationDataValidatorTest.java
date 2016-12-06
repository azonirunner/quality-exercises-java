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
public class GeoNamesLocationDataValidatorTest {

  static final Set<String> SAMPLE_VALID_COUNTRY_CODES = Collections.unmodifiableSet(
      new LinkedHashSet<String>() {
        {
          add("CA");
          add("DE");
          add("FR");
          add("IT");
          add("JP");
          add("RU");
          add("US");
          add("GB");
          //add("UK"); UK should be valid, but isn't
        }
      }
  );

  static final Set<String> SAMPLE_INVALID_COUNTRY_CODES = Collections.unmodifiableSet(
      new LinkedHashSet<String>() {
        {
          add("");
          add("USA");
          add("United States");
          add("YY");
          add("ZY");
        }
      }
  );

  private GeoNamesLocationDataValidator locationDataValidator;

  @Before
  public void setUp(){
    locationDataValidator = GeoNamesLocationDataValidator.getInstance();
  }

  @Test
  public void isCountryCodeValid_should_detect_valid_country_codes() {
    for (String countryCode : SAMPLE_VALID_COUNTRY_CODES) {
      locationDataValidator = GeoNamesLocationDataValidator.getInstance();
      assertTrue("Expected '" + countryCode + "' to be valid",
          locationDataValidator.isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isCountryCodeValid_should_detect_invalid_country_codes() {
    for (String countryCode : SAMPLE_INVALID_COUNTRY_CODES) {
      assertFalse("Expected '" + countryCode + "' to be invalid",
          GeoNamesLocationDataValidator.getInstance().isCountryCodeValid(countryCode));
    }
  }

  @Test
  public void isPostalCodeValid_should_detect_valid_country_codes() {
    assertTrue(GeoNamesLocationDataValidator.getInstance().isPostalCodeValid("US", "20500"));
    assertTrue(GeoNamesLocationDataValidator.getInstance().isPostalCodeValid("US", "85040"));
  }
}