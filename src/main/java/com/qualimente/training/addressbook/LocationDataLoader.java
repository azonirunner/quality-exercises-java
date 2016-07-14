package com.qualimente.training.addressbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

class LocationDataLoader {
  private static final Logger log = LoggerFactory.getLogger(LocationDataLoader.class);

  HashMap<String, Set<String>> makePostalCodesByCountryCode() {
    log.info("making postal and country codes");
    BufferedReader reader = null;
      try {
        reader = getReader();

        HashMap<String, Set<String>> postalCodesByCountryCode = new HashMap<>();

        String line;
        while((line = reader.readLine()) != null){
          String[] strings = line.split("\t");
          Set<String> postalCodesForCountry = postalCodesByCountryCode.getOrDefault(strings[0], new LinkedHashSet<>());
          postalCodesForCountry.add(strings[1]);
          postalCodesByCountryCode.put(strings[0], postalCodesForCountry);
        }

        return postalCodesByCountryCode;
      } catch (IOException e) {
        log.error("Encountered error while generating indices of postal and country codes", e);
        throw new RuntimeException(e);
      } finally {
        if(reader != null){
          try {
            reader.close();
          } catch (IOException e) {
            log.error("could not close location data reader", e);
          }
        }
      }
    }

  BufferedReader getReader() throws IOException {
    InputStream inputStream = new GZIPInputStream(LocationDataValidatorImpl.class.getResourceAsStream("/location-data.tsv.gz"));
    return new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
  }
}
