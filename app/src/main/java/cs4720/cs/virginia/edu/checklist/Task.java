package cs4720.cs.virginia.edu.checklist;

/**
 * Created by Joltbolt on 9/14/2015.
 */
public class Task {
    private String name;
    boolean finished;
    private String dateAdded;

    public Task(String n) {
        name = n;
        finished = false;
    }

    public String getName(){
        return name;
    }

    public boolean getFinished() {
        return finished;
    }

    public void setFinished(boolean f) {
        this.finished = f;
    }
}
