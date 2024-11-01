package com.hashtag.lasertag.activity;

import com.hashtag.lasertag.activity.Activity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Optional<Activity> findByIdAndDeletedIsFalse(Long id);

    List<Activity> findAllByDeletedIsFalse();
    @Query("""
        SELECT a
        FROM Activity a
        WHERE a.deleted = false
            AND a.active = true
        """)
    List<Activity> findAllActiveActivities();

    @Query("""
        SELECT aib.bundle
        FROM Activity a
        JOIN ActivityInBundle aib ON a = aib.activity
        WHERE a.id = :id
        """)
    List<Activity> findAllBundlesContainingActivity(Long id);
}
