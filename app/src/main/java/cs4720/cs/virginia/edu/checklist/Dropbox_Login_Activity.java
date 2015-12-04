package cs4720.cs.virginia.edu.checklist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AppKeyPair;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Dropbox_Login_Activity extends AppCompatActivity {

    private static final String appKey = "lssx2ezdqrffr3d";
    private static final String appSecret = "ueaqr31nkajxb9j";

    private static final int REQUEST_LINK_TO_DBX = 0;

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private final static String FILE_DIR = "/SavedCheckLists/";

    private ArrayList<String> itemList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Context context = this;
    private ListView listView;
    private String relevant = "";
    private String fileContents = "";
    private String accessToken = "";
    public static final String PREFS_NAME = "PrefsFile";
    private boolean longClicked;

    private final Handler listFilesHandler = new Handler() {
        public void handleMessage(Message msg) {
            itemList.clear();
            ArrayList<String> result = msg.getData().getStringArrayList("data");
            for (String oneFile : result) {
                String[] tmp = oneFile.split(Pattern.quote("."));
                itemList.add(tmp[0]);
            }
            adapter.notifyDataSetChanged();
        }
    };

    private final Handler loadFileHandler = new Handler() {
        public void handleMessage(Message msg) {
            fileContents = msg.getData().getString("data");
            if (!longClicked) {
                Intent intent = new Intent(context, OneList_Activity.class);
                intent.putExtra("accessToken", accessToken);
                intent.putExtra("fileContents", fileContents);
                intent.putExtra("fileName", relevant);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox__login_);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String accessTokenVal = settings.getString("accessToken", "none");
        Log.i("onCreate AT val: ", accessTokenVal);
        //login to dropbox
        AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);

        if (accessTokenVal.equals("")) {
            AndroidAuthSession session;
            session = new AndroidAuthSession(appKeys);
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().startOAuth2Authentication(Dropbox_Login_Activity.this);
        } else {
            AndroidAuthSession session;
            Log.i("Access token val", accessTokenVal);
            session = new AndroidAuthSession(appKeys);
            session.setOAuth2AccessToken(accessTokenVal);
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            refresh();
        }

        longClicked = false;
        //make sure listview works
        listView = (ListView) findViewById(R.id.fileList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);
        registerForContextMenu(listView);
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(mMessageClickedHandler);
        listView.setAdapter(adapter);

    }
    private AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            relevant = itemList.get(position) + ".txt";
            LoadDropboxFile list = new LoadDropboxFile(context, mDBApi, FILE_DIR,
                        loadFileHandler, relevant);
            list.execute();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String accessTokenVal = settings.getString("accessToken", "none");

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                    //Log.i("onResume(): ", "Whattup");
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();
                    Log.i("onResume()", ": Whataap");
                    accessToken = mDBApi.getSession().getOAuth2AccessToken();

                    //Log.i("Access Token DB: ", accessToken);
                    refresh();
                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                }
            }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("accessToken", accessToken);
        Log.i("onPause AT val: ", accessToken);
        // Commit the edits!
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("accessToken", accessToken);
        Log.i("onStop AT val: ", accessToken);
        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dropbox_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.add_list_btn:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add a List");
                builder.setMessage("Input name of list");
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String n = inputField.getText().toString();
                        UploadFileToDropbox upload = new UploadFileToDropbox(context, mDBApi,
                                FILE_DIR, "", n);
                        upload.execute();
                        refresh();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                return true;
            default:
                return false;
        }
    }

    private void refresh() {
        longClicked = false;
        ListFilesDropbox list = new ListFilesDropbox(mDBApi, FILE_DIR,
                listFilesHandler);
        list.execute();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        longClicked = true;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = ((String) adapter.getItem(info.position));
        menu.setHeaderTitle(title);
        menu.add(Menu.NONE, 0, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        DeleteDropboxFile delete;
        String fileName;
        switch (item.getItemId()) {
            //0 is delete
            case 0:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                fileName = itemList.get(info.position) + ".txt";
                delete = new DeleteDropboxFile(this, mDBApi, FILE_DIR, fileName);
                delete.execute();
                refresh();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
