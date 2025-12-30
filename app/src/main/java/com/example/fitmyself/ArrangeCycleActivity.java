package com.example.fitmyself;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ArrangeCycleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BodyPartAdapter adapter;
    private List<String> bodyPartsList;
    private String selectedSplitName; // 确保这个变量在 onCreate 里接收了 intent 的值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_cycle);

        // 1. 接收上一个页面传递过来的数据
        selectedSplitName = getIntent().getStringExtra("split_name");
        // 接收默认的部位列表
        bodyPartsList = getIntent().getStringArrayListExtra("default_parts");

        // 安全检查，防止空指针
        if (bodyPartsList == null) {
            bodyPartsList = new ArrayList<>();
            Toast.makeText(this, "数据加载错误", Toast.LENGTH_SHORT).show();
        }

        // 2. 初始化 RecyclerView 和 Adapter
        recyclerView = findViewById(R.id.recycler_view_cycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化 Adapter，处理长按删除逻辑
        adapter = new BodyPartAdapter(bodyPartsList, position -> {
            // 长按回调：询问删除
            new AlertDialog.Builder(this)
                    .setTitle("移除部位")
                    .setMessage("确定移除该部位吗？")
                    .setPositiveButton("移除", (d, w) -> {
                        // 1. 界面移除
                        adapter.getCurrentList().remove(position);
                        adapter.notifyItemRemoved(position);
                        // 刷新后续项目的序号，防止错乱
                        adapter.notifyItemRangeChanged(position, adapter.getItemCount());

                        // 2. 【新增】同步保存到数据库
                        saveChangesToDatabase();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
        recyclerView.setAdapter(adapter);

        // 3. 【核心】设置拖拽功能 (ItemTouchHelper)
        setupItemTouchHelper();

        // 4. 设置添加按钮逻辑
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_part);
        fabAdd.setOnClickListener(v -> {
            EditText et = new EditText(this);
            et.setHint("输入部位名称");
            // 加个背景框美化
            et.setBackgroundResource(android.R.drawable.edit_text);

            // 创建弹窗但不直接显示，为了后面控制按钮逻辑
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("添加训练日")
                    .setView(et)
                    .setPositiveButton("添加", null) // 先设为 null
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();

            // 重写点击事件，防止空输入关闭弹窗
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(btn -> {
                String part = et.getText().toString().trim();
                if (part.isEmpty()) {
                    et.setError("不能为空");
                    return;
                }

                // 1. 界面添加
                adapter.getCurrentList().add(part);
                adapter.notifyItemInserted(adapter.getCurrentList().size() - 1);
                dialog.dismiss();

                // 2. 【新增】同步保存到数据库
                saveChangesToDatabase();
            });
        });

        // 5. 下一步按钮点击事件
        Button btnConfirm = findViewById(R.id.btn_confirm_cycle);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取用户最终排序好的列表
                ArrayList<String> finalOrder = (ArrayList<String>) adapter.getCurrentList();

                // 跳转到选择动作页面
                Intent intent = new Intent(ArrangeCycleActivity.this, SelectExercisesActivity.class);
                intent.putStringArrayListExtra("final_order", finalOrder);
                startActivity(intent);
            }
        });
    }

    // 设置拖拽助手的方法
    private void setupItemTouchHelper() {
        // 定义回调：告诉系统我们要监听什么动作（上下拖拽）
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, // 监听上下拖动
                0 // 不监听侧滑删除
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                // 获取拖拽前的初始位置
                int fromPosition = viewHolder.getAdapterPosition();
                // 获取拖拽到的目标位置
                int toPosition = target.getAdapterPosition();

                // 调用 Adapter 的方法来交换数据并刷新界面
                adapter.onItemMove(fromPosition, toPosition);

                // 返回 true 表示我们已经处理了这次移动
                // 注意：拖拽通常不需要每一步都保存数据库，建议只在删除/添加时保存
                // 或者在 Activity 销毁/点击下一步时统一保存排序
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 这里是侧滑删除的逻辑，我们不需要，留空即可
            }
        };

        // 将这个助手绑定到我们的 RecyclerView 上
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // --- 【新增核心方法】将当前的列表保存回数据库 ---
    private void saveChangesToDatabase() {
        if (selectedSplitName == null) return;

        // 数据库操作需要在子线程进行
        new Thread(() -> {
            CustomDao dao = AppDatabase.getDatabase(this).customDao();

            // 1. 检查是不是用户自定义的分化
            UserSplit split = dao.getSplitByName(selectedSplitName);

            if (split != null) {
                // 是自定义的 -> 执行更新

                // 把 List 转成逗号分隔的 String (如 "胸,背,腿")
                StringBuilder sb = new StringBuilder();
                List<String> currentList = adapter.getCurrentList();
                for (int i = 0; i < currentList.size(); i++) {
                    sb.append(currentList.get(i));
                    if (i < currentList.size() - 1) {
                        sb.append(",");
                    }
                }

                // 更新数据库
                dao.updateSplitParts(selectedSplitName, sb.toString());

                // 回到主线程 (如果需要提示用户)
                runOnUiThread(() -> {
                    // 可以在这里 Toast 提示 "保存成功"，也可以静默保存
                });
            } else {
                // 是系统自带的 -> 无法永久修改
                runOnUiThread(() -> {
                    Toast.makeText(this, "提示：系统预设模板无法永久修改，本次修改仅对当前循环有效。", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}