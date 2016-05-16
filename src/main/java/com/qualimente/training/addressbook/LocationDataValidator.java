package com.qualimente.training.addressbook;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * LocationDataValidator provides functions that are useful for validating the correctness of various location-related facts.
 */
public class LocationDataValidator {

  private static final LocationDataValidator INSTANCE = new LocationDataValidator();

  //hide constructor as part of Singleton implementation
  private LocationDataValidator() {
  }

  public static LocationDataValidator getInstance() {
    return INSTANCE;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .toString();
  }

  public static void main(String[] args) {
    System.out.println("LocationDataValidator: " + LocationDataValidator.getInstance());
  }
}

