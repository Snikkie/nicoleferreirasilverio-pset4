package com.example.nicole.nicoleferreirasilverio_pset4;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Find ListView to populate
        lvItems = (ListView) findViewById(R.id.todo_list);
        showList();
    }

    public class TodoCursorAdapter extends CursorAdapter {

        public TodoCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find fields to populate in inflated template
            TextView tvElement = (TextView) view.findViewById(R.id.todo_element);
            // Extract properties from cursor
            String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
            // Populate fields with extracted properties
            tvElement.setText(task);
        }
    }

    public void AddToList(View view){

        DBhelper dbHelper = new DBhelper(this);

        EditText todo_input = (EditText) findViewById(R.id.editText);
        String task = todo_input.getText().toString();

        TodoElement ToDo = new TodoElement();
        ToDo.setTask(task);

        if (task.length() != 0) {
            dbHelper.create(ToDo);
            dbHelper.close();
            todo_input.setText("");
        }
        else{
            Toast.makeText(this, "Please enter a task!", Toast.LENGTH_LONG).show();
        }
        showList();
    }


    public void showList() {
        final DBhelper dbHelper = new DBhelper(this);
        final Cursor cursor = dbHelper.read();

        // Setup cursor adapter using cursor from last step
        TodoCursorAdapter todoAdapter = new TodoCursorAdapter(this, cursor);
        // Attach cursor adapter to the ListView
        lvItems.setAdapter(todoAdapter);


        dbHelper.close();

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor taskcursor = (Cursor) lvItems.getItemAtPosition(position);
                String task_element = taskcursor.getString(taskcursor.getColumnIndexOrThrow("task"));
                int taskID = taskcursor.getInt(taskcursor.getColumnIndexOrThrow("_id"));
                String done_task = "DONE: " + task_element;
                dbHelper.update(done_task, taskID);
                dbHelper.close();
                showList();
            }
        });

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                Cursor deleteCursor = (Cursor) lvItems.getItemAtPosition(position);
                int deletecursorID = deleteCursor.getInt(deleteCursor.getColumnIndexOrThrow("_id"));
                dbHelper.delete(deletecursorID);
                showList();
                return true;
            }
        });
    }
}


