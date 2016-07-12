package com.qualimente.training.addressbook;

/**
 * LocationDataValidator defines an interface for valiating various bits of location data.
 */
public interface LocationDataValidator {

  boolean isCountryCodeValid(String countryCode);

  boolean isPostalCodeValid(String countryCode, String postalCode);

}
