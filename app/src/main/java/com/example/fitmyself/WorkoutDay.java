package com.example.fitmyself;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

// 1. 告诉 Room 这是一张表，表名叫 "workout_schedule"
@Entity(tableName = "workout_schedule")
public class WorkoutDay {

    // 每一行得有个身份证号，自动生成
    @PrimaryKey(autoGenerate = true)
    public int id;

    // 第几天 (0, 1, 2...)
    public int dayIndex;

    // 这一天练哪个部位 (例如 "胸")
    public String bodyPart;

    // 这一天选了哪些动作 (例如 ["卧推", "夹胸"])
    // 数据库默认存不了 List，我们需要用下面的“转换器”把它变成字符串存进去
    @TypeConverters(Converters.class)
    public List<String> exerciseList;

    // 构造函数
    public WorkoutDay(int dayIndex, String bodyPart, List<String> exerciseList) {
        this.dayIndex = dayIndex;
        this.bodyPart = bodyPart;
        this.exerciseList = exerciseList;
    }
}

// --- 下面是黑科技：转换器 ---
// 把 List<String> 变成 json 字符串存入数据库，取出时再变回 List
// 如果你的项目报错找不到 Gson，你需要去 build.gradle 加一行: implementation 'com.google.code.gson:gson:2.10.1'
// 或者我们用简单的逗号分隔法来实现（为了不让你去引新库，我写个简单的逗号分隔版）

class Converters {
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) return "";
        // 把列表变成 "卧推,夹胸,飞鸟" 这样的字符串
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s).append(",");
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null || value.isEmpty()) return Collections.emptyList();
        // 把 "卧推,夹胸,飞鸟" 切割回列表
        String[] items = value.split(",");
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        for (String item : items) {
            if (!item.isEmpty()) list.add(item);
        }
        return list;
    }
}