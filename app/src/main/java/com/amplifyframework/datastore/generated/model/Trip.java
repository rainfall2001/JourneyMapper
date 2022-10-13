package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;
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

/** This is an auto generated class representing the Trip type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Trips", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Trip implements Model {
  public static final QueryField ID = field("Trip", "id");
  public static final QueryField START_LOCATION = field("Trip", "startLocation");
  public static final QueryField END_LOCATION = field("Trip", "endLocation");
  public static final QueryField TRAVEL_METHOD = field("Trip", "travelMethod");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String startLocation;
  private final @ModelField(targetType="String") String endLocation;
  private final @ModelField(targetType="TravelMethod") TravelMethod travelMethod;
  private final @ModelField(targetType="Coordinates") @HasMany(associatedWith = "tripID", type = Coordinates.class) List<Coordinates> Breadcrumbs = null;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getStartLocation() {
      return startLocation;
  }
  
  public String getEndLocation() {
      return endLocation;
  }
  
  public TravelMethod getTravelMethod() {
      return travelMethod;
  }
  
  public List<Coordinates> getBreadcrumbs() {
      return Breadcrumbs;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Trip(String id, String startLocation, String endLocation, TravelMethod travelMethod) {
    this.id = id;
    this.startLocation = startLocation;
    this.endLocation = endLocation;
    this.travelMethod = travelMethod;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Trip trip = (Trip) obj;
      return ObjectsCompat.equals(getId(), trip.getId()) &&
              ObjectsCompat.equals(getStartLocation(), trip.getStartLocation()) &&
              ObjectsCompat.equals(getEndLocation(), trip.getEndLocation()) &&
              ObjectsCompat.equals(getTravelMethod(), trip.getTravelMethod()) &&
              ObjectsCompat.equals(getCreatedAt(), trip.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), trip.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getStartLocation())
      .append(getEndLocation())
      .append(getTravelMethod())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Trip {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("startLocation=" + String.valueOf(getStartLocation()) + ", ")
      .append("endLocation=" + String.valueOf(getEndLocation()) + ", ")
      .append("travelMethod=" + String.valueOf(getTravelMethod()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
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
  public static Trip justId(String id) {
    return new Trip(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      startLocation,
      endLocation,
      travelMethod);
  }
  public interface BuildStep {
    Trip build();
    BuildStep id(String id);
    BuildStep startLocation(String startLocation);
    BuildStep endLocation(String endLocation);
    BuildStep travelMethod(TravelMethod travelMethod);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String startLocation;
    private String endLocation;
    private TravelMethod travelMethod;
    @Override
     public Trip build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Trip(
          id,
          startLocation,
          endLocation,
          travelMethod);
    }
    
    @Override
     public BuildStep startLocation(String startLocation) {
        this.startLocation = startLocation;
        return this;
    }
    
    @Override
     public BuildStep endLocation(String endLocation) {
        this.endLocation = endLocation;
        return this;
    }
    
    @Override
     public BuildStep travelMethod(TravelMethod travelMethod) {
        this.travelMethod = travelMethod;
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
    private CopyOfBuilder(String id, String startLocation, String endLocation, TravelMethod travelMethod) {
      super.id(id);
      super.startLocation(startLocation)
        .endLocation(endLocation)
        .travelMethod(travelMethod);
    }
    
    @Override
     public CopyOfBuilder startLocation(String startLocation) {
      return (CopyOfBuilder) super.startLocation(startLocation);
    }
    
    @Override
     public CopyOfBuilder endLocation(String endLocation) {
      return (CopyOfBuilder) super.endLocation(endLocation);
    }
    
    @Override
     public CopyOfBuilder travelMethod(TravelMethod travelMethod) {
      return (CopyOfBuilder) super.travelMethod(travelMethod);
    }
  }
  
}
