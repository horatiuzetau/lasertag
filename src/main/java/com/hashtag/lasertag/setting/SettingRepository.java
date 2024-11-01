package com.hashtag.lasertag.setting;

import com.hashtag.lasertag.setting.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    Optional<Setting> findByName(String name);

}
