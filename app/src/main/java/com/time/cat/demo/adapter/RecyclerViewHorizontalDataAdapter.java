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
import com.time.cat.dragboardview.callback.DragHorizontalViewHolderCallBack;

import java.util.ArrayList;
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
public class RecyclerViewHorizontalDataAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalDataAdapter.ViewHolder>{

    private Activity mContext;
    private List<Entry> mData;
    private LayoutInflater mInflater;

    private View mFooterView;

    public void setFooterView(View view) {
        mFooterView = view;
        notifyItemInserted(getItemCount() - 1);
    }

    public RecyclerViewHorizontalDataAdapter(Activity context, List<Entry> data) {
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mFooterView != null && viewType == TYPE_FOOTER)
            return new ViewHolder(mFooterView, TYPE_FOOTER);
        View convertView = mInflater.inflate(R.layout.recyclerview_item_entry, parent, false);
        return new ViewHolder(convertView, TYPE_CONTENT);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CONTENT:
                final Entry entry = mData.get(position);
                holder.tv_title.setText(entry.getId());
                final List<Item> itemList = entry.getItemList();
                LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                holder.rv_item.setLayoutManager(layoutManager);
                final RecyclerViewVerticalDataAdapter itemAdapter = new RecyclerViewVerticalDataAdapter(mContext, itemList);
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
                }});
                holder.btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    holder.add_subPlan.setVisibility(View.VISIBLE);
                    holder.edit_sub_plan.setVisibility(View.GONE);
                }});
                holder.btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = holder.editText.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            mData.add(new Entry(
                                    "entry id "+ name,
                                    "name : new entry",
                                    new ArrayList<Item>()));
                            notifyItemInserted(getItemCount() - 1);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mFooterView == null)
            return mData.size();
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView == null)
            return TYPE_CONTENT;
        if (position == getItemCount() - 1)
            return TYPE_FOOTER;
        return TYPE_CONTENT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements DragHorizontalViewHolderCallBack{

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
