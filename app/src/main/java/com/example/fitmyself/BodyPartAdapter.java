package com.example.fitmyself;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class BodyPartAdapter extends RecyclerView.Adapter<BodyPartAdapter.ViewHolder> {

    private List<String> partList;

    public BodyPartAdapter(List<String> partList) {
        this.partList = partList;
    }

    public interface OnLongClickListener {
        void onLongClick(int position);
    }
    private OnLongClickListener longListener;

    public BodyPartAdapter(List<String> partList, OnLongClickListener longListener) {
        this.partList = partList;
        this.longListener = longListener;
    }

    // 【关键方法】供 Activity 调用，用于在拖拽时交换数据位置
    public void onItemMove(int fromPosition, int toPosition) {
        // 使用 Collections.swap 快速交换列表中两个元素的位置
        Collections.swap(partList, fromPosition, toPosition);
        // 通知适配器这两个位置发生了移动，以更新界面动画
        notifyItemMoved(fromPosition, toPosition);
    }

    // 获取当前排序后的列表
    public List<String> getCurrentList() {
        return partList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_body_part, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvPartName.setText(partList.get(position));
        // 这里可以加一些逻辑，比如在名字前面加上 "第 " + (position+1) + " 天："
        holder.itemView.setOnLongClickListener(v -> {
            longListener.onLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return partList == null ? 0 : partList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartName;
        // 如果需要实现“按住图标才能拖拽”，这里需要获取 ImageView

        public ViewHolder(View itemView) {
            super(itemView);
            tvPartName = itemView.findViewById(R.id.tv_part_name);
        }
    }
}