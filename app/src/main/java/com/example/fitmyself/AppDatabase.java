package com.example.fitmyself;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

@Database(entities = {WorkoutDay.class, WorkoutRecord.class, UserSplit.class, UserExercise.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract WorkoutDao workoutDao();
    public abstract CustomDao customDao();
    public abstract RecordDao recordDao();
    // 单例模式：确保全App只有一个数据库实例，防止冲突
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "fit_myself_db")
                            .allowMainThreadQueries() // 为了简单，允许在主线程读写（正式开发建议用异步）
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}