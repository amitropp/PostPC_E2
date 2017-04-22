package com.example.amitropp.todolistmanager;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Event {

    private String title;
    private Date date;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Event(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object obj) {
        Event it = (Event) obj;
        boolean titleEquals = title.equals(it.getTitle());
        boolean dateEquals;
        if (date == null)
        {
            if (it.getDate() == null)
            {
                dateEquals = true;
            }
            else
            {
                dateEquals = false;
            }
        }
        else {
            dateEquals = date.equals(it.getDate());
        }

        return (titleEquals && dateEquals);
    }



}
