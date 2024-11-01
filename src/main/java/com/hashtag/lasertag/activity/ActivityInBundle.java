package com.hashtag.lasertag.activity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "activities_in_bundle")
@IdClass(ActivityInBundle.ActivityInBundleId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityInBundle {

  @Id
  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "bundle_id", foreignKey = @ForeignKey(name = "fk_activities_in_bundle_to_bundles"), nullable = false)
  Activity bundle;

  @Id
  @ManyToOne
  @JoinColumn(name = "activity_id", foreignKey = @ForeignKey(name = "fk_activities_in_bundle_to_activities"), nullable = false)
  Activity activity;

  @Min(1)
  @Column(nullable = false)
  int size;


  @NoArgsConstructor
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ActivityInBundleId implements Serializable {

    Long bundle;  // Corresponds to Bundle id
    Long activity;  // Corresponds to Activity id

  }
}