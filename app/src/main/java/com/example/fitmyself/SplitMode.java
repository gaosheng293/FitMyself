package com.example.fitmyself;

import java.util.List;
import java.util.ArrayList;

public class SplitMode {
    private String name;        // 比如 "五分化"
    private String description; // 比如 "单部位精准训练"
    private int dayCount;       // 比如 5
    // 默认的部位循环顺序（后续可以修改）
    private List<String> defaultBodyParts;

    public SplitMode(String name, String description, int dayCount, List<String> defaultBodyParts) {
        this.name = name;
        this.description = description;
        this.dayCount = dayCount;
        this.defaultBodyParts = defaultBodyParts;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDayCount() { return dayCount; }
    public ArrayList<String> getDefaultBodyParts() { return new ArrayList<>(defaultBodyParts); }
}