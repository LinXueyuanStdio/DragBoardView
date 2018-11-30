package com.time.cat.demo.data;

import com.time.cat.dragboardview.model.DragColumn;
import com.time.cat.dragboardview.model.DragItem;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 列表项，每个列表项包含一个子项的List {@link List<Item>}
 * @usage null
 */
public class Entry implements DragColumn {
    private final String id;
    private final String name;
    private final List<DragItem> itemList;

    public Entry(String id, String name, List<DragItem> items) {
        this.id = id;
        this.name = name;
        this.itemList = items;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<DragItem> getItemList() {
        return itemList;
    }

    @Override
    public int getColumnIndex() {
        return 0;
    }

    @Override
    public void setColumnIndex(int columnIndexInHorizontalRecycleView) {

    }
}
