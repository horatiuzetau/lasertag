package com.hashtag.lasertag.slot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

  @Query("""
      SELECT SUM(s.bookedSpots)
      FROM Slot s
      WHERE (:id is null OR s.id <> :id)
          AND s.date = :date
          AND s.status IN ('BOOKED', 'CONFIRMED', 'BLOCKED')
          AND s.activity.id = :activityId
          AND ((:startTime > s.startTime AND :startTime < s.endTime OR :startTime = s.startTime)
            OR (:endTime > s.startTime AND :endTime < s.endTime))
      """)
  Optional<Integer> getSumOfBookedSpotsForOverlappingTime(
      Long id, LocalDate date, LocalTime startTime, LocalTime endTime,  Long activityId
  );

  @Query("""
      SELECT s
      FROM Slot s
      WHERE s.status IN ('BOOKED', 'CONFIRMED', 'BLOCKED')
          AND s.date BETWEEN :startDate AND :endDate
      """)
  List<Slot> findAllSlotsByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
