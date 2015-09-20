package cs4720.cs.virginia.edu.checklist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Rock Beom Kim rk5dy
 * Peter Bahng pb5te
 */
public class OneList_Activity extends AppCompatActivity {

    ArrayList<Task> taskList = new ArrayList<Task>();
    ArrayList<Task> completedList = new ArrayList<Task>();
    TaskAdapter tAdapter = null;
    CompleteAdapter cAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_list);

        ListView listView = (ListView)findViewById(R.id.listView);
        ListView completeListView = (ListView) findViewById(R.id.complist_view);
        tAdapter = new TaskAdapter(this, R.layout.listitem, taskList);
        cAdapter = new CompleteAdapter(this, R.layout.listitem, completedList);

        listView.setAdapter(tAdapter);
        completeListView.setAdapter(cAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //adapter used for the tasks to be completed
    private class TaskAdapter extends ArrayAdapter<Task> {
        private ArrayList<Task> taskList;
        public TaskAdapter(Context context, int textViewResourceId, ArrayList<Task> tList) {
            super(context, textViewResourceId, tList);
            this.taskList = tList;
            this.taskList.addAll(tList);

        }

        private class ViewHolder {
            Button remove;
            CheckBox cBox;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Task task = taskList.get(position);
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listitem, null);

                holder = new ViewHolder();
                holder.remove = (Button) convertView.findViewById(R.id.edit_button);
                holder.cBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);

                holder.cBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox c = (CheckBox) v;
                        //Toast.makeText(getApplicationContext(), String.valueOf(c.isChecked()), Toast.LENGTH_LONG).show();
                        Task task = (Task) c.getTag();
                        task.setFinished(c.isChecked());
                        taskList.remove(task);
                        tAdapter.notifyDataSetChanged();
                        completedList.add(task);
                        cAdapter.notifyDataSetChanged();
                        //Toast.makeText(getApplicationContext(), String.valueOf(task.getFinished()), Toast.LENGTH_LONG).show();
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), OneTask_Activity.class);
                        Button b = (Button) v;
                        Task task = (Task) b.getTag();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("current", task);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.cBox.setTag(task);
            holder.remove.setTag(task);
            holder.cBox.setText(task.getName());
            holder.cBox.setChecked(task.getFinished());

            return convertView;
        }
    }

    //separate adapter used for the tasks that are completed
    private class CompleteAdapter extends ArrayAdapter<Task> {
        private ArrayList<Task> completedList;
        public CompleteAdapter(Context context, int textViewResourceId, ArrayList<Task> tList) {
            super(context, textViewResourceId, tList);
            this.completedList = tList;
            this.completedList.addAll(tList);

        }

        private class ViewHolder {
            Button remove;
            CheckBox cBox;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Task task = completedList.get(position);
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listitem, null);

                holder = new ViewHolder();
                holder.remove = (Button) convertView.findViewById(R.id.edit_button);
                holder.cBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);

                holder.cBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox c = (CheckBox) v;
                        Task task = (Task) c.getTag();
                        task.setFinished(c.isChecked());
                        completedList.remove(task);
                        cAdapter.notifyDataSetChanged();
                        taskList.add(task);
                        tAdapter.notifyDataSetChanged();
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), OneTask_Activity.class);
                        Button b = (Button) v;
                        Task task = (Task) b.getTag();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("current", task);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.cBox.setTag(task);
            holder.remove.setTag(task);
            holder.cBox.setText(task.getName());
            holder.cBox.setChecked(task.getFinished());

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //saves instance state so the list isn't destroyed upon calling the one task activity
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("tList", taskList);
        savedInstanceState.putParcelableArrayList("cList", completedList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.taskList = savedInstanceState.getParcelableArrayList("cList");
        this.taskList = savedInstanceState.getParcelableArrayList("tList");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_add_task:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add a task");
                builder.setMessage("What do you want to do?");
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Task tmp = new Task(inputField.getText().toString());
                        taskList.add(tmp);
                        tAdapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Cancel",null);

                builder.create().show();
                return true;

            default:
                return false;
        }
    }

}
