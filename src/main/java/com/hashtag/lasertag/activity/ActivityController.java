package com.hashtag.lasertag.activity;

import com.hashtag.lasertag.activity.dtos.ActivityRequest;
import com.hashtag.lasertag.activity.dtos.ActivityPatchRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityController {

  final ActivityService activityService;

  /**
   * Get all active activities.
   *
   * @return ResponseEntity with the list of active activities
   */
  @GetMapping("/active")
  public ResponseEntity<List<Activity>> getAllActiveActivities() {
    List<Activity> activities = activityService.findAllActiveActivities();
    return ResponseEntity.ok(activities);
  }

  /**
   * Get all active activities.
   *
   * @return ResponseEntity with the list of active activities
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public ResponseEntity<List<Activity>> getAllActivities() {
    List<Activity> activities = activityService.findAllActivities();
    return ResponseEntity.ok(activities);
  }

  /**
   * Get an activity by its ID.
   *
   * @param id the ID of the activity
   * @return ResponseEntity with the found activity
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
    Activity activity = activityService.findActivityById(id);
    return ResponseEntity.ok(activity);
  }

  /**
   * Create a new activity.
   *
   * @param activityRequest the data for the new activity
   * @return ResponseEntity with the created activity
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<Activity> createActivity(
      @Valid @RequestBody ActivityRequest activityRequest) {
    Activity createdActivity = activityService.createActivity(activityRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdActivity);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping
  public ResponseEntity<Activity> updateActivity(
      @Valid @RequestBody ActivityRequest activityRequest) {
    Activity updateActivity = activityService.updateActivity(activityRequest);
    return ResponseEntity.status(HttpStatus.OK).body(updateActivity);
  }

  /**
   * Update an existing activity.
   *
   * @param id                   the ID of the activity to update
   * @param activityPatchRequest the patch object containing updates
   * @return ResponseEntity with no content
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PatchMapping("/{id}")
  public ResponseEntity<Void> patchActivity(@PathVariable Long id,
      @Valid @RequestBody ActivityPatchRequest activityPatchRequest) {
    activityService.patchActivity(id, activityPatchRequest);
    return ResponseEntity.noContent().build();
  }

  /**
   * Safely delete an activity by ID.
   *
   * @param id the ID of the activity to delete
   * @return ResponseEntity with no content
   */
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> safeDeleteActivity(@PathVariable Long id) {
    activityService.safeDeleteActivity(id);
    return ResponseEntity.noContent().build();
  }
}