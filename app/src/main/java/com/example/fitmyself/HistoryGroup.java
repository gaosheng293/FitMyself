package com.example.fitmyself;

import java.util.List;

public class HistoryGroup {
    private String date;
    private List<WorkoutRecord> records;
    private boolean isExpanded = false; // 记录是否展开

    public HistoryGroup(String date, List<WorkoutRecord> records) {
        this.date = date;
        this.records = records;
    }

    public String getDate() { return date; }
    public List<WorkoutRecord> getRecords() { return records; }

    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}