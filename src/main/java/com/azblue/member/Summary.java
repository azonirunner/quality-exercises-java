package com.azblue.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Summary provides a summarized view of a Member.
 */
public class Summary {
  private final Member member;

  public Summary(@JsonProperty("member") Member member){
    this.member = member;
  }

  public Member getMember(){ return this.member; }
}
