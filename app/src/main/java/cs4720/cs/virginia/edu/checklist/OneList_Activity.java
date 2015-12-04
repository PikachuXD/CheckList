package cs4720.cs.virginia.edu.checklist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

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

    private static final String appKey = "lssx2ezdqrffr3d";
    private static final String appSecret = "ueaqr31nkajxb9j";

    ArrayList<Task> taskList = new ArrayList<Task>();
    ArrayList<Task> completedList = new ArrayList<Task>();
    TaskAdapter tAdapter = null;
    TaskAdapter cAdapter = null;
    Task passed;
    Task original;
    Context context = this;
    //dropbox info
    private String fileName;
    private String fileContents;
    private String FILE_DIR = "/SavedCheckLists/";
    private String accessToken;
    private boolean edited;
    private AppCompatActivity app = this;
    private DropboxAPI<AndroidAuthSession> mDBApi;
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

        Intent intent = getIntent();
        if (intent != null) {
            fileName = intent.getStringExtra("fileName");
            accessToken = intent.getStringExtra("accessToken");
            fileContents = intent.getStringExtra("fileContents");
            Log.i("File name", fileName);
            Log.i("OneListContent ", fileContents);
            Log.i("Access token", accessToken);
            setTitle(fileName.split(Pattern.quote("."))[0]);
            if (fileContents.contains("<")) {
                String[] staskList = fileContents.split(Pattern.quote("<"));
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
            } else if (fileContents.contains("*")){
                String[] taskinfo = fileContents.split(Pattern.quote("*"));
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
        }

        AndroidAuthSession session;
        AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
        session = new AndroidAuthSession(appKeys);
        session.setOAuth2AccessToken(accessToken);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

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
    }

    private String toFileString() {
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

        return toFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onRestart() {super.onRestart(); }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //saves instance state so the list isn't destroyed upon calling the one task activity
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("tList", taskList);
        savedInstanceState.putParcelableArrayList("cList", completedList);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);


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
            case R.id.sort_task_list_button:
                AlertDialog.Builder build2 = new AlertDialog.Builder(this);
                build2.setTitle("Sort To-Do List alphabetically?");
                build2.setMessage("Touch 'SORT' to sort");
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
            case R.id.share_on_dropbox:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String[] beforeTxt = fileName.split(Pattern.quote("."));
                builder.setTitle("Save the list as");
                builder.setMessage("Input name of list");
                final EditText inputField = new EditText(this);
                inputField.setText(beforeTxt[0]);
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n = inputField.getText().toString();
                        UploadFileToDropbox upload = new UploadFileToDropbox(context, mDBApi,
                                FILE_DIR, toFileString(), n);
                        upload.execute();
                        DeleteDropboxFile delete = new DeleteDropboxFile(context, mDBApi, FILE_DIR, fileName);
                        delete.execute();
                        fileName = n;
                        app.setTitle(fileName);
                        fileName += ".txt";
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
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

    public void addTask(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a to-do...");
        builder.setMessage("What would you like to add?");
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
    }
}
