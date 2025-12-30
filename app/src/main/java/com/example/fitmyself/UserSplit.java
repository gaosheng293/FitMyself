package com.example.fitmyself;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_splits")
public class UserSplit {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;        // 比如 "六分化"
    public String description; // 比如 "阿诺德经典分化"
    public String bodyParts;   // 存成字符串，用逗号隔开，比如 "胸,背,腿,肩,二头,三头"

    public UserSplit(String name, String description, String bodyParts) {
        this.name = name;
        this.description = description;
        this.bodyParts = bodyParts;
    }
}