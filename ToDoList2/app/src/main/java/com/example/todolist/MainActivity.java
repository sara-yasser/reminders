package com.example.todolist;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.todolist.R.*;


public class MainActivity extends AppCompatActivity {
    ListView listView ;
    RemindersDbAdapter mDbAdapter;
    RemindersSimpleCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_main);
      //  final Toolbar toolbar =  findViewById(id.toolbar);
        //create delete button for multi delete items
      //  final Button multiDelete = findViewById(id.multi_delete);
        // hide it
     //   multiDelete.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.mobile_list);
        listView.setDivider(null);
        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();

        Cursor cursor = mDbAdapter.fetchAllReminders();

        final String[] from = new String[]{RemindersDbAdapter.COL_CONTENT};
        int[] to = new int[]{R.id.test};

        mCursorAdapter = new RemindersSimpleCursorAdapter(MainActivity.this,layout.list_view_layout,cursor,from,to,0);
        listView.setAdapter(mCursorAdapter);

//------------here is a click item listener ---------------
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder mBilder2 = new AlertDialog.Builder(MainActivity.this);
                final View mView2 = getLayoutInflater().inflate(layout.choos_option, null);
                TextView edit = (TextView) mView2.findViewById(R.id.edit);
                TextView delete = (TextView) mView2.findViewById(R.id.delete);

                mBilder2.setView(mView2);
                final AlertDialog dialogCreater2 = mBilder2.create();
                dialogCreater2.show();
///////////---------------------------------  EDIT  ---------------------------------------------------------
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int reminderId = (int)mCursorAdapter.getItemId(position);
                        final Reminder reminder = mDbAdapter.fetchReminderById(reminderId);
                        final AlertDialog.Builder mBilder = new AlertDialog.Builder(MainActivity.this);
                        final View mView = getLayoutInflater().inflate(layout.custom_dialog, null);

                        Button cancleBtn = (Button) mView.findViewById(R.id.cancleBtn);
                        Button commitBtn = (Button) mView.findViewById(R.id.commitBtn);

                        final CheckBox important = (CheckBox) mView.findViewById(R.id.important);
                        final EditText multiLineText = (EditText) mView.findViewById(R.id.editText2);
                        TextView title = (TextView) mView.findViewById(R.id.newReminder);
                        ConstraintLayout container = (ConstraintLayout) mView.findViewById(R.id.container);
                        title.setText("Edit Reminder");

                        important.setChecked(reminder.getImportant() == 1);
                        multiLineText.setText(reminder.getContent());
                        container.setBackgroundColor(Color.argb(225,225,240,141));

                        mBilder.setView(mView);
                        final AlertDialog dialogCreater = mBilder.create();
                        dialogCreater.show();
                        dialogCreater2.dismiss();

                        cancleBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCreater.dismiss();
                                Toast.makeText(MainActivity.this, "canceled", Toast.LENGTH_SHORT).show();
                            }
                        });

                        commitBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Reminder editedReminder = new Reminder(reminder.getId(),multiLineText.getText().toString(), important.isChecked() ? 1 : 0);
                                mDbAdapter.updateReminder(editedReminder);
                                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                                Toast.makeText(MainActivity.this, "Edited", Toast.LENGTH_SHORT).show();
                                dialogCreater.dismiss();
                            }
                        });
                    }

                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDbAdapter.deleteReminderById((int)mCursorAdapter.getItemId(position));
                        mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        dialogCreater2.dismiss();
                    }

                    });
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) { }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.delete_multi_select_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }


                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete_multi_reminders:
                            for (int count = mCursorAdapter.getCount() - 1; count >= 0; count--) {
                                if (listView.isItemChecked(count)) {
                                    mDbAdapter.deleteReminderById((int)mCursorAdapter.getItemId(count));
                                }
                            }
                            mode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                            return true;
                    }
                    return false;
                }
                @Override
                public void onDestroyActionMode(ActionMode mode) { }
            });
        }

      //  setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_tab_menu, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case id.clear:
                Toast.makeText(this, "clear all....", Toast.LENGTH_SHORT).show();
                mDbAdapter.deleteAllReminders();
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                return true;
            case id.reminder:

                final AlertDialog.Builder mBilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(layout.custom_dialog, null);

                Button cancleBtn = (Button) mView.findViewById(id.cancleBtn);
                Button commitBtn = (Button) mView.findViewById(id.commitBtn);
                final CheckBox important = (CheckBox) mView.findViewById(id.important);
                final EditText multiLineText = (EditText) mView.findViewById(id.editText2);

                mBilder.setView(mView);
                final AlertDialog dialogCreater = mBilder.create();
                dialogCreater.show();

                cancleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogCreater.dismiss();
                        Toast.makeText(MainActivity.this, "canceled", Toast.LENGTH_SHORT).show();
                    }
                });

                commitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDbAdapter.createReminder(multiLineText.getText().toString(), important.isChecked());
                        mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        Toast.makeText(MainActivity.this, "added new reminder", Toast.LENGTH_SHORT).show();
                        dialogCreater.dismiss();

                    }
                });

                return true;

            case id.exit:
                Toast.makeText(this, "exiting....", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


