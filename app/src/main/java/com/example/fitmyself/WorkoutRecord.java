package com.example.fitmyself;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "training_records")
public class WorkoutRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String date;         // 日期，如 "2023-12-30"
    public long timestamp;      // 具体时间戳，用于排序
    public String bodyPart;     // 部位，如 "胸"
    public String exerciseName; // 动作，如 "卧推"
    public String weight;       // 重量
    public String reps;         // 次数
    public long restTimeSeconds;// 这一组后的休息时间

    public WorkoutRecord(String date, long timestamp, String bodyPart, String exerciseName, String weight, String reps) {
        this.date = date;
        this.timestamp = timestamp;
        this.bodyPart = bodyPart;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
    }
}