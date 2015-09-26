package cs4720.cs.virginia.edu.checklist;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Rock Beom Kim rk5dy
 * Peter Bahng pb5te
 */
public class Task implements Parcelable, Comparable<Task> {
    private String name;
    boolean checked;
    boolean isComplete;
    private String duedate;
    private int duetime;

    public Task(String n) {
        name = n;
        checked = false;
        isComplete = false;
    }

    public Task(Parcel in) {
        name = in.readString();
        duedate = in.readString();
        duetime = in.readInt();
    }

    public int describeContents() {
        return 0;
    }
    public String getName(){
        return name;
    }
    public boolean getIsComplete() { return isComplete; }
    public void setName(String s) {
        name = s;
    }
    public void setIsComplete(boolean b) { isComplete = b; }
    public boolean getChecked() {
        return checked;
    }
    public String getDueDate() { return duedate;    }
    public int getDueTime() {return duetime;}
    public void setChecked(boolean f) {
        this.checked = f;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(duedate);
        out.writeInt(duetime);
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public void setEqual(Task t) {
        name = t.getName();
        checked = t.getChecked();
        isComplete = t.getIsComplete();
        duedate = t.getDueDate();
        duetime = t.getDueTime();
    }

    public int compareTo(Task compareTask) {

        String compto = ((Task) compareTask).getName();

        return this.getName().compareTo(compto);

    }

}
