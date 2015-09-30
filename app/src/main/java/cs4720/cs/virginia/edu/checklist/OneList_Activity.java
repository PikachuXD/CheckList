package cs4720.cs.virginia.edu.checklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Rock Beom Kim rk5dy
 * Peter Bahng pb5te
 */
public class OneList_Activity extends AppCompatActivity {

    ArrayList<Task> taskList = new ArrayList<Task>();
    ArrayList<Task> completedList = new ArrayList<Task>();
    TaskAdapter tAdapter = null;
    TaskAdapter cAdapter = null;
    Task passed;
    Task original;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_list);

        ListView listView = (ListView)findViewById(R.id.listView);
        ListView completeListView = (ListView) findViewById(R.id.complist_view);
        tAdapter = new TaskAdapter(this, R.layout.listitem, taskList);
        cAdapter = new TaskAdapter(this, R.layout.listitem, completedList);

        listView.setAdapter(tAdapter);
        completeListView.setAdapter(cAdapter);

        String FILENAME = "oneliststore.txt";
        try {
            FileInputStream fis = openFileInput(FILENAME);
            StringBuilder builder = new StringBuilder();
            int ch;
            while ((ch = fis.read()) != -1) {
                builder.append((char) ch);
            }
            String fromFile = builder.toString().trim();
            Log.i("OneListActivity", builder.toString());

            if (fromFile.contains("<")) {
                String[] staskList = fromFile.split(Pattern.quote("<"));
                for (String s : staskList) {
                    String[] taskinfo = s.split(Pattern.quote("*"));
                    Task tmp = new Task(taskinfo[0], taskinfo[1], taskinfo[2], taskinfo[3], Boolean.parseBoolean(taskinfo[4]));
                    Log.i("Sup dude", tmp.asString());
                    if (tmp.getIsComplete()) {
                        completedList.add(tmp);
                        cAdapter.notifyDataSetChanged();
                    } else {
                        taskList.add(tmp);
                        tAdapter.notifyDataSetChanged();
                    }
                }
            } else if (fromFile.contains("*")){
                String[] taskinfo = fromFile.split(Pattern.quote("*"));
                Task tmp = new Task (taskinfo[0], taskinfo[1], taskinfo[2], taskinfo[3], Boolean.parseBoolean(taskinfo[4]));
                Log.i("Sup dude", tmp.asString());
                if (tmp.getIsComplete()) {
                    completedList.add(tmp);
                    cAdapter.notifyDataSetChanged();
                } else {
                    taskList.add(tmp);
                    tAdapter.notifyDataSetChanged();
                }
            }


        } catch(Exception e) {
            Log.e("No file to store in", e.getMessage());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();

        String FILENAME = "oneliststore.txt";
        String toFile = "";
        ArrayList<Task> fullList = new ArrayList<Task>();
        fullList.addAll(taskList);
        fullList.addAll(completedList);
        Task t;

        //get the tasklist
        if (fullList.size() > 1) {
            for (int i = 0; i < fullList.size() - 1; i++) {
                t = fullList.get(i);
                toFile += t.getName() + "*" + t.getDueDate() + "*" + t.getDueTime() + "*" + t.getAddress() + "*" + t.getIsComplete() + "<";
            }
            t = fullList.get(fullList.size() - 1);
            toFile += t.getName() + "*" + t.getDueDate() + "*" + t.getDueTime() + "*" + t.getAddress() + "*" + t.getIsComplete();
        } else if (fullList.size() == 1) {
            t = fullList.get(0);
            toFile += t.getName() + "*" + t.getDueDate() + "*" + t.getDueTime() + "*" + t.getAddress() + "*" + t.getIsComplete();
        }

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(toFile.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e("Storage", e.getMessage());
        }
        Log.i("onStop", toFile);
    }

    @Override
    protected void onRestart() {super.onRestart(); }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //int orientation = this.getResources().getConfiguration().orientation;

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("Portrait","PPPPPPPPPPPPPPPPP");
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Landscape ","LLLLLLLLLLLLLLLLLLLL");
        }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    original = data.getParcelableExtra("original");
                    passed = data.getParcelableExtra("current");
                    if (!original.getIsComplete()) {
                        Log.i("OneList_activity", original.getName() + original.getIsComplete());
                        taskList.set(getIndexOfTaskList(taskList, original), passed);
                        tAdapter.notifyDataSetChanged();
                    } else {
                        completedList.set(getIndexOfTaskList(completedList, original), passed);
                        cAdapter.notifyDataSetChanged();
                    }
                }
                if (resultCode == 9000) {
                    original = data.getParcelableExtra("original");
                    if (!original.getIsComplete()) {
                        taskList.remove(getIndexOfTaskList(taskList, original));
                        tAdapter.notifyDataSetChanged();
                    } else {
                        completedList.remove(getIndexOfTaskList(completedList, original));
                        cAdapter.notifyDataSetChanged();
                    }
                }
                break;
            }
        }
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
                        if ((getIndexOfTaskList(taskList, tmp) == -1) && (getIndexOfTaskList(completedList, tmp) == -1)) {
                            taskList.add(tmp);
                            tAdapter.notifyDataSetChanged();
                        }
                    }
                });

                builder.setNegativeButton("Cancel",null);

                builder.create().show();
                return true;
            case R.id.sort_task_list_button:
                AlertDialog.Builder build2 = new AlertDialog.Builder(this);
                build2.setTitle("Sort by name?");
                build2.setMessage("Yes or no?");
                build2.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Collections.sort(taskList);
                        tAdapter.notifyDataSetChanged();
                        Collections.sort(completedList);
                        cAdapter.notifyDataSetChanged();
                    }
                });
                build2.setNegativeButton("Cancel", null);
                build2.create().show();
                return true;
            default:
                return false;
        }
    }

    public void setAsComplete(View view) {
        ArrayList<Task> tmp = new ArrayList<Task>();
        ArrayList<Task> tmptoRemove = new ArrayList<Task>();
        for (Task t : taskList) {
            if (t.getChecked()) {
                Task tmpt = new Task("a");
                tmpt.setEqual(t);
                tmpt.setIsComplete(true);
                tmp.add(tmpt);
                tmptoRemove.add(t);
                Log.i("The task", tmpt.getName() + ": " + tmpt.getIsComplete());
            }
        }
        completedList.addAll(tmp);
        cAdapter.notifyDataSetChanged();
        taskList.removeAll(tmptoRemove);
        tAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    //helper method
    private int getIndexOfTaskList(ArrayList<Task> tl, Task t) {
        for (int i = 0; i < tl.size(); i++) {
            if (tl.get(i).getName().equals(t.getName())) return i;
        }
        return -1;
    }
    public void setAsIncomplete(View view) {
        ArrayList<Task> tmp = new ArrayList<Task>();
        ArrayList<Task> tmptoRemove = new ArrayList<Task>();
        for (Task t : completedList) {
            if (t.getChecked()) {
                Task tmpt = new Task("a");
                tmpt.setEqual(t);
                tmpt.setIsComplete(false);
                tmp.add(tmpt);
                tmptoRemove.add(t);
            }
        }
        completedList.removeAll(tmptoRemove);
        cAdapter.notifyDataSetChanged();
        taskList.addAll(tmp);
        tAdapter.notifyDataSetChanged();
    }
}
