package com.qualimente.training.addressbook;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * LocationDataValidator provides functions that are useful for validating the correctness of various location-related facts.
 * <p/>
 * Note: LocationDataValidator is 'troubled' class with a number of problems included to support exercises around refactoring code.
 */
@Component
public class GeoNamesLocationDataValidator implements LocationDataValidator {

  private static final Logger log = LoggerFactory.getLogger(GeoNamesLocationDataValidator.class);

  private static final GeoNamesLocationDataValidator INSTANCE = new GeoNamesLocationDataValidator();

  private static final Map<String, Set<String>> postalCodesByCountryCode = makePostalCodesByCountryCode();

  private static HashMap<String, Set<String>> makePostalCodesByCountryCode() {
    try (BufferedReader reader = new BufferedReader
        (new InputStreamReader(new GZIPInputStream(GeoNamesLocationDataValidator.class.getResourceAsStream("/location-data.tsv.gz")), "UTF-8"))) {

      HashMap<String, Set<String>> postalCodesByCountryCode = new HashMap<>();

      String line;
      while ((line = reader.readLine()) != null) {
        String[] strings = line.split("\t");
        Set<String> postalCodesForCountry = postalCodesByCountryCode.getOrDefault(strings[0], new LinkedHashSet<>());
        postalCodesForCountry.add(strings[1]);
        postalCodesByCountryCode.put(strings[0], postalCodesForCountry);
      }

      return postalCodesByCountryCode;
    } catch (IOException e) {
      log.error("Encountered error while generating indices of postal and country codes", e);
      throw new RuntimeException(e);
    }
  }


  //hide constructor as part of Singleton implementation
  private GeoNamesLocationDataValidator() {
  }

  public static GeoNamesLocationDataValidator getInstance() {
    return INSTANCE;
  }

  public boolean isCountryCodeValid(String countryCode) {
    return postalCodesByCountryCode.containsKey(countryCode);
  }

  public boolean isPostalCodeValid(String countryCode, String postalCode) {
    return isCountryCodeValid(countryCode) && postalCodesByCountryCode.get(countryCode).contains(postalCode);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .toString();
  }

}

