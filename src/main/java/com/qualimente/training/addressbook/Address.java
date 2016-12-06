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
  private final String name;
  private final String line1;
  private final String line2;
  private final String city;
  private final String state;
  private final String postalCode;
  private final String country;

  public Address(@JsonProperty("id") String id,
                 @JsonProperty("name") String name,
                 @JsonProperty("line1") String line1,
                 @JsonProperty("line2") String line2,
                 @JsonProperty("city") String city,
                 @JsonProperty("postalCode") String postalCode,
                 @JsonProperty("state") String state,
                 @JsonProperty("country") String country) {
    this.id = id;
    this.name = name;
    this.line1 = line1;
    this.line2 = line2;
    this.city = city;
    this.postalCode = postalCode;
    this.state = state;
    this.country = country;
  }

  public String getId() { return this.id; }
  public String getName() { return this.name; }
  public String getLine1() { return this.line1; }
  public String getLine2() { return this.line2; }
  public String getCity() { return this.city; }
  public String getState() { return this.state; }
  public String getPostalCode() { return this.postalCode; }
  public String getCountry() { return this.country; }

  public Address copyWith(String id) {
    return new Address(id, name, getLine1(), getLine2(), getCity(), getPostalCode(), getState(), getCountry());
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(421, 23)
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
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("line1", this.line1)
        .append("line2", this.line1)
        .append(city, this.city)
        .append(postalCode, this.postalCode)
        .append(state, this.state)
        .append(country, this.country)
        .toString();
  }
}
