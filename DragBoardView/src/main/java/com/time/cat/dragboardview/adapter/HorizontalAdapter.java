package com.time.cat.dragboardview.adapter;

import android.content.Context;
import androidx.annotation.IntRange;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.time.cat.dragboardview.callback.DragHorizontalAdapter;
import com.time.cat.dragboardview.callback.DragHorizontalViewHolder;
import com.time.cat.dragboardview.helper.DragHelper;
import com.time.cat.dragboardview.model.DragColumn;

import java.util.Collections;
import java.util.List;

import static com.time.cat.dragboardview.helper.DragHelper.TYPE_CONTENT;
import static com.time.cat.dragboardview.helper.DragHelper.TYPE_FOOTER;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 水平排列的列表
 * @usage null
 */
public abstract class HorizontalAdapter<VH extends HorizontalAdapter.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements DragHorizontalAdapter {

    protected Context mContext;
    private List<DragColumn> mData;
    protected DragHelper dragHelper;

    private int mDragPosition;//正在拖动的 View 的 position
    private boolean mHideDragItem; // 是否隐藏正在拖动的 position

    public HorizontalAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (needFooter() && viewType == TYPE_FOOTER) {
            View convertView = LayoutInflater.from(mContext)
                                             .inflate(getFooterLayoutRes(), parent, false);
            return onCreateViewHolder(convertView, TYPE_FOOTER);
        }
        View convertView = LayoutInflater.from(mContext)
                                         .inflate(getContentLayoutRes(), parent, false);
        return onCreateViewHolder(convertView, TYPE_CONTENT);
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                final DragColumn dragColumn = mData.get(position);
                if (position == mDragPosition && mHideDragItem) {
                    holder.itemView.setVisibility(View.INVISIBLE);
                    return;
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                }
                holder.itemView.setTag(dragColumn);

                onBindContentViewHolder(holder, dragColumn, position);
                break;
            case TYPE_FOOTER:
                onBindFooterViewHolder(holder, position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (!needFooter()) {return mData.size();}
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (!needFooter()) {return TYPE_CONTENT;}
        if (position == getItemCount() - 1) {return TYPE_FOOTER;}
        return TYPE_CONTENT;
    }

    @Override
    public void onDrag(int position) {
        mDragPosition = position;
        mHideDragItem = true;
        notifyItemChanged(position);
    }

    @Override
    public void onDrop(int page, int position, DragColumn tag) {
        mHideDragItem = false;
        //        notifyItemChanged(position);
        notifyDataSetChanged();
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
    public void onDragIn(int position, DragColumn dragColumnObject) {
        if (position > mData.size()) {// 如果拖进来时候的 position 比当前 列表的长度大，就添加到列表末端
            position = mData.size();
        }
        mData.add(position, dragColumnObject);
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

    public List<DragColumn> getData() {
        return mData;
    }

    public void setData(List<DragColumn> mData) {
        this.mData = mData;
    }

    public void appendNewColumn(DragColumn dragColumn) {
        getData().add(dragColumn);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addNewColumn(@IntRange(from = 0) int position, DragColumn dragColumn) {
        getData().add(position, dragColumn);
        notifyItemInserted(position);
    }

    public void setDragHelper(DragHelper dragHelper) {
        this.dragHelper = dragHelper;
    }

    public void dragCol(View columnView, int position) {
        if (dragHelper != null) {
            dragHelper.dragCol(columnView, position);
        }
    }

    public void dragCol(VH holder) {
        dragCol(holder.itemView, holder.getAdapterPosition());
    }

    public abstract int getContentLayoutRes();

    public abstract int getFooterLayoutRes();

    public abstract VH onCreateViewHolder(View parent, int viewType);

    public abstract void onBindContentViewHolder(final VH holder, DragColumn dragColumn, int position);

    public abstract void onBindFooterViewHolder(final VH holder, int position);

    public abstract boolean needFooter();

    public abstract class ViewHolder extends RecyclerView.ViewHolder implements DragHorizontalViewHolder {

        public ViewHolder(View convertView, int itemType) {
            super(convertView);
            if (itemType == TYPE_CONTENT) {
                findViewForContent(convertView);
            } else {
                findViewForFooter(convertView);
            }
        }

        @Override
        public abstract RecyclerView getRecyclerView();

        @Override
        public abstract void findViewForContent(View convertView);

        @Override
        public abstract void findViewForFooter(View convertView);
    }
}
