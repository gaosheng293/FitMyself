package com.example.fitmyself;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView rv = findViewById(R.id.rv_history);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // 1. 获取所有数据
        List<WorkoutRecord> allRecords = AppDatabase.getDatabase(this).recordDao().getAllRecords();

        // 2. 将数据按日期分组 (使用 LinkedHashMap 保持日期顺序)
        Map<String, List<WorkoutRecord>> groupedMap = new LinkedHashMap<>();

        for (WorkoutRecord record : allRecords) {
            String date = record.date;
            if (!groupedMap.containsKey(date)) {
                groupedMap.put(date, new ArrayList<>());
            }
            groupedMap.get(date).add(record);
        }

        // 3. 转换成 Adapter 需要的 HistoryGroup 列表
        List<HistoryGroup> groupList = new ArrayList<>();
        for (Map.Entry<String, List<WorkoutRecord>> entry : groupedMap.entrySet()) {
            groupList.add(new HistoryGroup(entry.getKey(), entry.getValue()));
        }

        // 4. 设置新的适配器
        GroupAdapter adapter = new GroupAdapter(groupList);
        rv.setAdapter(adapter);
    }
}