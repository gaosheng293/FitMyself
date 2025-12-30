package com.example.fitmyself;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CustomDao {
    // --- 分化相关 ---
    @Insert
    void insertSplit(UserSplit split);

    @Query("SELECT * FROM user_splits")
    List<UserSplit> getAllUserSplits();

    // --- 动作相关 ---
    @Insert
    void insertExercise(UserExercise exercise);

    @Query("SELECT * FROM user_exercises WHERE bodyPart = :part")
    List<UserExercise> getUserExercisesByPart(String part);

    // 【新增】根据对象删除自定义分化
    @Delete
    void deleteSplit(UserSplit split);

    // 【新增】根据名称删除自定义动作 (简单起见用名字删，或者你也可以用对象删)
    @Query("DELETE FROM user_exercises WHERE name = :name AND bodyPart = :bodyPart")
    void deleteExerciseByName(String name, String bodyPart);

    // 【新增】根据名字查询分化 (用于判断是否为自定义)
    @Query("SELECT * FROM user_splits WHERE name = :name LIMIT 1")
    UserSplit getSplitByName(String name);

    // 【新增】更新分化的部位列表
    @Query("UPDATE user_splits SET bodyParts = :newParts WHERE name = :name")
    void updateSplitParts(String name, String newParts);



}