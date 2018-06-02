package com.time.cat.demo.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.time.cat.demo.R;
import com.time.cat.demo.data.Entry;
import com.time.cat.demo.data.Item;
import com.time.cat.dragboardview.callback.DragActivityCallBack;
import com.time.cat.dragboardview.callback.DragHorizontalAdapterCallBack;
import com.time.cat.dragboardview.callback.DragHorizontalViewHolderCallBack;

import java.util.ArrayList;
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
public class RecyclerViewHorizontalDataAdapter
        extends RecyclerView.Adapter<RecyclerViewHorizontalDataAdapter.ViewHolder>
        implements DragHorizontalAdapterCallBack{

    private Activity mContext;
    private List<Entry> mData;
    private LayoutInflater mInflater;

    private View mFooterView;

    private int mDragPosition;//正在拖动的 View 的 position
    private boolean mHideDragItem; // 是否隐藏正在拖动的 position

    public RecyclerViewHorizontalDataAdapter(Activity context, List<Entry> data) {
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    public void setFooterView(View view) {
        mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            return new ViewHolder(mFooterView, TYPE_FOOTER);
        }
        View convertView = mInflater.inflate(R.layout.recyclerview_item_entry, parent, false);
        return new ViewHolder(convertView, TYPE_CONTENT);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                final Entry entry = mData.get(position);

                if (position == mDragPosition && mHideDragItem) {
                    holder.itemView.setVisibility(View.INVISIBLE);
                    return;
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                }

                holder.tv_title.setText(entry.getId());
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        holder.itemView.setTag(entry);
                        ((DragActivityCallBack) mContext).getDragHelper()
                                .dragCol(holder.itemView, holder.getAdapterPosition());
                        return true;
                    }
                });
                final List<Item> itemList = entry.getItemList();
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                holder.rv_item.setLayoutManager(layoutManager);
                final RecyclerViewVerticalDataVerticalAdapter itemAdapter = new RecyclerViewVerticalDataVerticalAdapter(mContext, itemList);
                holder.rv_item.setAdapter(itemAdapter);
                holder.add_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do something
                        // plz replace below with your action
                        new MaterialDialog.Builder(mContext)
                                .content("添加一个卡片")
                                .positiveText("添加")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        // add new Item instantly
                                        itemList.add(new Item(
                                                "entry " + " item id ",
                                                "item name : new Item",
                                                "info : new Item"));
                                        itemAdapter.notifyItemInserted(itemAdapter.getItemCount() - 1);
                                        // then add new Item to your database in io thread
                                        // then view will auto-refresh
                                    }
                                })
                                .negativeText("取消")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        // plz replace above with your action
                    }
                });
                break;
            case TYPE_FOOTER:
                holder.add_subPlan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.add_subPlan.setVisibility(View.GONE);
                        holder.edit_sub_plan.setVisibility(View.VISIBLE);
                    }
                });
                holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.add_subPlan.setVisibility(View.VISIBLE);
                        holder.edit_sub_plan.setVisibility(View.GONE);
                    }
                });
                holder.btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = holder.editText.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            mData.add(new Entry(
                                    "entry id " + name,
                                    "name : new entry",
                                    new ArrayList<Item>()));
                            notifyItemInserted(getItemCount() - 1);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mFooterView == null) {return mData.size();}
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null) {return TYPE_CONTENT;}
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
    public void onDrop(int page, int position, Object tag) {
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
    public void onDragIn(int position, Object ColumnObject) {
        Entry entry = (Entry) ColumnObject;
        if (position > mData.size()) {// 如果拖进来时候的 position 比当前 列表的长度大，就添加到列表末端
            position = mData.size();
        }
        mData.add(position, entry);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements DragHorizontalViewHolderCallBack {

        RelativeLayout col_content_container;
        ImageView title_icon;
        TextView tv_title;
        RecyclerView rv_item;
        RelativeLayout add_task;

        RelativeLayout add_subPlan;
        RelativeLayout edit_sub_plan;
        Button btn_cancel;
        Button btn_ok;
        EditText editText;

        public ViewHolder(View convertView, int itemType) {
            super(convertView);
            if (itemType == TYPE_CONTENT) {
                col_content_container = convertView.findViewById(R.id.col_content_container);
                title_icon = convertView.findViewById(R.id.title_icon);
                tv_title = convertView.findViewById(R.id.tv_title);
                rv_item = convertView.findViewById(R.id.rv);
                add_task = convertView.findViewById(R.id.add);
            } else {
                add_subPlan = convertView.findViewById(R.id.add_sub_plan);
                edit_sub_plan = convertView.findViewById(R.id.edit_sub_plan);
                btn_cancel = convertView.findViewById(R.id.add_cancel);
                btn_ok = convertView.findViewById(R.id.add_ok);
                editText = convertView.findViewById(R.id.add_et);
            }
        }

        @Override
        public RecyclerView getRecyclerView() {
            return rv_item;
        }
    }
}
