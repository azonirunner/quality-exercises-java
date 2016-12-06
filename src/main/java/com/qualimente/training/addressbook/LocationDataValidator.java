package com.qualimente.training.addressbook;

/**
 * LocationDataValidator provides functions that are useful for validating the correctness of various location-related facts.
 */
public interface LocationDataValidator {

  boolean isCountryCodeValid(String countryCode);

  boolean isPostalCodeValid(String countryCode, String postalCode);

}

