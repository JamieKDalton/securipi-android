package com.duni.tp_androidapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.duni.tp_androidapp.database.DatabaseHandler;
import com.duni.tp_androidapp.devices.Device;
import com.duni.tp_androidapp.devices.DeviceArrayAdapter;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    private static final String TAG = "HomeScreen";

    private ListView connectedDevicesList;

    private static DeviceArrayAdapter arrayAdapter;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_ACCESS_WIFI_STATE = 2;
    private static final int PERMISSION_REQUEST_CHANGE_WIFI_STATE = 3;
    private static final int PERMISSION_REQUEST_INTERNET = 4;

    private static final int REQUEST_CODE_LAUNCH_CA = 5;
    private static final int REQUEST_CODE_LAUNCH_CA_UPDATE = 6;

    private static DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        Toolbar toolbar = findViewById(R.id.toolbar_hs);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.homescreen_name);

        databaseHandler = new DatabaseHandler(this);
        ArrayList<Device> devices = databaseHandler.getAllDevices();

        arrayAdapter = new DeviceArrayAdapter(this, devices, databaseHandler);

        connectedDevicesList = findViewById(R.id.connected_devices);
        connectedDevicesList.setAdapter(arrayAdapter);

        // Permission getter:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request Location Permissions
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Location Access is needed to scan for devices on a network");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
            if (this.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                // Request Access Wifi State Permissions
                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE}, PERMISSION_REQUEST_ACCESS_WIFI_STATE);
            }
            if (this.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                // Request Change Wifi State Permissions
                requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE}, PERMISSION_REQUEST_CHANGE_WIFI_STATE);
            }
            if (this.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                // Request Internet Permissions
                requestPermissions(new String[]{Manifest.permission.INTERNET}, PERMISSION_REQUEST_INTERNET);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Coarse Location permission has been granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality Limited");
                    builder.setMessage("Location Access has not been granted");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_ACCESS_WIFI_STATE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Access Wifi State has been granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality Limited");
                    builder.setMessage("You will not be able to create Peer to Peer connections to SecuriPi devices.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_CHANGE_WIFI_STATE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Change Wifi State permission has been granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality Limited");
                    builder.setMessage("You will not be able to create Peer to Peer connections to SecuriPi devices.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_INTERNET: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Internet permission has been granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality Limited");
                    builder.setMessage("You will not be able to create Peer to Peer connections to SecuriPi devices.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homescreen_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.launch_connectactivity:
                // Launch ConnectActivity for result:
                Intent intent = new Intent(HomeScreen.this, ConnectActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LAUNCH_CA);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_LAUNCH_CA):
                if (resultCode == Activity.RESULT_OK) {
                    String deviceName = data.getStringExtra("server_name");
                    String deviceIpAddress = data.getStringExtra("server_address");
                    int devicePort = Integer.parseInt(data.getStringExtra("server_port"));

                    Log.d(TAG, "Valid Device Data returned from ConnectActivity");
                    Log.d(TAG, "Name: " + deviceName + "\nIP: " + deviceIpAddress + "\n Port: " + devicePort);

                    Device device = new Device(deviceName, deviceIpAddress, devicePort);

                    arrayAdapter.add(device);
                    arrayAdapter.notifyDataSetChanged();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d(TAG, "resultCode == RESULT_CANCELED");
                }
                break;
            case (REQUEST_CODE_LAUNCH_CA_UPDATE):
                if (resultCode == Activity.RESULT_OK) {

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(getBaseContext(), "EDITED DEVICE", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}