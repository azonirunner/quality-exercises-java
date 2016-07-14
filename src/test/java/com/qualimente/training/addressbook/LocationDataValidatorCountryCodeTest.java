package com.qualimente.training.addressbook;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * LocationDataValidatorCountryCodeTest test's LocationDataValidatorImpl's validation of country codes.
 */
@RunWith(Parameterized.class)
public class LocationDataValidatorCountryCodeTest {

  @Parameterized.Parameters(name = "{index}: is {0} valid? {1}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        //code, valid?
        // -- valid --
        {"CA", true},
        {"DE", true},
        {"ES", true},
        {"GB", true},
        {"US", true},

        // -- invalid --
        {"nonsense", false},
        {"USA", false},

        // -- weird --
        {"CN", false}
    });
  }

  private String countryCode;
  private boolean expectValid;

  private LocationDataValidator locationDataValidator;

  @Before
  public void setUp(){
    locationDataValidator = LocationDataValidatorImpl.getInstance();
  }

  public LocationDataValidatorCountryCodeTest(String countryCode, boolean expectValid) {
    this.countryCode = countryCode;
    this.expectValid = expectValid;
  }

  @Test
  public void isCountryCodeValid_should_detect_valid_inputs() {
    assertEquals("Expected " + countryCode + " to be valid: " + expectValid,
        expectValid, locationDataValidator.isCountryCodeValid(countryCode));
  }

}
