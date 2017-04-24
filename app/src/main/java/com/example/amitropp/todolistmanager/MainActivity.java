package com.example.amitropp.todolistmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.ArrayList;
import 	java.io.IOException;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    //needed variables
    private static final String TAB_NAME = "missions";
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(TAB_NAME);
        addDBListenter(mDatabase);

        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();

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

        final Button btn = new Button(MainActivity.this);
        btn.setHint("Call");
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        final int index = pos;
                        CharSequence cs = "Delete Item";
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(items.get(pos).toString()) //
                                .setPositiveButton(cs, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Remove the item within array at position
                                        final String itemToRemoved = items.get(index);
                                        Log.d("firebaseLog", "~~~here");
                                        removeItemFromFirebase(itemToRemoved);

                                    }
                                });
                                if ((items.get(pos).toString()).toLowerCase().contains("call")){
                                    final String number = (((items.get(pos).toString()).split("call")[1]).split(",")[0]).trim();
                                    Log.d("Number: ", number);
                                    builder.setView(btn);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + number));
                                            startActivity(intent);
                                        }
                                    });
                                }

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


        builder.setTitle("Add new Item")
                .setMessage(msg)
                .setPositiveButton(add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // add the item to the cloud
                        Log.d("firebaseLog", "~~~here1");
                        String itemText = input.getText().toString() + ",\t" + date.getText().toString();
                        mDatabase.push().setValue(itemText);

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

    private void addDBListenter(DatabaseReference newRef)
    {
        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String newItem = dataSnapshot.getValue(String.class);
                if (newItem != null)
                    Log.d("tagChildAdded", "Value is: " + newItem.split(",")[0]);
                addItemToScreen(newItem);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String item = dataSnapshot.getValue(String.class);
                if (item != null)
                    Log.d("tagChildRemoved", "Value is: " + item.split(",")[0]);
                removeItemFromScreen(item);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tagDataCancelled", "Failed to read value.", error.toException());
            }
        });

    }

    public void addItemToScreen(String newItem) {

        // Refresh the adapter

        itemsAdapter.notifyDataSetChanged();
        itemsAdapter.add(newItem);
        writeItems();
        //scroll to the bottom of the list
        scrollMyListViewToBottom();

    }

    public void removeItemFromScreen(String item){

        items.remove(getIndex(item));
        // Refresh the adapter
        itemsAdapter.notifyDataSetChanged();


    }
    private int getIndex(String item){
        for (int pos = 0; pos < items.size(); pos++){
            String it = items.get(pos);
            if (it.equals(item)){
                return pos;
            }
        }
        return -1;
    }

    //remove the tdlItem given as an argument from the database of firebase
    public void removeItemFromFirebase(final String itemToBeRemoved) {

        final Query query = mDatabase.orderByValue();
        //print item body
        Log.d("logTaskFound", itemToBeRemoved.split(",")[0]);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot task : dataSnapshot.getChildren())
                {
                    String item = task.getValue(String.class);
                    Log.d("logTaskFound", item.split(",")[0]);
                    if (item.equals(itemToBeRemoved))
                    {
                        task.getRef().removeValue();
                        query.removeEventListener(this);
                        break;
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

