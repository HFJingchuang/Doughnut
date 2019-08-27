package com.doughnut.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private ItemClickListener mOnItemClickListener;
    private ItemLongClickListener mOnItemLongClickListener;


    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public BaseRecyclerViewHolder(View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        this.mOnItemClickListener = itemClickListener;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        });
    }

    public BaseRecyclerViewHolder(View itemView, ItemLongClickListener itemLongClickListener) {
        super(itemView);
        this.mOnItemLongClickListener = itemLongClickListener;
        /****2019-08-27 增长安按钮事件*/
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemLongClickListener.onItemLongClick(v,getAdapterPosition());
                return false;
            }
        });
    }

    public BaseRecyclerViewHolder(View itemView, ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener) {
        super(itemView);
        this.mOnItemLongClickListener = itemLongClickListener;
    }
}
