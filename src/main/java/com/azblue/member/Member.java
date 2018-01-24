package com.azblue.member;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Member is the central entity of the member domain.
 */
public class Member {

    private final String id;
    private final String firstName;
    private final String lastName;

    public Member(@JsonProperty("id") String id,
                  @JsonProperty("firstName") String firstName,
                  @JsonProperty("lastName") String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() { return id; }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName(){
      return this.lastName;
  }

  @JsonProperty("fullName")
  public String getFullName(){
    return this.getFirstName() + " " + this.getLastName();
  }
}
