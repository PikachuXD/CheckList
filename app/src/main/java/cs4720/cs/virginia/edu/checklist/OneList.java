package cs4720.cs.virginia.edu.checklist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class OneList extends AppCompatActivity {

    ArrayList<Task> taskList = new ArrayList<Task>();
    TaskAdapter tAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_list);

        ListView listView = (ListView)findViewById(R.id.listView);
        tAdapter = new TaskAdapter(this, R.layout.listitem, taskList);

        listView.setAdapter(tAdapter);
    }

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
        public View getView(int position, View convertView, ViewGroup parent) {
            Task task = taskList.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listitem, null);

                holder = new ViewHolder();
                holder.remove = (Button) convertView.findViewById(R.id.remove_button);
                holder.cBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);

                holder.cBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox c = (CheckBox) v;
                        //Toast.makeText(getApplicationContext(), String.valueOf(c.isChecked()), Toast.LENGTH_LONG).show();
                        Task task = (Task) c.getTag();

                        task.setFinished(c.isChecked());
                        //Toast.makeText(getApplicationContext(), String.valueOf(task.getFinished()), Toast.LENGTH_LONG).show();
                    }
                });

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Button b = (Button) v;
                        Task task = (Task) b.getTag();
                        taskList.remove(task);
                        tAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.menu_one_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItem(View view) {
        EditText editText = (EditText)findViewById(R.id.item);
        taskList.add(new Task(editText.getText().toString()));
        tAdapter.notifyDataSetChanged();
        Log.d("BuildingListView", taskList.toString());
    }

    public void getLoc(View view) {
        Uri geoLocation = null;
        try {
            geoLocation = Uri.parse("geo:38.0316114,-78.5107279?z=11");
        } catch(Exception e) {
            Log.e("Intent Example", "URI Exception");
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        //Intent intent = new Intent(this, MapsActivity.class);
        //startActivity(intent);
    }
}
