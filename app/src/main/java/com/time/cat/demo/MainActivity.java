package com.time.cat.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.time.cat.demo.adapter.ColumnAdapter;
import com.time.cat.demo.data.Entry;
import com.time.cat.demo.data.Item;
import com.time.cat.dragboardview.DragBoardView;
import com.time.cat.dragboardview.model.DragColumn;
import com.time.cat.dragboardview.model.DragItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/4/3
 * @discription 主界面
 * @usage null
 */
public class MainActivity extends AppCompatActivity {

    private ColumnAdapter mAdapter;
    DragBoardView dragBoardView;
    private List<DragColumn> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dragBoardView = findViewById(R.id.layout_main);
        mAdapter = new ColumnAdapter(this);
        mAdapter.setData(mData);
        dragBoardView.setHorizontalAdapter(mAdapter);

        getDataAndRefreshView();
    }

    private void getDataAndRefreshView() {
        for (int i = 0; i < 3; i++) {
            List<DragItem> itemList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                itemList.add(new Item("entry " + i + " item id " + j, "item name " + j, "info " + j));
            }
            mData.add(new Entry("entry id " + i, "name " + i, itemList));
        }
        mAdapter.notifyDataSetChanged();
    }
}

