package com.hashtag.lasertag.activity;

import com.hashtag.lasertag.activity.dtos.ActivityPatchRequest;
import com.hashtag.lasertag.activity.dtos.ActivityRequest;
import com.hashtag.lasertag.activity.dtos.BundleActivityDto;
import com.hashtag.lasertag.shared.exceptions.BadRequestException;
import com.hashtag.lasertag.shared.exceptions.ErrorMessages;
import com.hashtag.lasertag.shared.exceptions.InvalidOperationException;
import com.hashtag.lasertag.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ActivityService {

  final ActivityRepository activityRepository;

  public List<Activity> findAllActiveActivities() {
    return activityRepository.findAllActiveActivities();
  }

  public List<Activity> findAllActivities() {
    return activityRepository.findAllByDeletedIsFalse();
  }

  public Activity findActivityById(Long id) {
    return activityRepository.findByIdAndDeletedIsFalse(id)
        .orElseThrow(() ->
            new ResourceNotFoundException(Activity.class, id, ErrorMessages.ACTIVITY_NOT_FOUND)
        );
  }

  @Transactional
  public Activity createActivity(ActivityRequest activityRequest) {
    Activity activity = activityRequest.toActivity();

    if (activity.isBundle()) {
      List<ActivityInBundle> bundledActivities = activityRequest.getBundledActivities()
          .stream()
          .map(bundledActivityDto -> createBundledActivity(bundledActivityDto, activity))
          .toList();

      validateIfBundledActivitiesCanFitInBundleDuration(bundledActivities, activity);

      activity.setShareable(false);
      activity.setBundledActivities(bundledActivities);
    }

    return activityRepository.save(activity);
  }

  @Transactional
  public Activity updateActivity(ActivityRequest request) {
    Activity activity = findActivityById(request.getId());

    activity.setName(request.getName());
    activity.setActive(request.isActive());
    activity.setDuration(request.getDuration());
    activity.setRecoveryTime(request.getRecoveryTime());
    activity.setCapacity(request.getCapacity());
    activity.setType(request.getType());
    activity.setShareable(request.isShareable());
    activity.setPrice(request.getPrice());

    // Create a map to easily access ActivityInBundle by activity key (for removal)
    Map<Activity, ActivityInBundle> newActivityInBundleMap = request.getBundledActivities()
        .stream()
        .map(newBundledActivityDto -> createBundledActivity(newBundledActivityDto, activity))
        .collect(Collectors.toMap(
            ActivityInBundle::getActivity,
            bundledActivity -> bundledActivity
        ));

    // Validate if there is enough time for all activities included
    validateIfBundledActivitiesCanFitInBundleDuration(
        newActivityInBundleMap.values().stream().toList(),
        activity
    );

    // Remove if no longer part of the bundle
    activity.getBundledActivities()
        .removeIf(o -> !newActivityInBundleMap.containsKey(o.getActivity()));

    // Update existing ones with correct sizes
    activity.getBundledActivities()
        .forEach(activityInBundle -> {
          activityInBundle.setSize(
              newActivityInBundleMap.get(activityInBundle.getActivity()).getSize()
          );
          newActivityInBundleMap.remove(activityInBundle.getActivity());
        });

    // Add new ones
    activity.getBundledActivities().addAll(newActivityInBundleMap.values());

    if (activity.isBundle() && activity.getBundledActivities().isEmpty()) {
      throw new InvalidOperationException(
          ErrorMessages.BUNDLE_SHOULD_NOT_REMAIN_WITHOUT_BUNDLED_ITEMS
      );
    }

    return activityRepository.save(activity);
  }

  @Transactional
  public void patchActivity(Long id, ActivityPatchRequest activityPatchRequest) {
    Activity activity = findActivityById(id);

    if (activityPatchRequest.getActive() != null) {
      activity.setActive(activityPatchRequest.getActive());

      // if making inactive, unlink from bundles
      if (!activity.isActive()) {
        unlinkActivityFromAllBundles(activity);
      }
    }

    if (activityPatchRequest.getBundledActivities() != null && activity.isBundle()) {
      // Create a map to easily access ActivityInBundle by activity key (for removal)
      Map<Activity, ActivityInBundle> newActivityInBundleMap = activityPatchRequest.getBundledActivities()
          .stream()
          .map(newBundledActivityDto -> createBundledActivity(newBundledActivityDto, activity))
          .collect(Collectors.toMap(
              ActivityInBundle::getActivity,
              bundledActivity -> bundledActivity
          ));

      // Validate if there is enough time for all activities included
      validateIfBundledActivitiesCanFitInBundleDuration(
          newActivityInBundleMap.values().stream().toList(),
          activity
      );

      // Remove if no longer part of the bundle
      activity.getBundledActivities()
          .removeIf(o -> !newActivityInBundleMap.containsKey(o.getActivity()));

      // Update existing ones with correct sizes
      activity.getBundledActivities()
          .forEach(activityInBundle -> {
            activityInBundle.setSize(
                newActivityInBundleMap.get(activityInBundle.getActivity()).getSize()
            );
            newActivityInBundleMap.remove(activityInBundle.getActivity());
          });

      // Add new ones
      activity.getBundledActivities().addAll(newActivityInBundleMap.values());

      if (activity.getBundledActivities().isEmpty()) {
        throw new InvalidOperationException(
            ErrorMessages.BUNDLE_SHOULD_NOT_REMAIN_WITHOUT_BUNDLED_ITEMS
        );
      }
    }
  }

  @Transactional
  public void safeDeleteActivity(Long id) {
    Activity activity = findActivityById(id);

    // Unlink activity from bundles
    unlinkActivityFromAllBundles(activity);

    // If bundle, remove all bundled activities
    if (activity.isBundle()) {
      activity.getBundledActivities().clear();
    }

    // Inactivate and delete activity
    activity.setActive(false);
    activity.setDeleted(true);
  }

  private void unlinkActivityFromAllBundles(Activity activity) {
    // Remove the activity from bundles
    activityRepository.findAllBundlesContainingActivity(activity.getId())
        .forEach(bundleActivity -> {
          bundleActivity
              .getBundledActivities()
              .removeIf(bundledActivity -> bundledActivity.getActivity().equals(activity));

          activityRepository.save(bundleActivity);
        });
  }

  private ActivityInBundle createBundledActivity(BundleActivityDto newBundledActivityDto,
      Activity bundleActivity) {

    Activity newBundledActivity = findActivityById(newBundledActivityDto.getId());

    if (!newBundledActivity.isActive()) {
      throw new BadRequestException(
          String.format(ErrorMessages.ACTIVITY_NOT_ACTIVE, newBundledActivity.getName())
      );
    }

    return new ActivityInBundle(
        bundleActivity, newBundledActivity, newBundledActivityDto.getSize()
    );
  }

  private static void validateIfBundledActivitiesCanFitInBundleDuration(
      List<ActivityInBundle> bundledActivities, Activity activity) {
    // verify if the duration of bundle is enough
    int bundledActivitiesDuration = bundledActivities.stream()
        .map(activityInBundle ->
            activityInBundle.getSize() * activityInBundle.getActivity().getDuration())
        .reduce(0, Integer::sum);

    if (bundledActivitiesDuration > activity.getDuration()) {
      throw new InvalidOperationException(ErrorMessages.TOO_FEW_TIME_FOR_BUNDLED_ACTIVITIES);
    }
  }
}
