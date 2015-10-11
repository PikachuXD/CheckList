package cs4720.cs.virginia.edu.checklist;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Rock Beom Kim rk5dy
 * Peter Bahng pb5te
 */
public class Task implements Parcelable, Comparable<Task> {
    private String name;
    boolean checked;
    boolean isComplete;
    private String duedate;
    private String duetime;
    private String address;

    public Task(String n) {
        name = n;
        checked = false;
        isComplete = false;
        address = "";
        duetime = "";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        duedate = sdf.format(date);
    }

    public Task(String name, String duedate, String duetime, String address, Boolean isComplete) {
        this.name = name;
        this.duedate = duedate;
        this.duetime = duetime;
        this.address = address;
        this.isComplete = isComplete;
    }

    public Task(Parcel in) {
        name = in.readString();
        duedate = in.readString();
        duetime = in.readString();
        isComplete = in.readByte() != 0;
        address = in.readString();
        checked = in.readByte() != 0;
    }

    public int describeContents() {
        return 0;
    }

    //name
    public String getName(){
        return name;
    }
    public void setName(String s) {
        name = s;
    }

    //is the task complete? Is the item checked?
    public boolean getIsComplete() { return isComplete; }
    public void setIsComplete(boolean b) { isComplete = b; }
    public boolean getChecked() {
        return checked;
    }
    public void setChecked(boolean f) {
        this.checked = f;
    }

    //due date and due time
    public String getDueDate() { return duedate;    }
    public String getDueTime() {return duetime;}
    public void setDuetime(String t) {duetime = t;}
    public void setDuedate(String t) {duedate = t;}

    //getting the address
    public String getAddress() {return address;}
    public void setAddress(String a) {this.address = a;}

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(duedate);
        out.writeString(duetime);
        out.writeByte((byte) (isComplete ? 1 : 0));
        out.writeString(address);
        out.writeByte((byte) (checked ? 1 : 0));
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
        address = t.getAddress();
    }

    public int compareTo(Task compareTask) {

        String compto = ((Task) compareTask).getName();

        return this.getName().compareTo(compto);

    }

    public String asString() {
        return "Name: " + name + " Due Date: " + duedate + " Due time: " + duetime + " Address: " + address;
    }

    public String getDueString() {
        if (duedate.equals("") || duetime.equals("")) {
            return "";
        } else if (duedate.equals("") && !duetime.equals("")) {
            return duetime;
        } else if (!duedate.equals("") && duetime.equals("")) {
            return duedate;
        } else {
            return duedate + "@" + duetime;
        }
    }
}
