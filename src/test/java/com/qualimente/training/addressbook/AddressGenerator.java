package com.qualimente.training.addressbook;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.ArrayList;
import java.util.List;

/**
 * AddressGenerator is responsible for generating randomly-populated, valid Address objects.
 */
public class AddressGenerator extends Generator<Address> {

  private static final StringGenerator STRING_GENERATOR = new StringGenerator();

  private static final List<PostalCountryPair> VALID_POSTAL_COUNTRY_CODES =
      new ArrayList<PostalCountryPair>() {{
        add(new PostalCountryPair("M5V", "CA"  /*+ " 2T6"*/));

        add(new PostalCountryPair("10118", "US"));
        add(new PostalCountryPair("20500", "US"));
        add(new PostalCountryPair("85042", "US"));
        add(new PostalCountryPair("94065", "US"));
        add(new PostalCountryPair("85283", "US"));
      }};

  public AddressGenerator() {
    super(Address.class);
  }

  @Override
  public Address generate(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
    PostalCountryPair postalCountryPair = anyPostalCountryPair(sourceOfRandomness);
    return new Address(null,
        STRING_GENERATOR.generate(sourceOfRandomness, generationStatus),
        maybeAnyString(sourceOfRandomness, generationStatus),
        maybeAnyString(sourceOfRandomness, generationStatus),
        maybeAnyString(sourceOfRandomness, generationStatus),
        postalCountryPair.postalCode,
        maybeAnyString(sourceOfRandomness, generationStatus),
        postalCountryPair.countryCode
    );
  }

  private PostalCountryPair anyPostalCountryPair(SourceOfRandomness sourceOfRandomness) {
    int randomIndex = sourceOfRandomness.nextInt(VALID_POSTAL_COUNTRY_CODES.size());
    return VALID_POSTAL_COUNTRY_CODES.get(randomIndex);
  }

  private String maybeAnyString(SourceOfRandomness sourceOfRandomness, GenerationStatus generationStatus) {
    return STRING_GENERATOR.generate(sourceOfRandomness, generationStatus);
  }

  private static class PostalCountryPair {
    final String postalCode;
    final String countryCode;

    PostalCountryPair(String postalCode, String countryCode) {
      this.postalCode = postalCode;
      this.countryCode = countryCode;
    }
  }
}
