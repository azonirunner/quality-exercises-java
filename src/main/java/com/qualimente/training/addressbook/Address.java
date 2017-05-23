package com.qualimente.training.addressbook;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Address is the domain model for the address entity.
 */
public class Address {

  private final String id;
  private final String line1;
  private final String line2;
  private final String city;
  private final String state;
  private final String postalCode;
  private final String country;
  private String name;

  public Address(@JsonProperty("id") String id,
                 @JsonProperty("name") String name,
                 @JsonProperty("line1") String line1,
                 @JsonProperty("line2") String line2,
                 @JsonProperty("city") String city,
                 @JsonProperty("postalCode") String postalCode,
                 @JsonProperty("state") String state,
                 @JsonProperty("country") String country) {
    this.id = id;
    if(null == name){
      throw new IllegalArgumentException("Name is required, but was null");
    }
    this.name = name;
    this.line1 = line1;
    this.line2 = line2;
    this.city = city;
    this.postalCode = postalCode;
    this.state = state;
    this.country = country;
  }

  public String getId() { return this.id; }
  public String getName() { return name; }
  public String getLine1() { return this.line1; }
  public String getLine2() { return this.line2; }
  public String getCity() { return this.city; }
  public String getState() { return this.state; }
  public String getPostalCode() { return this.postalCode; }
  public String getCountry() { return this.country; }

  public Address copyWith(String id) {
    return new Builder().withId(id).withName(getName()).withLine1(getLine1()).withLine2(getLine2()).withCity(getCity()).withPostalCode(getPostalCode()).withState(getState()).withCountry(getCountry()).createAddress();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(421, 23)
        .append(name)
        .append(line1)
        .append(line2)
        .append(city)
        .append(postalCode)
        .append(state)
        .append(country)
        .toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) { return false; }
    if (obj == this) { return true; }
    if (obj.getClass() != getClass()) { return false; }

    Address rhs = (Address) obj;
    return new EqualsBuilder()
        .append(name, rhs.name)
        .append(line1, rhs.line1)
        .append(line2, rhs.line2)
        .append(city, rhs.city)
        .append(postalCode, rhs.postalCode)
        .append(state, rhs.state)
        .append(country, rhs.country)
        .isEquals();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public static class Builder {
    private String id;
    private String name;
    private String line1;
    private String line2;
    private String city;
    private String postalCode;
    private String state;
    private String country;

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withLine1(String line1) {
      this.line1 = line1;
      return this;
    }

    public Builder withLine2(String line2) {
      this.line2 = line2;
      return this;
    }

    public Builder withCity(String city) {
      this.city = city;
      return this;
    }

    public Builder withPostalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    public Builder withState(String state) {
      this.state = state;
      return this;
    }

    public Builder withCountry(String country) {
      this.country = country;
      return this;
    }

    public Address createAddress() {
      return new Address(id, name, line1, line2, city, postalCode, state, country);
    }
  }
}
