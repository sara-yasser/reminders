package com.example.todolist;

//package com.mine.trial1;

/**
 * Created by engMa_000 on 2017-04-03.
 */

public class Reminder {


    private int mId;
    private String mContent;
    private int mImportant;
    public Reminder(int id, String content, int important) {
        mId = id;
        mImportant = important;
        mContent = content;
    }
    public int getId() {
        return mId;
    }
    public void setId(int id) {
        mId = id;
    }
    public int getImportant() {
        return mImportant;
    }
    public void setImportant(int important) {
        mImportant = important;
    }
    public String getContent() {
        return mContent;
    }
    public void setContent(String content) {
        mContent = content;
    }

}
