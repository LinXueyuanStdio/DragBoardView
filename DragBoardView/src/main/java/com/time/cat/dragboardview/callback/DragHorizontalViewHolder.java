package com.time.cat.dragboardview.callback;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription null
 * @usage null
 */
public interface DragHorizontalViewHolder {
    /**
     * each Horizontal ViewHolder is supposed to have a RecycleView
     * @return RecycleView in ViewHolder which contains vertical items
     */
    RecyclerView getRecyclerView();

    void findViewForContent(View itemView);

    void findViewForFooter(View itemView);
}
