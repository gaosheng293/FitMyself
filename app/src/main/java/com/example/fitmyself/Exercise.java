package com.example.fitmyself;

public class Exercise {
    private String name;       // 动作名称，如 "平板杠铃卧推"
    private String bodyPart;   // 所属部位，如 "胸"
    private boolean isSelected; // 临时状态，用于界面勾选

    public Exercise(String name, String bodyPart) {
        this.name = name;
        this.bodyPart = bodyPart;
        this.isSelected = false;
    }

    public String getName() { return name; }
    public String getBodyPart() { return bodyPart; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    // 重写 toString 是为了在对话框里直接显示名字
    @Override
    public String toString() { return name; }
}