package cs4720.cs.virginia.edu.checklist;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

/**
 * Created by rbk on 12/3/15.
 */
public class DeleteDropboxFile extends AsyncTask<Void, Void, Boolean> {
    private DropboxAPI<?> dropbox;
    private String path;
    private Context context;
    private String fileName;

    public DeleteDropboxFile(Context context, DropboxAPI<?> dropbox,
                             String path, String fileName) {
        this.context = context;
        this.fileName = fileName;
        this.dropbox = dropbox;
        this.path = path;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            dropbox.delete(path + "/" + fileName);
            return true;
        } catch (DropboxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
