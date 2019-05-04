package com.duni.tp_androidapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duni.tp_androidapp.devices.Device;
import com.google.gson.Gson;

import java.util.regex.Pattern;

public class ConnectActivity extends AppCompatActivity {

    private static final String TAG = "ConnectActivity";

    private Button btnAddDevice;
    private Button btnCancel;

    private TextInputLayout layName;
    private TextInputLayout layIpAddress;
    private TextInputLayout layPort;

    private String newDeviceName;
    private String newDeviceIpAddress;
    private String newDevicePort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // Initialise the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_ca);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.connectactivity_name);

        layName = findViewById(R.id.new_device_name_layout);
        layIpAddress = findViewById(R.id.new_device_ip_address_layout);
        layPort = findViewById(R.id.new_device_port_layout);

        btnAddDevice = findViewById(R.id.add_device);
        btnCancel = findViewById(R.id.cancel_add_device);

        btnAddDevice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (validateDataFields()) {
                    // If data is valid, create intent and send to HomeScreen...
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("server_name", newDeviceName);
                    resultIntent.putExtra("server_address", newDeviceIpAddress);
                    resultIntent.putExtra("server_port", newDevicePort);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add cancelled data to activity
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        layName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 30) {
                    layName.setError(getString(R.string.invalid_name_too_long));
                    layName.setErrorEnabled(true);
                } else if (s.length() == 0) {
                    layName.setError(getString(R.string.invalid_name_too_short));
                    layName.setErrorEnabled(true);
                } else {
                    layName.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        layIpAddress.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String temp = s.toString();
                String[] ipArray = temp.split(Pattern.quote("."));
                boolean errorFound = false;

                if (ipArray.length == 4) {
                    for (int i = 0; i < 4; i++) {
                        try {
                            int num = Integer.parseInt(ipArray[i]);
                            if (num < 0 || num > 255) {
                                // Numbers are out of range
                                errorFound = true;
                            } else {
                                layIpAddress.setErrorEnabled(false);
                            }
                        } catch (NumberFormatException e) {
                            layIpAddress.setError(getString(R.string.invalid_ip_number));
                            layIpAddress.setErrorEnabled(true);
                        }
                    }
                } else {
                    layIpAddress.setError(getString(R.string.invalid_ip_format));
                    layIpAddress.setErrorEnabled(true);
                }
                if (errorFound) {
                    layIpAddress.setError(getString(R.string.invalid_ip_oor));
                    layIpAddress.setErrorEnabled(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        layPort.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempPort = s.toString();
                try {
                    int port = Integer.parseInt(tempPort);
                    if (port < 2000) {
                        layPort.setError(getString(R.string.invalid_port_small));
                        layPort.setErrorEnabled(true);
                    } else {
                        layPort.setErrorEnabled(false);
                    }
                } catch (NumberFormatException e) {
                    layPort.setError(getString(R.string.invalid_port_format));
                    layPort.setErrorEnabled(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateDataFields() {
        newDeviceName = layName.getEditText().getText().toString();
        newDeviceIpAddress = layIpAddress.getEditText().getText().toString();
        newDevicePort = layPort.getEditText().getText().toString();

        if (layName.getEditText().getText().length() == 0 ||
                layIpAddress.getEditText().getText().length() == 0 ||
                layPort.getEditText().getText().length() == 0) {
            return false;
        } else {
            return (!(layName.isErrorEnabled() ||
                    layIpAddress.isErrorEnabled() ||
                    layPort.isErrorEnabled()));
        }
    }
}
