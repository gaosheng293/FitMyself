package com.example.fitmyself;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectExercisesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DayAdapter adapter;
    private List<String> sortedBodyParts;
    // 用一个 Map 来存储：Key是第几天(0,1,2...), Value是选中的动作列表
    private Map<Integer, List<String>> userSelectionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercises);

        // 1. 获取上一页排好的部位顺序
        sortedBodyParts = getIntent().getStringArrayListExtra("final_order");
        if (sortedBodyParts == null) sortedBodyParts = new ArrayList<>();

        // 【新增】2. 尝试从数据库加载已保存的动作 (回显功能)
        loadExistingSelections();

        recyclerView = findViewById(R.id.recycler_view_days);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. 设置适配器 (内部类实现，代码更紧凑)
        adapter = new DayAdapter();
        recyclerView.setAdapter(adapter);

        // 3. 完成按钮
        Button btnFinish = findViewById(R.id.btn_finish_setup);
        btnFinish.setOnClickListener(v -> finishSetup());
    }

    // 【新增方法】从数据库读取旧数据填充到 map 中
    private void loadExistingSelections() {
        AppDatabase db = AppDatabase.getDatabase(this);
        List<WorkoutDay> savedDays = db.workoutDao().getAll();

        if (savedDays != null && !savedDays.isEmpty()) {
            for (WorkoutDay day : savedDays) {
                // 将数据库里的 List<String> 放入 map，Key 是 dayIndex
                if (day.exerciseList != null) {
                    userSelectionMap.put(day.dayIndex, day.exerciseList);
                }
            }
        }
    }

    // 弹出多选对话框
    private void showSelectionDialog(int position, String bodyPart) {
        // 1. 【关键】创建一个可修改的列表，先装入系统默认动作
        // 注意：一定要用 new ArrayList<>(...) 包裹，否则默认列表可能是不可修改的，导致报错
        List<Exercise> allExercises = new ArrayList<>(ExerciseRepository.getExercisesByPart(bodyPart));

        // 2. 【关键】从数据库查询用户自定义动作，并合并进去
        List<UserExercise> customExs = AppDatabase.getDatabase(this).customDao().getUserExercisesByPart(bodyPart);
        if (customExs != null) {
            for (UserExercise ue : customExs) {
                // 把数据库对象转成显示对象
                allExercises.add(new Exercise(ue.name, ue.bodyPart));
            }
        }

        // 3. 准备数据给对话框显示
        String[] exerciseNames = new String[allExercises.size()];
        boolean[] checkedItems = new boolean[allExercises.size()];

        // 获取用户之前已选的动作 (回显勾选状态)
        List<String> currentSelected = userSelectionMap.getOrDefault(position, new ArrayList<>());

        for (int i = 0; i < allExercises.size(); i++) {
            exerciseNames[i] = allExercises.get(i).getName();
            // 如果名字一样，就打钩
            if (currentSelected.contains(exerciseNames[i])) {
                checkedItems[i] = true;
            }
        }

        // 4. 构建并显示对话框
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(bodyPart + " - 选择动作")
                .setMultiChoiceItems(exerciseNames, checkedItems, (d, which, isChecked) -> {
                    checkedItems[which] = isChecked;
                })
                .setPositiveButton("确定", (d, w) -> {
                    // 点击确定，保存用户的勾选结果
                    List<String> newSelection = new ArrayList<>();
                    for (int i = 0; i < checkedItems.length; i++) {
                        if (checkedItems[i]) {
                            newSelection.add(exerciseNames[i]);
                        }
                    }
                    userSelectionMap.put(position, newSelection);
                    adapter.notifyItemChanged(position);
                })
                .setNeutralButton("+ 新建动作", null)
                .create();

        dialog.show();

        // 2. 【核心黑科技】获取 Dialog 内部的 ListView
        android.widget.ListView listView = dialog.getListView();

        // 3. 设置长按监听
        listView.setOnItemLongClickListener((parent, view, itemPosition, id) -> {
            String selectedName = exerciseNames[itemPosition];

            // 判断是否为自定义动作 (系统动作不能删)
            // 简单判断法：去 ExerciseRepository 查一下，查不到就是自定义的
            boolean isSystem = false;
            for (Exercise sysEx : ExerciseRepository.getExercisesByPart(bodyPart)) {
                if (sysEx.getName().equals(selectedName)) {
                    isSystem = true;
                    break;
                }
            }

            if (isSystem) {
                Toast.makeText(this, "系统预设动作无法删除", Toast.LENGTH_SHORT).show();
            } else {
                // 弹出二次确认删除
                new AlertDialog.Builder(this)
                        .setTitle("删除动作")
                        .setMessage("确定永久删除 \"" + selectedName + "\" 吗？")
                        .setPositiveButton("删除", (delDialog, delWhich) -> {
                            // 1. 删库
                            AppDatabase.getDatabase(this).customDao().deleteExerciseByName(selectedName, bodyPart);
                            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();

                            // 2. 关闭当前选择弹窗 (因为数据过时了)
                            dialog.dismiss();

                            // 3. 重新打开弹窗 (刷新列表)
                            showSelectionDialog(position, bodyPart);
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
            return true; // 消费事件
        });

        // 5. 绑定新建动作按钮事件
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            // 弹出输入框
            showAddExerciseInput(bodyPart, dialog, position);
        });
    }

    // 弹出小窗输入新动作
    private void showAddExerciseInput(String bodyPart, AlertDialog parentDialog, int position) {
        EditText etName = new EditText(this);
        etName.setHint("输入动作名称");

        new AlertDialog.Builder(this)
                .setTitle("添加 " + bodyPart + " 动作")
                .setView(etName)
                .setPositiveButton("保存", (d, w) -> {
                    String name = etName.getText().toString().trim(); // 记得加 trim() 去空格
                    if (name.isEmpty()) return;

                    // 1. 存入数据库
                    UserExercise newEx = new UserExercise(name, bodyPart);
                    AppDatabase.getDatabase(this).customDao().insertExercise(newEx);

                    Toast.makeText(this, "已添加: " + name, Toast.LENGTH_SHORT).show();

                    // 2. 【关键】关闭旧的列表弹窗
                    if (parentDialog != null) {
                        parentDialog.dismiss();
                    }

                    // 3. 【关键】重新打开列表弹窗 (这时会重新执行第二步的代码，把新数据查出来)
                    showSelectionDialog(position, bodyPart);
                })
                .show();
    }

    // 完成设置逻辑
    private void finishSetup() {
        // 1. 准备要保存的数据列表
        List<WorkoutDay> daysToSave = new ArrayList<>();

        // 遍历排序好的部位列表 (sortedBodyParts)
        for (int i = 0; i < sortedBodyParts.size(); i++) {
            String part = sortedBodyParts.get(i);

            // 获取用户为这一天选的动作，如果没有选就存个空列表
            List<String> exercises = userSelectionMap.get(i);
            if (exercises == null) {
                exercises = new ArrayList<>();
            }

            // 创建数据对象
            WorkoutDay day = new WorkoutDay(i, part, exercises);
            daysToSave.add(day);
        }

        // 2. 【核心】调用数据库保存
        AppDatabase db = AppDatabase.getDatabase(this);

        // 先清空旧数据（防止用户反复设置导致数据堆积）
        db.workoutDao().deleteAll();
        // 插入新数据
        db.workoutDao().insertAll(daysToSave);

        // 3. 标记设置完成
        SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("is_setup_complete", true).apply();

        // 4. 提示并跳转
        Toast.makeText(this, "计划保存成功！", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SelectExercisesActivity.this, TrainingDashboardActivity.class);
        // 清空栈，防止按返回键回到设置页
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // --- 内部适配器类 ---
    class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_day_selection, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String bodyPart = sortedBodyParts.get(position);
            holder.tvTitle.setText("第 " + (position + 1) + " 天：" + bodyPart);

            // 检查已选了多少个
            List<String> selected = userSelectionMap.get(position);
            if (selected == null || selected.isEmpty()) {
                holder.tvSummary.setText("点击此处添加动作 +");
                holder.tvSummary.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                holder.tvSummary.setText("已选 " + selected.size() + " 个动作: " + selected.toString().replace("[", "").replace("]", ""));
                holder.tvSummary.setTextColor(0xFF333333); // 深灰色
            }

            // 点击弹出选择框
            holder.itemView.setOnClickListener(v -> showSelectionDialog(position, bodyPart));
        }

        @Override
        public int getItemCount() {
            return sortedBodyParts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSummary;
            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_day_title);
                tvSummary = itemView.findViewById(R.id.tv_selected_summary);
            }
        }
    }
}