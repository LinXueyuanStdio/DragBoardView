package com.time.cat.demo.data;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 子项
 * @usage null
 */
public class Item {
    private final String itemId;
    private final String itemName;
    private final String info;

    public Item(String itemId, String itemName, String info) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.info = info;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getInfo() {
        return info;
    }
}


