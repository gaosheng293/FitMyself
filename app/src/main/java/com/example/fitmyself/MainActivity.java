package com.example.fitmyself;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 获取轻量级存储
        SharedPreferences prefs = getSharedPreferences("FitMyselfPrefs", MODE_PRIVATE);
        boolean isSetupComplete = prefs.getBoolean("is_setup_complete", false);

        if (!isSetupComplete) {
            // 2. 如果是第一次进入，跳转到设置向导页面
            Intent intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish(); // 关闭 MainActivity，防止用户按返回键回来
        } else {
            // 3. 如果已经设置过，跳转到训练主页
            Intent intent = new Intent(this, TrainingDashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }
}