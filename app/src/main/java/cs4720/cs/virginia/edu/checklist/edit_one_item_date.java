package cs4720.cs.virginia.edu.checklist;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Rock Beom Kim rk5dy
 * Peter Bahng pb5te
 */
public class edit_one_item_date extends Fragment {
    public edit_one_item_date() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_one_item_date, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
    }

}
