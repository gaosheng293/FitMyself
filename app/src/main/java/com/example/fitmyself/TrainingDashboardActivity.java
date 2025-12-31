package com.example.fitmyself;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrainingDashboardActivity extends AppCompatActivity {

    private TextView tvCycleDay, tvTodayPart;
    private RecyclerView recyclerView;
    private List<WorkoutDay> allWorkoutDays;

    // 不需要 isFirstLoad 了，我们让 SharedPreferences 成为唯一真理
    private int currentDayIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_dashboard);

        // 1. 初始化控件
        tvCycleDay = findViewById(R.id.tv_cycle_day);
        tvTodayPart = findViewById(R.id.tv_today_part);
        recyclerView = findViewById(R.id.recycler_view_today_exercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. 显示日期
        TextView tvDate = findViewById(R.id.tv_dashboard_date);
        String today = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINESE).format(new Date());
        tvDate.setText(today);

        // 3. 绑定按钮事件
        findViewById(R.id.btn_reset_plan).setOnClickListener(v -> showResetDialog());

        findViewById(R.id.btn_edit_exercises).setOnClickListener(v -> {
            if (allWorkoutDays == null || allWorkoutDays.isEmpty()) return;
            java.util.ArrayList<String> currentOrder = new java.util.ArrayList<>();
            for (WorkoutDay day : allWorkoutDays) {
                currentOrder.add(day.bodyPart);
            }
            Intent intent = new Intent(TrainingDashboardActivity.this, SelectExercisesActivity.class);
            intent.putStringArrayListExtra("final_order", currentOrder);
            startActivity(intent);
        });

        findViewById(R.id.btn_history).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        // 【关键】点击下一天/跳过今天
        findViewById(R.id.btn_next_day).setOnClickListener(v -> advanceToNextDay());

        findViewById(R.id.btn_start_training).setOnClickListener(v -> {
            if (allWorkoutDays != null && !allWorkoutDays.isEmpty()) {
                // 也要保存一下当前进度，双重保险
                saveCurrentProgress();
                Intent intent = new Intent(TrainingDashboardActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        });
    }

    // 每次页面可见时执行 (包括最小化再打开)
    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载数据和进度
        loadWorkoutPlan();
    }

    private void loadWorkoutPlan() {
        AppDatabase db = AppDatabase.getDatabase(this);
        // 1. 获取所有计划数据
        allWorkoutDays = db.workoutDao().getAll();

        if (allWorkoutDays == null || allWorkoutDays.isEmpty()) {
            tvTodayPart.setText("暂无计划");
            tvCycleDay.setText("请点击重置计划");
            return;
        }

        // 2. 【核心逻辑】直接从 SharedPreferences 读取进度
        // 这意味着：无论你是冷启动，还是最小化回来，都以硬盘里存的进度为准
        SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
        currentDayIndex = prefs.getInt("current_day_index", 0);

        // 3. 安全检查
        if (currentDayIndex >= allWorkoutDays.size()) {
            currentDayIndex = 0;
            // 越界自动修复并保存
            saveCurrentProgress();
        }

        // 4. 刷新界面
        updateUI();
    }

    private void updateUI() {
        if (allWorkoutDays == null || allWorkoutDays.isEmpty()) return;

        WorkoutDay today = allWorkoutDays.get(currentDayIndex);

        tvCycleDay.setText("当前循环：第 " + (today.dayIndex + 1) + " 天");
        tvTodayPart.setText(today.bodyPart + " 训练");

        DashboardAdapter adapter = new DashboardAdapter(today.exerciseList);
        recyclerView.setAdapter(adapter);
    }

    private void advanceToNextDay() {
        if (allWorkoutDays == null || allWorkoutDays.isEmpty()) return;

        // 1. 内存中切换
        currentDayIndex++;
        if (currentDayIndex >= allWorkoutDays.size()) {
            currentDayIndex = 0; // 循环回到第一天
        }

        // 2. 【关键】立刻保存到硬盘 (Commit 是同步写入，Apply 是异步，为了确保逻辑正确，这里改状态很快，没性能问题)
        saveCurrentProgress();

        // 3. 刷新界面
        updateUI();
        Toast.makeText(this, "已切换到下一天", Toast.LENGTH_SHORT).show();
    }

    // 抽离出的保存方法
    private void saveCurrentProgress() {
        SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
        prefs.edit().putInt("current_day_index", currentDayIndex).apply();
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("重置计划")
                .setMessage("确定要重新选择分化和动作吗？当前进度将丢失。")
                .setPositiveButton("确定重置", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    AppDatabase.getDatabase(this).workoutDao().deleteAll();

                    Intent intent = new Intent(this, SetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}