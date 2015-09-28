package cs4720.cs.virginia.edu.checklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joltbolt on 9/20/2015.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    private ArrayList<Task> taskList;
    public TaskAdapter(Context context, int textViewResourceId, ArrayList<Task> tList) {
        super(context, textViewResourceId, tList);
        this.taskList = tList;
        this.taskList.addAll(tList);

    }

    private class ViewHolder {
        Button edit;
        TextView datetime;
        CheckBox cBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Task task = taskList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.listitem, null);


            holder = new ViewHolder();
            holder.edit = (Button) convertView.findViewById(R.id.edit_button);
            holder.datetime = (TextView) convertView.findViewById(R.id.datetimeoftask);
            holder.cBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);

            holder.cBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox c = (CheckBox) v;
                    //Toast.makeText(getApplicationContext(), String.valueOf(c.isChecked()), Toast.LENGTH_LONG).show();
                    Task task = (Task) c.getTag();
                    task.setChecked(c.isChecked());
                    //Toast.makeText(getApplicationContext(), String.valueOf(task.getFinished()), Toast.LENGTH_LONG).show();
                }
            });

            holder.edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), OneTaskActivity.class);
                    Button b = (Button) v;
                    Task task = (Task) b.getTag();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("current", task);
                    intent.putExtras(bundle);
                    ((Activity) getContext()).startActivityForResult(intent, 0);
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cBox.setTag(task);
        holder.edit.setTag(task);
        holder.cBox.setText(task.getName());
        if (task.getDueTime().equals("") || task.getDueDate().equals("")) {
            holder.datetime.setText("");
        } else {
            holder.datetime.setText(task.getDueDate() + "@" + task.getDueTime());
        }
        holder.cBox.setChecked(task.getChecked());

        return convertView;
    }
}
