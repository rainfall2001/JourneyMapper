package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the Coordinate type in your schema. */
public final class Coordinate {
  private final Double Latitude;
  private final Double Longitude;
  public Double getLatitude() {
      return Latitude;
  }
  
  public Double getLongitude() {
      return Longitude;
  }
  
  public Coordinate(Double Latitude, Double Longitude) {
    this.Latitude = Latitude;
    this.Longitude = Longitude;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Coordinate coordinate = (Coordinate) obj;
      return ObjectsCompat.equals(getLatitude(), coordinate.getLatitude()) &&
              ObjectsCompat.equals(getLongitude(), coordinate.getLongitude());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getLatitude())
      .append(getLongitude())
      .toString()
      .hashCode();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(Latitude,
      Longitude);
  }
  public interface BuildStep {
    Coordinate build();
    BuildStep latitude(Double latitude);
    BuildStep longitude(Double longitude);
  }
  

  public static class Builder implements BuildStep {
    private Double Latitude;
    private Double Longitude;
    @Override
     public Coordinate build() {
        
        return new Coordinate(
          Latitude,
          Longitude);
    }
    
    @Override
     public BuildStep latitude(Double latitude) {
        this.Latitude = latitude;
        return this;
    }
    
    @Override
     public BuildStep longitude(Double longitude) {
        this.Longitude = longitude;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(Double latitude, Double longitude) {
      super.latitude(latitude)
        .longitude(longitude);
    }
    
    @Override
     public CopyOfBuilder latitude(Double latitude) {
      return (CopyOfBuilder) super.latitude(latitude);
    }
    
    @Override
     public CopyOfBuilder longitude(Double longitude) {
      return (CopyOfBuilder) super.longitude(longitude);
    }
  }
  
}
