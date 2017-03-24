package com.example.amitropp.todolistmanager;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import 	java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    //needed variables
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();
        //adapter with the text color condition (odd-red, even-blue)
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
        //on long click - open dialog with delete button
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
                                        dialog.dismiss();
                                    }
                                });

                        builder.show();
                        return true;
                    }


                });
    }


    public void onButtonClick(Button view) {
        CharSequence can = "Cancel";
        CharSequence add = "Add";
        CharSequence msg = "Insert item and date of reminder";

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        final EditText date = new EditText(MainActivity.this);



        input.setHint("Title");
        layout.addView(input);

        date.setHint("Date");
        layout.addView(date);

        builder.setView(layout);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDatePickerDialog((EditText) view);
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker =new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        date.setText("" + selectedday + "/" + (selectedmonth+1) + "/" + selectedyear);
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }

        });


        builder.setTitle("Add new Item") //
                .setMessage(msg)
                .setPositiveButton(add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Remove the item within array at position
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        String itemText = input.getText().toString() + ",\t" + date.getText().toString();
                        itemsAdapter.add(itemText);
                        writeItems();
                        scrollMyListViewToBottom();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(can, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

        builder.show();


    }

    public void onAddItem(View v) {
        Button addBtn = (Button) findViewById(R.id.btnAddItem);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick((Button) view);
            }

        });


        /*
        EditText etNewItem = (EditText) findViewById(R.id.enterNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
        scrollMyListViewToBottom();
        */
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

