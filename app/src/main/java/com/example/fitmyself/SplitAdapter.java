package com.example.fitmyself;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.ViewHolder> {

    private List<SplitMode> splitList;
    private OnItemClickListener listener;

    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(SplitMode mode);
    }
    // 1. 定义长按接口
    public interface OnItemLongClickListener {
        void onItemLongClick(SplitMode mode);
    }

    private OnItemLongClickListener longClickListener; // 变量

    // 2. 修改构造函数，接收这个接口
    public SplitAdapter(List<SplitMode> splitList, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
        this.splitList = splitList;
        this.listener = listener;
        this.longClickListener = longClickListener; // 赋值
    }
    public SplitAdapter(List<SplitMode> splitList, OnItemClickListener listener) {
        this.splitList = splitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 这里我们需要定义一个简单的 item 布局，稍后创建 item_split.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_split, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SplitMode mode = splitList.get(position);
        holder.tvName.setText(mode.getName());
        holder.tvDesc.setText(mode.getDescription());

        // 点击事件
        holder.itemView.setOnClickListener(v -> listener.onItemClick(mode));
        // 【新增】长按事件
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(mode);
            }
            return true; // 返回 true 表示消费了事件，不会触发短按
        });
    }

    @Override
    public int getItemCount() {
        return splitList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_split_name);
            tvDesc = itemView.findViewById(R.id.tv_split_desc);
        }
    }
}