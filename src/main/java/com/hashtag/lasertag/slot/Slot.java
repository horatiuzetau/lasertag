package com.hashtag.lasertag.slot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hashtag.lasertag.activity.Activity;
import com.hashtag.lasertag.client.Client;
import com.hashtag.lasertag.schedule.Schedule;
import com.hashtag.lasertag.slot.enums.SlotStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "slots")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Slot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  Instant createdAt = Instant.now();

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  Instant updatedAt = Instant.now();

  @Column(nullable = false)
  LocalDate date;

  @Column(name = "start_time", nullable = false)
  LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  LocalTime endTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  SlotStatus status;

  @Column(name = "booked_spots", nullable = false)
  @Positive(message = "Booked spots must be greater than 0")
  int bookedSpots;

  @Column(name = "price")
  Double price;

  @ManyToOne
  @JoinColumn(name = "client_id", foreignKey = @ForeignKey(name = "fk_slots_to_clients"))
  Client client;

  @ManyToOne
  @JoinColumn(name = "schedule_id", foreignKey = @ForeignKey(name = "fk_slots_to_schedules"))
  Schedule schedule;

  @ManyToOne
  @JoinColumn(name = "activity_id", foreignKey = @ForeignKey(name = "fk_slots_to_services"))
  Activity activity;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bundle_id", foreignKey = @ForeignKey(name = "fk_slots_to_bundle_slots"))
  Slot bundleSlot;

  // do i need cascade and all that?
  @OneToMany(mappedBy = "bundleSlot", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  List<Slot> bundledSlots = new ArrayList<>();

  public static Slot create(LocalDate date, LocalTime startTime, int bookedSpots, SlotStatus status,
      Activity activity, Schedule schedule, Client client, Slot bundleSlot) {
    Slot slot = new Slot();
    slot.setDate(date);
    slot.setStartTime(startTime);
    slot.setEndTime(startTime.plusMinutes(activity.getDuration()));
    slot.setBookedSpots(status == SlotStatus.BLOCKED || !activity.isShareable()
        ? activity.getCapacity() : bookedSpots);
    slot.setStatus(status);

    slot.setPrice(activity.getPrice());
    slot.setBundleSlot(bundleSlot);
    slot.setClient(client);
    slot.setSchedule(schedule);
    slot.setActivity(activity);
    return slot;
  }


}