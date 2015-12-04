package cs4720.cs.virginia.edu.checklist;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

/**
 * Created by rbk on 12/3/15.
 */
public class LoadDropboxFile extends AsyncTask<Void, Void, String> {

    private DropboxAPI<?> dropbox;
    private String path;
    private Context context;
    private String fileName;
    private Handler handler;
    public LoadDropboxFile(Context context, DropboxAPI<?> dropbox,
                           String path, Handler handler, String fileName) {
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
        this.fileName = fileName;
        this.handler = handler;
    }

    @Override
    protected String doInBackground(Void... params) {
        String fileContent = "";
        try {
            DropboxAPI.DropboxInputStream info = dropbox.getFileStream(path + "/" + fileName, null);
            int content;
            while ((content = info.read()) != -1) {
                fileContent += (char) content;
            }
            return fileContent;
        } catch (DropboxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileContent;
    }

    @Override
    protected void onPostExecute(String result) {
        Message msgObj = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("data", result);
        msgObj.setData(b);
        handler.sendMessage(msgObj);
    }
}
