package com.example.fitmyself;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private List<WorkoutRecord> list;

    public DetailAdapter(List<WorkoutRecord> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WorkoutRecord r = list.get(position);
        holder.tvName.setText(r.exerciseName);
        holder.tvData.setText(r.weight + "kg × " + r.reps);

        if(r.restTimeSeconds > 0) {
            holder.tvRest.setText("休 " + r.restTimeSeconds + "s");
        } else {
            holder.tvRest.setText("无休息记录");
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvData, tvRest;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_hist_name);
            tvData = itemView.findViewById(R.id.tv_hist_data);
            tvRest = itemView.findViewById(R.id.tv_hist_rest);
        }
    }
}