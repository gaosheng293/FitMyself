package com.example.fitmyself;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {

    private List<String> exercises;
    private OnLogSetListener listener;

    // 用一个 Map 记录每个动作做了几组 (Key: 动作索引, Value: 组数)
    private Map<Integer, Integer> setsMap = new HashMap<>();

    public interface OnLogSetListener {
        void onLogSet(int position, String exerciseName);
    }

    public TrainingAdapter(List<String> exercises, OnLogSetListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    // 更新某一个动作的组数显示
    public void incrementSet(int position) {
        int current = setsMap.getOrDefault(position, 0);
        setsMap.put(position, current + 1);
        notifyItemChanged(position); // 局部刷新
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = exercises.get(position);
        holder.tvName.setText(name);

        int sets = setsMap.getOrDefault(position, 0);
        holder.tvSets.setText("已完成: " + sets + " 组");

        holder.btnLog.setOnClickListener(v -> {
            listener.onLogSet(position, name);
        });
    }

    @Override
    public int getItemCount() {
        return exercises == null ? 0 : exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSets;
        Button btnLog;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ex_name);
            tvSets = itemView.findViewById(R.id.tv_sets_count);
            btnLog = itemView.findViewById(R.id.btn_log_set);
        }
    }
}