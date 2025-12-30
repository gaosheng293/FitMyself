package com.example.fitmyself;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_exercises")
public class UserExercise {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;     // 比如 "哈克深蹲"
    public String bodyPart; // 比如 "腿"

    public UserExercise(String name, String bodyPart) {
        this.name = name;
        this.bodyPart = bodyPart;
    }
}