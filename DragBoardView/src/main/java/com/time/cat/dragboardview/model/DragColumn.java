package com.time.cat.dragboardview.model;

import androidx.annotation.IntRange;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/11/30
 * @discription null
 * @usage null
 */
public interface DragColumn {
    List<? extends DragItem> getItemList();

    @IntRange(from = 0)
    int getColumnIndex();

    void setColumnIndex(@IntRange(from = 0) int columnIndexInHorizontalRecycleView);
}
