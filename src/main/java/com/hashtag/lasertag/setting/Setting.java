package com.hashtag.lasertag.setting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "settings")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Setting {

    @Id
    String name;
    String value;

}
