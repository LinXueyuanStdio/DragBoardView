package com.time.cat.demo.data;

import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 列表项，每个列表项包含一个子项的List {@link List< Item >}
 * @usage null
 */
public class Entry {
    private final String id;
    private final String name;
    private final List<Item> itemList;

    public Entry(String id, String name, List<Item> items) {
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

    public List<Item> getItemList() {
        return itemList;
    }
}
