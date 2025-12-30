package com.example.fitmyself;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<HistoryGroup> groups;

    public GroupAdapter(List<HistoryGroup> groups) {
        this.groups = groups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryGroup group = groups.get(position);

        // 1. 设置日期标题
        holder.tvDate.setText(group.getDate());
        holder.tvSummary.setText("共完成 " + group.getRecords().size() + " 组动作");

        // 2. 初始化子 RecyclerView (显示详情)
        DetailAdapter detailAdapter = new DetailAdapter(group.getRecords());
        holder.rvDetails.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvDetails.setAdapter(detailAdapter);

        // 3. 根据 isExpanded 状态控制可见性
        boolean isExpanded = group.isExpanded();
        holder.rvDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // 箭头旋转动画 (简单版：直接设角度)
        holder.ivArrow.setRotation(isExpanded ? 180f : 0f);

        // 4. 点击事件：切换展开状态
        holder.headerLayout.setOnClickListener(v -> {
            group.setExpanded(!group.isExpanded());
            // 刷新这一行，让UI变化生效
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() { return groups.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSummary;
        ImageView ivArrow;
        RecyclerView rvDetails;
        RelativeLayout headerLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_group_date);
            tvSummary = itemView.findViewById(R.id.tv_group_summary);
            ivArrow = itemView.findViewById(R.id.iv_expand_arrow);
            rvDetails = itemView.findViewById(R.id.rv_group_details);
            headerLayout = itemView.findViewById(R.id.layout_group_header);
        }
    }
}