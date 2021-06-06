package com.time.cat.demo.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.time.cat.demo.R;
import com.time.cat.demo.data.Item;
import com.time.cat.dragboardview.adapter.VerticalAdapter;
import com.time.cat.dragboardview.helper.DragHelper;
import com.time.cat.dragboardview.model.DragItem;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 垂直排列的子项卡片
 * @usage null
 */
public class ItemAdapter extends VerticalAdapter<ItemAdapter.ViewHolder> {

    public ItemAdapter(Context context, DragHelper dragHelper) {
        super(context, dragHelper);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(Context context, final ViewHolder holder, @NonNull DragItem item, final int position) {
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dragItem(holder);
                return true;
            }
        });
        holder.item_title.setText(((Item) item).getItemName());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_title;

        public ViewHolder(View itemView) {
            super(itemView);
            item_title = itemView.findViewById(R.id.item_title);
        }
    }
}
