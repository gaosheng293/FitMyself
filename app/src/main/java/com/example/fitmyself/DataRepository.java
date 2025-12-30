package com.example.fitmyself;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataRepository {

    // 获取所有支持的分化模式
    public static List<SplitMode> getAllSplitModes() {
        List<SplitMode> modes = new ArrayList<>();

        // --- 在这里添加或修改分化模式，界面会自动更新 ---

        // 1. 五分化
        modes.add(new SplitMode("五分化", "单部位精准训练 (胸/背/肩/腿/手)", 5,
                Arrays.asList("胸", "背", "肩", "腿", "手臂")));

        // 2. 三分化
        modes.add(new SplitMode("三分化", "推/拉/腿 经典分化", 3,
                Arrays.asList("推(胸/肩/三头)", "拉(背/二头)", "腿")));

        // 3. 四分化 (如果你想加，直接解除注释，不用改界面)
        // modes.add(new SplitMode("四分化", "肩手合并训练", 4,
        //        Arrays.asList("胸", "背", "腿", "肩手")));

        // 4. 全身训练
        modes.add(new SplitMode("全身训练", "适合新手的全身循环", 1,
                Arrays.asList("全身")));

        return modes;
    }
}