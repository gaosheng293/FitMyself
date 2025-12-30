package com.example.fitmyself;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface RecordDao {
    @Insert
    long insert(WorkoutRecord record); // 返回新插入行的ID

    // 更新休息时间
    @Query("UPDATE training_records SET restTimeSeconds = :seconds WHERE id = :recordId")
    void updateRestTime(int recordId, long seconds);

    // 获取所有记录，按时间倒序
    @Query("SELECT * FROM training_records ORDER BY timestamp DESC")
    List<WorkoutRecord> getAllRecords();

    // 获取某一天的记录
    @Query("SELECT * FROM training_records WHERE date = :dateStr")
    List<WorkoutRecord> getRecordsByDate(String dateStr);
}