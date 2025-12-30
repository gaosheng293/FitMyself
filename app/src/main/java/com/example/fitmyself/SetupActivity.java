package com.example.fitmyself;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetupActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SplitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        recyclerView = findViewById(R.id.recycler_view_splits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 绑定悬浮按钮点击事件
        FloatingActionButton fab = findViewById(R.id.fab_add_split);
        fab.setOnClickListener(v -> showAddSplitDialog());
    }

    // 【核心修改】将数据加载放到 onResume
    // 无论是第一次进入，还是重置后跳转过来，只要页面可见，就会执行这里
    @Override
    protected void onResume() {
        super.onResume();
        loadSplits();
    }

    private void loadSplits() {
        // 1. 创建一个新的空列表，防止数据重复堆叠
        List<SplitMode> displayList = new ArrayList<>();

        // 2. 先加入系统默认的分化模式
        // (注意：DataRepository.getAllSplitModes() 应该返回一个新的 ArrayList)
        displayList.addAll(DataRepository.getAllSplitModes());

        // 3. 再从数据库查出用户自定义的分化
        List<UserSplit> userSplits = AppDatabase.getDatabase(this).customDao().getAllUserSplits();

        if (userSplits != null) {
            for (UserSplit us : userSplits) {
                // 安全检查：防止空指针
                if (us.bodyParts != null) {
                    List<String> parts = Arrays.asList(us.bodyParts.split(","));
                    displayList.add(new SplitMode(us.name, us.description, parts.size(), parts));
                }
            }
        }

        // 4. 设置适配器
        // 如果 adapter 为空就新建，如果不为空就刷新数据 (优化性能)
        if (adapter == null) {
            adapter = new SplitAdapter(displayList, this::goToNextStep,mode -> showDeleteSplitDialog(mode));
            recyclerView.setAdapter(adapter);
        } else {
            // 如果你愿意给 SplitAdapter 加一个 updateList 方法会更好，
            // 但为了简单，这里直接重新设置一个新的 adapter 也行
            adapter = new SplitAdapter(displayList, this::goToNextStep,mode -> showDeleteSplitDialog(mode));
            recyclerView.setAdapter(adapter);
        }
    }

    // 显示删除确认弹窗
    private void showDeleteSplitDialog(SplitMode mode) {
        // 1. 系统自带的不能删 (我们假设系统自带的没有 ID 或者我们在 SplitMode 里加个标记)
        // 这里用一个简单的逻辑：检查数据库里有没有这个名字，或者我们可以简单判断
        // 更好的方式是 SplitMode 里加一个 isCustom 字段。
        // 这里我们先去数据库查一下，查不到就是系统的。

        new AlertDialog.Builder(this)
                .setTitle("删除分化")
                .setMessage("确定要删除 \"" + mode.getName() + "\" 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    // 数据库操作
                    UserSplit splitToDelete = findUserSplitByName(mode.getName());

                    if (splitToDelete != null) {
                        AppDatabase.getDatabase(this).customDao().deleteSplit(splitToDelete);
                        Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                        loadSplits(); // 刷新列表
                    } else {
                        Toast.makeText(this, "系统预设分化不可删除", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 辅助方法：根据名字找数据库里的对象
    private UserSplit findUserSplitByName(String name) {
        List<UserSplit> list = AppDatabase.getDatabase(this).customDao().getAllUserSplits();
        for (UserSplit us : list) {
            if (us.name.equals(name)) return us;
        }
        return null; // 没找到，说明是系统的
    }

    private void goToNextStep(SplitMode mode) {
        Intent intent = new Intent(SetupActivity.this, ArrangeCycleActivity.class);
        intent.putExtra("split_name", mode.getName());
        intent.putStringArrayListExtra("default_parts", mode.getDefaultBodyParts());
        startActivity(intent);
        // 不调用 finish()，允许用户按返回键回来
    }

    private void showAddSplitDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_split, null);

        final EditText etName = view.findViewById(R.id.et_split_name);
        final EditText etDesc = view.findViewById(R.id.et_split_desc);
        final EditText etParts = view.findViewById(R.id.et_split_parts);

        new AlertDialog.Builder(this)
                .setTitle("创建新分化")
                .setView(view)
                .setPositiveButton("创建", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String partsStr = etParts.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();

                    if (name.isEmpty() || partsStr.isEmpty()) {
                        Toast.makeText(this, "名称和部位不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 替换中文逗号，防止格式错误
                    partsStr = partsStr.replace("，", ",");

                    // 保存到数据库
                    UserSplit newSplit = new UserSplit(name, desc, partsStr);
                    AppDatabase.getDatabase(this).customDao().insertSplit(newSplit);

                    Toast.makeText(this, "分化创建成功", Toast.LENGTH_SHORT).show();

                    // 重新加载列表 (虽然 onResume 会自动加载，但手动调一下反应更快)
                    loadSplits();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}