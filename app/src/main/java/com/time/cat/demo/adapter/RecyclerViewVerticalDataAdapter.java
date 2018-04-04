package com.time.cat.demo.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.time.cat.demo.R;
import com.time.cat.demo.data.Item;
import com.time.cat.dragboardview.callback.DragActivityCallBack;
import com.time.cat.dragboardview.callback.DragAdapterCallBack;

import java.util.Collections;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 垂直排列的子项卡片
 * @usage null
 */
public class RecyclerViewVerticalDataAdapter extends RecyclerView.Adapter<RecyclerViewVerticalDataAdapter.ViewHolder> implements DragAdapterCallBack {

    private Activity mContext;
    private List<Item> mData;
    private LayoutInflater mInflater;

    private int mDragPosition;//正在拖动的 View 的 position
    private boolean mHideDragItem; // 是否隐藏正在拖动的 position

    public RecyclerViewVerticalDataAdapter(Activity context, List<Item> data) {
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = mInflater.inflate(R.layout.recyclerview_item_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = mData.get(holder.getAdapterPosition());
        holder.item_title.setText(item.getItemName());

        if (position == mDragPosition && mHideDragItem) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.setTag(item);
                ((DragActivityCallBack)mContext).getDragHelper().drag(v, position);
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                Toast.makeText(mContext, "go to Item detail page,\nposition : "+ position
                        +"\n plz replace this with your action.", Toast.LENGTH_SHORT).show();
            }
        });
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
    public void onDrop(int page, int position, Object tag) {
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
    public void onDragIn(int position, Object tag) {
        Item task = (Item) tag;
        if (position > mData.size()) {// 如果拖进来时候的 position 比当前 列表的长度大，就添加到列表末端
            position = mData.size();
        }
        mData.add(position, task);
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


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_title;

        public ViewHolder(View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
        }
    }
}
