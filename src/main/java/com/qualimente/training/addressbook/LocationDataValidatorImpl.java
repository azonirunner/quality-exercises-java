package com.qualimente.training.addressbook;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * LocationDataValidator provides functions that are useful for validating the correctness of various location-related facts.
 *
 * Note: LocationDataValidator is 'troubled' class with a number of problems included to support exercises around refactoring code.
 */
@Component
public class LocationDataValidatorImpl implements LocationDataValidator {

  private static final Logger log = LoggerFactory.getLogger(LocationDataValidatorImpl.class);

  private static final LocationDataValidatorImpl INSTANCE = new LocationDataValidatorImpl();

  private static final Map<String, Set<String>> postalCodesByCountryCode =
      new LocationDataLoader().makePostalCodesByCountryCode();

  //hide constructor as part of Singleton implementation
  private LocationDataValidatorImpl() {
  }

  public static LocationDataValidator getInstance() {
    return INSTANCE;
  }

  public boolean isCountryCodeValid(String countryCode){
    return postalCodesByCountryCode.containsKey(countryCode);
  }

  public boolean isPostalCodeValid(String countryCode, String postalCode){
    return isCountryCodeValid(countryCode) && postalCodesByCountryCode.get(countryCode).contains(postalCode);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .toString();
  }

  public static void main(String[] args) {
    LocationDataValidator validator = LocationDataValidatorImpl.getInstance();
    System.out.println("LocationDataValidator: " + validator);

    for(String countryCode : new String [] {"CA", "DE", "FR", "IT", "JP", "RU", "US", "UK" }){
      System.out.println(countryCode + " is valid: " + validator.isCountryCodeValid(countryCode));
    }

    String countryCode = "US";
    String postalCode = "20500";

    System.out.println(postalCode + " is valid for (" + countryCode + "): " + validator.isPostalCodeValid(countryCode, postalCode));
  }
}

