package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Coordinates type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Coordinates", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byTrip", fields = {"tripID"})
public final class Coordinates implements Model {
  public static final QueryField ID = field("Coordinates", "id");
  public static final QueryField COORD = field("Coordinates", "coord");
  public static final QueryField TIMESTAMP = field("Coordinates", "timestamp");
  public static final QueryField TRIP_ID = field("Coordinates", "tripID");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Coordinate", isRequired = true) Coordinate coord;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime timestamp;
  private final @ModelField(targetType="ID", isRequired = true) String tripID;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public Coordinate getCoord() {
      return coord;
  }
  
  public Temporal.DateTime getTimestamp() {
      return timestamp;
  }
  
  public String getTripId() {
      return tripID;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Coordinates(String id, Coordinate coord, Temporal.DateTime timestamp, String tripID) {
    this.id = id;
    this.coord = coord;
    this.timestamp = timestamp;
    this.tripID = tripID;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Coordinates coordinates = (Coordinates) obj;
      return ObjectsCompat.equals(getId(), coordinates.getId()) &&
              ObjectsCompat.equals(getCoord(), coordinates.getCoord()) &&
              ObjectsCompat.equals(getTimestamp(), coordinates.getTimestamp()) &&
              ObjectsCompat.equals(getTripId(), coordinates.getTripId()) &&
              ObjectsCompat.equals(getCreatedAt(), coordinates.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), coordinates.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getCoord())
      .append(getTimestamp())
      .append(getTripId())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Coordinates {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("coord=" + String.valueOf(getCoord()) + ", ")
      .append("timestamp=" + String.valueOf(getTimestamp()) + ", ")
      .append("tripID=" + String.valueOf(getTripId()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static CoordStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Coordinates justId(String id) {
    return new Coordinates(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      coord,
      timestamp,
      tripID);
  }
  public interface CoordStep {
    TimestampStep coord(Coordinate coord);
  }
  

  public interface TimestampStep {
    TripIdStep timestamp(Temporal.DateTime timestamp);
  }
  

  public interface TripIdStep {
    BuildStep tripId(String tripId);
  }
  

  public interface BuildStep {
    Coordinates build();
    BuildStep id(String id);
  }
  

  public static class Builder implements CoordStep, TimestampStep, TripIdStep, BuildStep {
    private String id;
    private Coordinate coord;
    private Temporal.DateTime timestamp;
    private String tripID;
    @Override
     public Coordinates build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Coordinates(
          id,
          coord,
          timestamp,
          tripID);
    }
    
    @Override
     public TimestampStep coord(Coordinate coord) {
        Objects.requireNonNull(coord);
        this.coord = coord;
        return this;
    }
    
    @Override
     public TripIdStep timestamp(Temporal.DateTime timestamp) {
        Objects.requireNonNull(timestamp);
        this.timestamp = timestamp;
        return this;
    }
    
    @Override
     public BuildStep tripId(String tripId) {
        Objects.requireNonNull(tripId);
        this.tripID = tripId;
        return this;
    }
    
    /** 
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, Coordinate coord, Temporal.DateTime timestamp, String tripId) {
      super.id(id);
      super.coord(coord)
        .timestamp(timestamp)
        .tripId(tripId);
    }
    
    @Override
     public CopyOfBuilder coord(Coordinate coord) {
      return (CopyOfBuilder) super.coord(coord);
    }
    
    @Override
     public CopyOfBuilder timestamp(Temporal.DateTime timestamp) {
      return (CopyOfBuilder) super.timestamp(timestamp);
    }
    
    @Override
     public CopyOfBuilder tripId(String tripId) {
      return (CopyOfBuilder) super.tripId(tripId);
    }
  }
  
}
