package com.qualimente.training.addressbook;

import org.junit.Test;

import java.io.BufferedReader;

import static org.mockito.Mockito.*;

public class LocationDataLoaderTest {
  @Test
  public void makePostalCodesByCountryCode_should_close_its_resources() throws Exception{
    BufferedReader bufferedReader = mock(BufferedReader.class);
    when(bufferedReader.readLine())
        .thenReturn(null);

    LocationDataLoader locationDataLoader = spy(LocationDataLoader.class);
    when(locationDataLoader.getReader())
        .thenReturn(bufferedReader);

    locationDataLoader.makePostalCodesByCountryCode();

    verify(bufferedReader).close();
  }
}
