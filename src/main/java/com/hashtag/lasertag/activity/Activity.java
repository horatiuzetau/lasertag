package com.hashtag.lasertag.activity;

import com.hashtag.lasertag.activity.enums.ActivityType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "activities")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Activity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 100)
  String name;

  @Column(nullable = false)
  int duration;

  @Column(name = "recovery_time", nullable = false)
  int recoveryTime;

  @Column(nullable = false)
  int capacity;

  @Column(nullable = false)
  double price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  ActivityType type;

  @Column(nullable = false)
  boolean active = false;

  @Column(nullable = false)
  boolean deleted;

  @Column(nullable = false)
  boolean shareable;

  @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  List<ActivityInBundle> bundledActivities = new ArrayList<>();

  public boolean isBundle() {
    return this.type == ActivityType.BUNDLE;
  }
}
