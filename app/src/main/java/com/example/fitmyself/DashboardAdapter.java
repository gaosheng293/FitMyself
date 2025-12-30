package com.example.fitmyself;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private List<String> exercises;

    public DashboardAdapter(List<String> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 这里为了省事，复用 Android 自带的简单布局，只显示一行文字
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 加上序号，更清晰
        holder.text1.setText((position + 1) + ". " + exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises == null ? 0 : exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        public ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }
}