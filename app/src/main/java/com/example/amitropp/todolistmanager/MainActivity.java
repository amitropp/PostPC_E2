package com.example.amitropp.todolistmanager;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.ArrayList;
import 	java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // true = red, false = blue
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();

        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);

                if ((position % 2) == 0) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(Color.BLUE);
                }
                return tv;
            }
        };
        
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
                        final int index = pos;
                        CharSequence cs = "Delete";
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(items.get(pos).toString()) //
                                .setPositiveButton(cs, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Remove the item within array at position
                                        items.remove(index);
                                        // Refresh the adapter
                                        itemsAdapter.notifyDataSetChanged();
                                        writeItems();
                                        // Return true consumes the long click event (marks it handled)
                                        dialog.dismiss();
                                    }
                                });

                        builder.show();
                        return true;
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

