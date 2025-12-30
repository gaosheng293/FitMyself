package com.example.fitmyself;

import java.util.ArrayList;
import java.util.List;

public class ExerciseRepository {

    // 模拟数据库：根据部位名称，返回该部位的所有动作
    public static List<Exercise> getExercisesByPart(String bodyPart) {
        List<Exercise> list = new ArrayList<>();

        // 这里可以使用 switch 或 if 来匹配部位
        // 注意：这里的部位名称必须和之前 SplitMode 里定义的一致

        if (bodyPart.contains("胸")) {
            list.add(new Exercise("平板杠铃卧推", "胸"));
            list.add(new Exercise("上斜哑铃卧推", "胸"));
            list.add(new Exercise("下斜杠铃卧推", "胸"));
            list.add(new Exercise("平板哑铃卧推", "胸"));
            list.add(new Exercise("平板哑铃飞鸟", "胸"));
            list.add(new Exercise("上斜哑铃飞鸟", "胸"));
            list.add(new Exercise("龙门架绳索夹胸", "胸"));
            list.add(new Exercise("坐姿器械推胸", "胸"));
            list.add(new Exercise("蝴蝶机夹胸", "胸"));
            list.add(new Exercise("双杠臂屈伸", "胸"));
            list.add(new Exercise("俯卧撑", "胸"));
        }
        else if (bodyPart.contains("背")) {
            list.add(new Exercise("引体向上", "背"));
            list.add(new Exercise("高位下拉", "背"));
            list.add(new Exercise("坐姿绳索划船", "背"));
            list.add(new Exercise("俯身杠铃划船", "背"));
            list.add(new Exercise("单臂哑铃划船", "背"));
            list.add(new Exercise("T杠划船", "背"));
            list.add(new Exercise("直臂下压", "背"));
            list.add(new Exercise("器械划船", "背"));
            list.add(new Exercise("山羊挺身", "背"));
            list.add(new Exercise("传统硬拉", "背"));
        }
        else if (bodyPart.contains("肩")) {
            list.add(new Exercise("坐姿哑铃推举", "肩"));
            list.add(new Exercise("站姿杠铃推举", "肩"));
            list.add(new Exercise("史密斯推举", "肩"));
            list.add(new Exercise("哑铃侧平举", "肩"));
            list.add(new Exercise("绳索侧平举", "肩"));
            list.add(new Exercise("哑铃前平举", "肩"));
            list.add(new Exercise("俯身哑铃飞鸟", "肩"));
            list.add(new Exercise("蝴蝶机反向飞鸟", "肩"));
            list.add(new Exercise("绳索面拉", "肩"));
            list.add(new Exercise("杠铃耸肩", "肩"));
        }
        else if (bodyPart.contains("腿")) {
            list.add(new Exercise("杠铃深蹲", "腿"));
            list.add(new Exercise("颈前深蹲", "腿"));
            list.add(new Exercise("倒蹬机腿举", "腿"));
            list.add(new Exercise("哈克深蹲", "腿"));
            list.add(new Exercise("坐姿腿屈伸", "腿"));
            list.add(new Exercise("俯卧腿弯举", "腿"));
            list.add(new Exercise("坐姿腿弯举", "腿"));
            list.add(new Exercise("罗马尼亚硬拉", "腿"));
            list.add(new Exercise("直腿硬拉", "腿"));
            list.add(new Exercise("行走箭步蹲", "腿"));
            list.add(new Exercise("站姿提踵", "腿"));
            list.add(new Exercise("坐姿提踵", "腿"));
        }
        else if (bodyPart.contains("手") || bodyPart.contains("二头") || bodyPart.contains("三头")) {
            list.add(new Exercise("站姿杠铃弯举", "手臂"));
            list.add(new Exercise("哑铃交替弯举", "手臂"));
            list.add(new Exercise("锤式弯举", "手臂"));
            list.add(new Exercise("牧师凳弯举", "手臂"));
            list.add(new Exercise("集中弯举", "手臂"));
            list.add(new Exercise("绳索下压", "手臂"));
            list.add(new Exercise("直杆下压", "手臂"));
            list.add(new Exercise("仰卧杠铃臂屈伸", "手臂"));
            list.add(new Exercise("坐姿哑铃颈后臂屈伸", "手臂"));
            list.add(new Exercise("窄距卧推", "手臂"));
            list.add(new Exercise("单臂绳索臂屈伸", "手臂"));
        }
        else if (bodyPart.contains("腹")|| bodyPart.contains("腹肌")) {
            list.add(new Exercise("仰卧卷腹", "腹肌"));
            list.add(new Exercise("悬垂举腿", "腹肌"));
            list.add(new Exercise("仰卧举腿", "腹肌"));
            list.add(new Exercise("俄罗斯转体", "腹肌"));
            list.add(new Exercise("绳索卷腹", "腹肌"));
            list.add(new Exercise("平板支撑", "腹肌"));
            list.add(new Exercise("健腹轮", "腹肌"));

        }
        else {
            // 通用或未知部位
            list.add(new Exercise("波比跳", "全身"));
            list.add(new Exercise("跑步机", "有氧"));
            list.add(new Exercise("椭圆机", "有氧"));
            list.add(new Exercise("划船机", "有氧"));
            list.add(new Exercise("跳绳", "有氧"));
        }

        return list;
    }
}