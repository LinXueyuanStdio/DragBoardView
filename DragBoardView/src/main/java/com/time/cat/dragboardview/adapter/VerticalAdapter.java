package com.time.cat.dragboardview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.time.cat.dragboardview.callback.DragVerticalAdapter;
import com.time.cat.dragboardview.helper.DragHelper;
import com.time.cat.dragboardview.model.DragItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 垂直排列的子项卡片
 * @usage null
 */
public abstract class VerticalAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements DragVerticalAdapter {

    protected Context mContext;
    private List<DragItem> mData;
    @NonNull
    private DragHelper dragHelper;

    private int mDragPosition;//正在拖动的 View 的 position
    private boolean mHideDragItem; // 是否隐藏正在拖动的 position

    public VerticalAdapter(Context context, @NonNull DragHelper dragHelper) {
        this.mContext = context;
        this.mData = new ArrayList<>();
        this.dragHelper = dragHelper;
    }

    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        if (position == mDragPosition && mHideDragItem) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        final DragItem item = mData.get(holder.getAdapterPosition());
        holder.itemView.setTag(item);

        onBindViewHolder(mContext, holder, item, holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onDrag(int position) {
        mDragPosition = position;
        mHideDragItem = true;
        notifyItemChanged(position);
    }

    @Override
    public void onDrop(int page, int position, DragItem tag) {
        mHideDragItem = false;
        notifyItemChanged(position);
    }

    @Override
    public void onDragOut() {
        if (mDragPosition >= 0 && mDragPosition < mData.size()) {
            mData.remove(mDragPosition);
            notifyDataSetChanged();// 此处如果用 notifyItemRemove 下一次选定时的 position 是错的
            mDragPosition = -1;
        }
    }

    @Override
    public void onDragIn(int position, DragItem item) {
        if (position > mData.size()) {// 如果拖进来时候的 position 比当前 列表的长度大，就添加到列表末端
            position = mData.size();
        }
        mData.add(position, item);
        notifyItemInserted(position);
        mDragPosition = position;
        mHideDragItem = true;
    }

    @Override
    public void updateDragItemVisibility(int position) {
        if (mDragPosition >= 0 && mDragPosition < mData.size() && position < mData.size() && mDragPosition != position) {
            if (Math.abs(mDragPosition - position) == 1) {
                notifyItemChanged(mDragPosition);
                Collections.swap(mData, mDragPosition, position);
                mDragPosition = position;
                notifyItemChanged(position);
            } else {
                notifyItemChanged(mDragPosition);
                if (mDragPosition > position) {
                    for (int i = mDragPosition; i > position; i--) {
                        Collections.swap(mData, i, i - 1);
                        notifyItemChanged(i);
                    }
                } else {
                    for (int i = mDragPosition; i < position; i++) {
                        Collections.swap(mData, i, i + 1);
                        notifyItemChanged(i);
                    }
                }
                mDragPosition = position;
                notifyItemChanged(position);
            }
        }
    }

    public void dragItem(View columnView, int position) {
        dragHelper.dragItem(columnView, position);
    }

    public void dragItem(VH holder) {
        dragItem(holder.itemView, holder.getAdapterPosition());
    }

    public void setDragHelper(DragHelper dragHelper) {
        this.dragHelper = dragHelper;
    }

    public abstract void onBindViewHolder(Context context, VH holder, @NonNull DragItem item, final int position);

    public void setData(List<DragItem> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public List<DragItem> getData() {
        return mData;
    }
}
