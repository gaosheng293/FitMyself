package com.example.fitmyself;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<WorkoutRecord> records;

    public HistoryAdapter(List<WorkoutRecord> records) {
        this.records = records;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 这里我们偷个懒，用系统的简单布局，或者你可以自己写个好看的 item_history.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WorkoutRecord r = records.get(position);

        // 主标题：日期 - 动作
        holder.text1.setText(r.date + " | " + r.exerciseName);
        holder.text1.setTextColor(Color.BLACK);
        holder.text1.setTextSize(16);

        // 副标题：成绩细节
        String detail = "重量: " + r.weight + "kg  x  " + r.reps + "次";
        if (r.restTimeSeconds > 0) {
            detail += "  (休" + r.restTimeSeconds + "s)";
        }
        holder.text2.setText(detail);
        holder.text2.setTextColor(Color.DKGRAY);
    }

    @Override
    public int getItemCount() { return records.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        public ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}