package com.example.fitmyself;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class TrainingDashboardActivity extends AppCompatActivity {

    private TextView tvCycleDay, tvTodayPart;
    private RecyclerView recyclerView;
    private List<WorkoutDay> allWorkoutDays;
    private int currentDayIndex = 0; // 0, 1, 2...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_dashboard);

//        // 【新增】强制设置状态栏颜色为深蓝色
//        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
//
//        // 【新增】强制设置状态栏文字为白色 (false 表示不是浅色背景)
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(0); // 清除可能存在的浅色标志

        tvCycleDay = findViewById(R.id.tv_cycle_day);
        tvTodayPart = findViewById(R.id.tv_today_part);
        recyclerView = findViewById(R.id.recycler_view_today_exercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 显示今天的日期
        TextView tvDate = findViewById(R.id.tv_dashboard_date);
        String today = new java.text.SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINESE).format(new java.util.Date());
        tvDate.setText(today);

        // 1. 加载数据库中的计划
        loadWorkoutPlan();

        // 2. 左上角重置按钮
        findViewById(R.id.btn_reset_plan).setOnClickListener(v -> {
            showResetDialog();
        });

        findViewById(R.id.btn_edit_exercises).setOnClickListener(v -> {
            if (allWorkoutDays == null || allWorkoutDays.isEmpty()) return;

            // 1. 提取当前的部位顺序列表
            java.util.ArrayList<String> currentOrder = new java.util.ArrayList<>();
            for (WorkoutDay day : allWorkoutDays) {
                currentOrder.add(day.bodyPart);
            }

            // 2. 跳转回选择动作页面
            Intent intent = new Intent(TrainingDashboardActivity.this, SelectExercisesActivity.class);
            intent.putStringArrayListExtra("final_order", currentOrder);
            startActivity(intent);
        });
        // 历史按钮跳转
        findViewById(R.id.btn_history).setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });

        // 3. 切换到下一天 (模拟完成训练)
        findViewById(R.id.btn_next_day).setOnClickListener(v -> advanceToNextDay());

        // 4. 开始训练 (后续开发核心功能)
        findViewById(R.id.btn_start_training).setOnClickListener(v -> {
            Intent intent = new Intent(TrainingDashboardActivity.this, TrainingActivity.class);
            startActivity(intent);
        });
    }

    private void loadWorkoutPlan() {
        AppDatabase db = AppDatabase.getDatabase(this);
        allWorkoutDays = db.workoutDao().getAll();

        if (allWorkoutDays == null || allWorkoutDays.isEmpty()) {
            // 如果数据库空了（异常情况），回退到设置页
            tvTodayPart.setText("暂无计划");
            return;
        }

        // 读取当前进度
        SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
        currentDayIndex = prefs.getInt("current_day_index", 0);

        // 如果进度超出了总天数（比如删改了计划），归零
        if (currentDayIndex >= allWorkoutDays.size()) {
            currentDayIndex = 0;
        }

        updateUI();
    }

    private void updateUI() {
        // 获取今天的计划对象
        WorkoutDay today = allWorkoutDays.get(currentDayIndex);

        tvCycleDay.setText("当前循环：第 " + (today.dayIndex + 1) + " 天");
        tvTodayPart.setText(today.bodyPart + " 训练");

        // 设置列表
        DashboardAdapter adapter = new DashboardAdapter(today.exerciseList);
        recyclerView.setAdapter(adapter);
    }

    private void advanceToNextDay() {
        // 索引 +1
        currentDayIndex++;

        // 如果超过了最后一天，回到第一天 (循环)
        if (currentDayIndex >= allWorkoutDays.size()) {
            currentDayIndex = 0;
        }

        // 保存新进度
        SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
        prefs.edit().putInt("current_day_index", currentDayIndex).apply();

        // 刷新界面
        updateUI();
        Toast.makeText(this, "已切换到下一天", Toast.LENGTH_SHORT).show();
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("重置计划")
                .setMessage("确定要重新选择分化和动作吗？当前进度将丢失。")
                .setPositiveButton("确定重置", (dialog, which) -> {
                    // 1. 清除设置完成的标记
                    SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    // 2. 清空数据库
                    AppDatabase.getDatabase(this).workoutDao().deleteAll();

                    // 3. 跳转回 SetupActivity
                    Intent intent = new Intent(this, SetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}