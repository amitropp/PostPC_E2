package com.example.amitropp.todolistmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.ArrayList;
import 	java.io.IOException;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private boolean color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // true = red, false = blue
        color = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        // Setup remove listener method call
        setupListViewListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        items.remove(pos);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adpterView, View view, int position,
                                            long id) {
                        if (color == true) {
                            lvItems.getChildAt(itemsAdapter.getCount() - 1).setBackgroundColor(Color.RED);
                            color = false;
                        } else {
                            lvItems.getChildAt(itemsAdapter.getCount() - 1).setBackgroundColor(Color.BLUE);
                            color = false;
                        }
                    }
                });
    }


    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.enterNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
        scrollMyListViewToBottom();
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scrollMyListViewToBottom() {
        lvItems.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvItems.setSelection(itemsAdapter.getCount() - 1);
            }
        });
    }
}

