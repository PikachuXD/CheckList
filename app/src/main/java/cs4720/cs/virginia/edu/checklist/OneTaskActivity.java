package cs4720.cs.virginia.edu.checklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/*
* Rock Beom Kim rk5dy
* Peter Bahng pb5te
*
* */

public class OneTaskActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private Task current;
    private Task original;
    private EditText nameField;
    private EditText locField;
    private TextView timeView;
    private TextView dateView;
    private Geocoder geocoder;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = OneTaskActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Marker marker;
    private MarkerOptions mOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onetask);

        mOptions = new MarkerOptions().position(new LatLng(0, 0)).title("Marker");
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        Intent intent = getIntent();
        if (intent != null) {
            current = (Task) intent.getParcelableExtra("current");
            nameField = (EditText) findViewById(R.id.edit_name);
            nameField.setText(current.getName());
            locField = (EditText) findViewById(R.id.edit_loc);
            locField.setText(current.getAddress());
            timeView = (TextView) findViewById(R.id.time);
            timeView.setText(current.getDueTime());
            dateView = (TextView) findViewById(R.id.date);
            dateView.setText(current.getDueDate());
            original = new Task(current.getName());
            original.setEqual(current);
        }
        geocoder = new Geocoder(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.remove:
                intent.putExtra("original", (Parcelable) original);
                setResult(9000, intent);
                finish();
                return true;
            default:
                intent.putExtra("current", (Parcelable) current);
                intent.putExtra("original", (Parcelable) original);
                setResult(RESULT_OK, intent);
                finish();
        }



        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    //reconnect google api
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    //disconnect the map and stop the loc updates
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(mOptions);
    }

    //updating location
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    //saves instance state so the list isn't destroyed upon calling the one list activity
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //editing functions
    public void editName(View view) {
        current.setName(nameField.getText().toString());
    }

    public void editDate(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick the due date");
        final DatePicker datePicker = new DatePicker(this);
        builder.setView(datePicker)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String a = Integer.toString(datePicker.getYear()) + "/" + Integer.toString(datePicker.getMonth() + 1) + "/" + Integer.toString(datePicker.getDayOfMonth());
                        current.setDuedate(a);
                        dateView.setText(a);
                    }
                })
                .setNegativeButton("Cancel", null).show();
        Log.i("OneTask Activity", "The dialog should pop up");
    }

    public void editTime(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //time picking
        builder.setTitle("Pick the due time");
        final NumberPicker hours = new NumberPicker(this);
        final NumberPicker minutes = new NumberPicker(this);
        hours.setMinValue(1);
        hours.setMaxValue(24);
        hours.setWrapSelectorWheel(false);

        final TextView selectHours = new TextView(this);
        selectHours.setText("Hour (in military time)");
        final TextView selectMins = new TextView(this);
        selectMins.setText("Minutes");

        String[] minsof5 = new String[12];
        for (int i = 0; i < 12; i++) {
            minsof5[i] = Integer.toString(i*5);
        }
        minutes.setMinValue(0);
        minutes.setMaxValue(minsof5.length - 1);
        hours.setWrapSelectorWheel(false);
        minutes.setDisplayedValues(minsof5);
        //making the layout
        layout.addView(selectHours);
        layout.addView(hours);
        layout.addView(selectMins);
        layout.addView(minutes);
        builder.setView(layout)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int tmpmins = minutes.getValue() * 5;
                        String newmins = "";
                        if (tmpmins < 10) {
                            newmins = "0" + Integer.toString(tmpmins);
                        } else  {
                            newmins = Integer.toString(tmpmins);
                        }

                    String a = Integer.toString(hours.getValue()) + ":" + newmins;
                    current.setDuetime(a);
                    timeView.setText(a);
                }
    })
                .setNegativeButton("Cancel", null).show();
        Log.i("OneTask Activity", "The dialog should pop up");
    }

    //fixed up the map issue
    //sup yo
    public void submitloc(View view) {
        EditText locfield = (EditText) findViewById(R.id.edit_loc);
        current.setAddress(locfield.getText().toString());
        List<Address> listOfAddress;
        try {
            mMap.clear();
            listOfAddress = geocoder.getFromLocationName(locfield.getText().toString(), 1);
            if (listOfAddress.size() > 0) {
                Address address = listOfAddress.get(0);
                mOptions.title(address.getLocality())
                        .position(new LatLng(address.getLatitude(),
                                address.getLongitude()));
                marker = mMap.addMarker(mOptions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //clearing functions
    public void clearname(View view) {
        nameField.setText("");
    }

    public void clearTime(View view) {
        dateView.setText("");
        timeView.setText("");
        current.setDuedate("");
        current.setDuetime("");
    }

    public void clearloc(View view) {
        mMap.clear();
        locField.setText("");
        current.setAddress("");
    }

}
