# DragBoardView
可拖拽看板, 只用RecyclerView实现

# gif

<img src="art/device-2018-04-04-085351.gif" width="360" height="645"/>

# 截图

|  |  |  |
|:---:|:---:|:---:|
|![截图1](/art/device-2018-04-04-085942.png)|![截图2](/art/device-2018-04-04-090017.png)|![截图3](/art/device-2018-04-04-090030.png)|
|![截图4](/art/device-2018-04-04-090047.png)|![截图5](/art/device-2018-04-04-090115.png)|

### Just use it as a RecyclerView!

Recyclerview:

Recycler view is same as listview but recyclerview is added in android support lib for material design concept.

Example:

<details>
  <summary>1. Add dependency for recyclerview</summary>

compile 'com.android.support:recyclerview-v7:23.1.0'
Add recyclerview in main layout file
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
    <android.support.v7.widget.RecyclerView
        android:id="@+id/item_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="vertical"
        />
</LinearLayout>
```
</details>

<details>
  <summary>2. Make one item layout xml file</summary>

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:orientation="horizontal"
android:layout_width="match_parent"
android:layout_height="wrap_content">

    <TextView
        android:id="@+id/txtChords"
        android:layout_height="wrap_content"
        android:layout_width="wrap_content"
        android:layout_gravity="center_horizontal"
        />
    <TextView
        android:layout_height="wrap_content"
        android:layout_width="wrap_content"
        android:id="@+id/txtLyrics"/>

</LinearLayout>
```
</details>

<details>
  <summary>3. Make one model class for each item in list. it can be any custom class.</summary>


```java
public class Item {

    private String name;

    public Item(String n) {
        name = n;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
</details>

<details>
  <summary>4. make adapter for recyclerview</summary>

```java
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codexpedia.list.viewholder.R;
import java.util.ArrayList;

public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<Item> itemList;
    // Constructor of the class
    public ItemArrayAdapter(int layoutId, ArrayList<Item> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getName());
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.txtChords);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());
        }
    }
```
</details>


<details>
  <summary>5. bind adapter with recyclerview</summary>

```java
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codexpedia.list.viewholder.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing list view with the custom adapter
        ArrayList <Item> itemList = new ArrayList<Item>();

        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

        // Populating list items
        for(int i=0; i<100; i++) {
            itemList.add(new Item("Item " + i));
        }

    }

}
```

</details>

As for DragBoardView, it's all the same.

<details>
  <summary>1. Add dependency for recyclerview</summary>
```
compile 'com.android.support:recyclerview-v7:23.1.0'
```

Add recyclerview in main layout file

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
    <android.support.v7.widget.RecyclerView
        android:id="@+id/item_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="vertical"
        />
</LinearLayout>
```

</details>

<details>
  <summary>2. Make one item layout xml file</summary>

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:orientation="horizontal"
android:layout_width="match_parent"
android:layout_height="wrap_content">

    <TextView
        android:id="@+id/txtChords"
        android:layout_height="wrap_content"
        android:layout_width="wrap_content"
        android:layout_gravity="center_horizontal"
        />
    <TextView
        android:layout_height="wrap_content"
        android:layout_width="wrap_content"
        android:id="@+id/txtLyrics"/>

</LinearLayout>
```
</details>

<details>
  <summary>3. Make one model class for each item in list. it can be any custom class.</summary>

```java
public class Item {

    private String name;

    public Item(String n) {
        name = n;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
</details>

<details>
  <summary>4. make adapter for recyclerview</summary>

```java
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codexpedia.list.viewholder.R;
import java.util.ArrayList;

public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<Item> itemList;
    // Constructor of the class
    public ItemArrayAdapter(int layoutId, ArrayList<Item> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getName());
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.txtChords);
        }
        @Override
        public void onClick(View view) {
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());
        }
    }
```
</details>


<details>
  <summary>5. bind adapter with recyclerview</summary>

```java
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codexpedia.list.viewholder.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing list view with the custom adapter
        ArrayList <Item> itemList = new ArrayList<Item>();

        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

        // Populating list items
        for(int i=0; i<100; i++) {
            itemList.add(new Item("Item " + i));
        }

    }

}
```

</details>

## How to use

0. Add it in your `root` `build.gradle` at the end of repositories:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency
```gradle
dependencies {
    compile 'com.github.LinXueyuanStdio:DragBoardView:v1.0.0'
}
```

1. declare `DragBoardView` in `xml` file
```xml
<com.time.cat.dragboardview.DragBoardView
    android:id="@+id/layout_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>

```

2. in `Java` files:
```java
dragBoardView = findViewById(R.id.layout_main);
mAdapter = new ColumnAdapter(this);
mAdapter.setData(mData);
dragBoardView.setHorizontalAdapter(mAdapter);
```

## Advance

Meanwhile, PagerRecyclerView is able to be customized by these 3 params:

|name|format|description|
|:---:|:---:|:---:|
| singlePageFling | boolean | single Page Fling, default false |
| triggerOffset | float | trigger offset, default 0.25f |
| flingFactor | float | fling factor, default 0.15f |

demo is more clear

### demo apk
[download](art/app-debug.apk)
