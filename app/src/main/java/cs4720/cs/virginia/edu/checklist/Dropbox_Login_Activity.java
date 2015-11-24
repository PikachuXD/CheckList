package cs4720.cs.virginia.edu.checklist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AppKeyPair;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Dropbox_Login_Activity extends AppCompatActivity {

    private static final String appKey = "lssx2ezdqrffr3d";
    private static final String appSecret = "ueaqr31nkajxb9j";

    private static final int REQUEST_LINK_TO_DBX = 0;
    private String fromFile;

    private TextView tmp;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private final static String FILE_DIR = "/SavedCheckLists/";
    private EditText fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox__login_);

        tmp = (TextView) findViewById(R.id.test_output);
        fileName = (EditText) findViewById(R.id.fileName);

        Intent intent = getIntent();
        if (intent != null) {
            fromFile = intent.getStringExtra("toFile");
            tmp.setText(fromFile);

        }

        // In the class declaration section:

// And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(Dropbox_Login_Activity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    public void upload(View view) {
        String n = fileName.getText().toString();
        UploadFileToDropbox upload = new UploadFileToDropbox(this, mDBApi,
                FILE_DIR, fromFile, n);
        upload.execute();
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
}
