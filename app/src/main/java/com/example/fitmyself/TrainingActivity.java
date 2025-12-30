package com.example.fitmyself;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class TrainingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrainingAdapter adapter;
    private List<WorkoutDay> allDays;
    private int currentDayIndex;

    // --- 计时器相关变量 ---
    private CardView cardTimer;
    private TextView tvTimerDisplay;
    private Button btnStopRest;

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private long startTime = 0;
    private boolean isTimerRunning = false;
    // 新增几个变量
    private RecordDao recordDao;
    private int lastRecordId = -1; // 刚刚插入的那条记录的ID

    // 计时任务：每秒更新一次 UI
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            // 更新显示格式 00:00
            tvTimerDisplay.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

            // 继续下一秒
            timerHandler.postDelayed(this, 500); // 500ms刷新一次更流畅
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        recordDao = AppDatabase.getDatabase(this).recordDao(); // 初始化DAO

        // 1. 获取数据
        loadData();

        // 2. 初始化列表
        recyclerView = findViewById(R.id.recycler_view_training);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取今天的动作列表
        List<String> todayExercises = allDays.get(currentDayIndex).exerciseList;

        TextView tvTitle = findViewById(R.id.tv_training_title);
        tvTitle.setText(allDays.get(currentDayIndex).bodyPart + " - 训练中");

        adapter = new TrainingAdapter(todayExercises, new TrainingAdapter.OnLogSetListener() {
            @Override
            public void onLogSet(int position, String exerciseName) {
                // 点击“记一组”时，弹出输入框
                showLogDialog(position, exerciseName);
            }
        });
        recyclerView.setAdapter(adapter);

        // --- 绑定计时器控件 ---
        cardTimer = findViewById(R.id.card_timer);
        tvTimerDisplay = findViewById(R.id.tv_timer_countdown); // ID 还是沿用之前的
        btnStopRest = findViewById(R.id.btn_stop_rest); // 注意检查 XML 里的 ID

        // 点击“结束休息”按钮
        btnStopRest.setOnClickListener(v -> stopRestTimer());

        // 结束训练按钮
        findViewById(R.id.btn_finish_workout).setOnClickListener(v -> finish());

    }

    private void loadData() {
        // 简单起见，重新查询一次数据库（实际开发可以用 Intent 传 ID）
        allDays = AppDatabase.getDatabase(this).workoutDao().getAll();
        currentDayIndex = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE)
                .getInt("current_day_index", 0);

        if (currentDayIndex >= allDays.size()) currentDayIndex = 0;
    }

    // 弹出“记录数据”对话框
    private void showLogDialog(int position, String exerciseName) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_log_set, null);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(exerciseName + " - 记录");

        final EditText etWeight = view.findViewById(R.id.et_weight);
        final EditText etReps = view.findViewById(R.id.et_reps);

        // 1. 创建 Dialog，但在 setPositiveButton 里先填 null (不要立刻绑定监听器)
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("完成", null) // <--- 注意这里是 null
                .setNegativeButton("取消", null)
                .create();

        // 2. 显示 Dialog
        dialog.show();

        // 3. 【核心技巧】显示之后，再单独获取按钮并设置点击事件
        // 这样我们就可以控制：如果校验失败，就不调用 dialog.dismiss()，弹窗就不会关
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String weightText = etWeight.getText().toString().trim();
            String repsText = etReps.getText().toString().trim();

            // --- 开始校验 ---

            // 校验重量
            if (!InputValidator.isValidNumber(weightText)) {
                etWeight.setError("重量必须大于0"); // 给输入框加红色错误提示
                return; // 停止执行，弹窗不关闭
            }

            // 校验次数
            if (!InputValidator.isValidNumber(repsText)) {
                etReps.setError("次数必须大于0");
                return; // 停止执行
            }

            // --- 校验通过，继续之前的逻辑 ---

            // 1. 更新界面
            adapter.incrementSet(position);

            // 2. 保存数据库
            saveRecord(exerciseName, weightText, repsText);

            // 3. 开始计时
            startRestTimer();

            // 4. 手动关闭弹窗
            dialog.dismiss();
        });
    }

    // 保存记录的方法
    private void saveRecord(String exerciseName, String weight, String reps) {
        // 获取当前日期
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());
        long timestamp = System.currentTimeMillis();
        String bodyPart = allDays.get(currentDayIndex).bodyPart;

        WorkoutRecord record = new WorkoutRecord(date, timestamp, bodyPart, exerciseName, weight, reps);

        // 数据库操作要在子线程，这里为了简单演示用 allowMainThreadQueries，
        // 如果你很严谨，可以用 new Thread(() -> { ... }).start();
        long id = recordDao.insert(record);
        lastRecordId = (int) id; // 记住ID，等会儿更新休息时间用
    }


    // --- 开始正向计时 ---
    private void startRestTimer() {
        if (isTimerRunning) return; // 防止重复开启

        // 显示计时卡片
        cardTimer.setVisibility(View.VISIBLE);
        // 隐藏列表交互，防止休息时乱点 (可选)
        // recyclerView.setEnabled(false);

        // 记录开始时间
        startTime = System.currentTimeMillis();
        isTimerRunning = true;

        // 立即开始跑任务
        timerHandler.post(timerRunnable);
    }

    // --- 结束休息 ---
    private void stopRestTimer() {
        if (!isTimerRunning) return;

        timerHandler.removeCallbacks(timerRunnable);
        isTimerRunning = false;
        long restDuration = (System.currentTimeMillis() - startTime) / 1000;

        // 更新上一条记录的休息时间
        if (lastRecordId != -1) {
            recordDao.updateRestTime(lastRecordId, restDuration);
        }

        cardTimer.setVisibility(View.GONE);
        Toast.makeText(this, "休息 " + restDuration + "秒，记录已更新", Toast.LENGTH_SHORT).show();
    }

    // 记得在页面销毁时停止计时，防止内存泄漏
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

}