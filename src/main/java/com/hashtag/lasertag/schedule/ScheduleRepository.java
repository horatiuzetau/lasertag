package com.hashtag.lasertag.schedule;

import com.hashtag.lasertag.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByIdAndDeletedIsFalse(Long id);

    @Query("""
            SELECT s
            FROM Schedule s
            WHERE s.deleted IS false
                AND (:id IS NULL OR :id <> s.id)
                AND ((:startDate BETWEEN s.startDate AND s.endDate)
                    OR (:endDate BETWEEN s.startDate AND s.endDate))
            """)
    List<Schedule> findSchedulesInSamePeriod(Long id, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Schedule s WHERE s.deleted = false AND s.main = true")
    Optional<Schedule> findMainSchedule();

    @Query("""
            SELECT s
            FROM Schedule s
            WHERE s.deleted = false
                AND s.main = false
                AND s.active = true
                AND s.startDate <= :date
                AND s.endDate >= :date
            """)
    Optional<Schedule> findActiveNonMainScheduleByDate(LocalDate date);

    @Query("""
            SELECT s
            FROM Schedule s
            WHERE s.deleted = false
            ORDER BY s.main, s.startDate
            """)
    List<Schedule> findAllNonDeleted();

    @Query("""
                SELECT s
                FROM Schedule s
                WHERE s.deleted = false
                    AND s.active = true
                    AND s.startDate >= :date
                    AND s.endDate <= :date
            """)
    Optional<Schedule> findActiveScheduleByDate(LocalDate date);

    @Modifying
    @Query("UPDATE Schedule s SET s.main = false, s.active = false WHERE s.main = true")
    void resetMainSchedule();

}