package com.example.fitmyself;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WorkoutDao {
    // 存：把所有天数的计划一次性存进去
    @Insert
    void insertAll(List<WorkoutDay> days);

    // 取：按照天数顺序取出所有计划
    @Query("SELECT * FROM workout_schedule ORDER BY dayIndex ASC")
    List<WorkoutDay> getAll();

    // 删：清空旧计划（比如用户想重置计划时用）
    @Query("DELETE FROM workout_schedule")
    void deleteAll();
}