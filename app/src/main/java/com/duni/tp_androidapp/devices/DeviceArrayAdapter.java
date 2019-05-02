package com.duni.tp_androidapp.devices;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.duni.tp_androidapp.R;
import com.duni.tp_androidapp.database.DatabaseHandler;
import com.duni.tp_androidapp.network.MessageHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class DeviceArrayAdapter extends ArrayAdapter<Device> {

    private TextView deviceCustomName = null;
    private TextView deviceName = null;
    private TextView deviceAddress = null;
    private Switch swtToggle = null;
    private Button btnUpdate = null;
    private Button btnMore = null;

    private Device CUR_DEVICE = null;
    private int POSITION = -1;

    private ArrayList<Device> devices;
    private Context context;

    private static final String MESSAGE_UPDATE = "update";
    private static final String MESSAGE_SET_STATUS = "setStatus";

    private static final String TAG = "DeviceArrayAdapter";

    private static DatabaseHandler databaseHandler;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    public DeviceArrayAdapter(Context context, ArrayList<Device> devices, DatabaseHandler databaseHandler) {
        super(context, 0, devices);
        this.context = context;
        this.devices = devices;
        this.databaseHandler = databaseHandler;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public void add(Device d) {
        super.add(d);
        databaseHandler.addDevice(d);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Device d) {
        super.remove(d);
        databaseHandler.deleteDevice(d);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Device device = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_connected_device, null);
        }

        deviceCustomName = convertView.findViewById(R.id.device_custom_name);
        String customName = context.getString(R.string.custom_name) + " <b>" + device.getDeviceCustomName() + "</b>";
        deviceCustomName.setText(Html.fromHtml(customName));

        deviceName = convertView.findViewById(R.id.device_name);
        String name = context.getString(R.string.device_name) + " <b>" + device.getDeviceName() + "</b>";
        deviceName.setText(Html.fromHtml(name));

        deviceAddress = convertView.findViewById(R.id.device_full_address);
        String address = context.getString(R.string.ip_address ) + " <b>" + device.getDeviceIp() + ":" + device.getDevicePort() + "</b>";
        deviceAddress.setText(Html.fromHtml(address));

        swtToggle = convertView.findViewById(R.id.device_toggle);

        btnUpdate = convertView.findViewById(R.id.device_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send update AsyncConnectionHandler:
                String[] asyncTaskData = new String[3];
                asyncTaskData[0] = device.getDeviceIp();
                asyncTaskData[1] = String.valueOf(device.getDevicePort());
                asyncTaskData[2] = MESSAGE_UPDATE;
                AsyncConnectionHandler updateMessage = new AsyncConnectionHandler();
                updateMessage.execute(asyncTaskData);
            }
        });

        btnMore = convertView.findViewById(R.id.device_more);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate menu
                PopupMenu popup = new PopupMenu(context, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.device_menu, popup.getMenu());
                //btnRemove = view.findViewById(R.id.device_remove);
                //btnEdit = view.findViewById(R.id.device_edit);
                popup.show();
                MenuItem btnRemove = popup.getMenu().getItem(0);
                btnRemove.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        devices.remove(device);
                        databaseHandler.deleteDevice(device);
                        notifyDataSetChanged();
                        return true;
                    }
                });
            }
        });

        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String[] params = new String[4];
                params[0] = device.getDeviceIp();
                params[1] = String.valueOf(device.getDevicePort());
                params[2] = MESSAGE_SET_STATUS;
                params[3] = String.valueOf(isChecked);
                AsyncConnectionHandler updateMessage = new AsyncConnectionHandler();
                updateMessage.execute(params);
                databaseHandler.updateDevice(device);
            }
        };
        swtToggle.setOnCheckedChangeListener(checkedChangeListener);

        // These variables are so that the current device can be updated with devices.set()
        CUR_DEVICE = device;
        POSITION = position;

        // Update toggle with device's status
        updateToggle(device.getStatus());

        // Update list:
        notifyDataSetChanged();

        return convertView;
    }

    private void updateDeviceName(String name) {
        // Update Device's name...
        CUR_DEVICE.setDeviceName(name);
        devices.set(POSITION, CUR_DEVICE);

        // Update device in database:
        databaseHandler.updateDevice(CUR_DEVICE);

        notifyDataSetChanged();
    }

    private void updateToggle(boolean status) {
        // Have to remove the listener and then re add it so that the program doesn't
        // think that the user has changed it here.
        swtToggle.setOnCheckedChangeListener(null);
        swtToggle.setChecked(status);
        swtToggle.setOnCheckedChangeListener(checkedChangeListener);

        // Update current item
        CUR_DEVICE.setStatus(status);
        devices.set(POSITION, CUR_DEVICE);
    }

    public class AsyncConnectionHandler extends AsyncTask<String, Integer, String> {

        private Socket soc = null;

        private static final String TAG = "AsyncConnectionHandler";

        private static final int RESULT_CONNECTION_SUCCESSFUL = 1;
        private static final int RESULT_MESSAGE_CREATION_ERROR = 0;
        private static final int RESULT_CONNECTION_FAILED = -1;
        private static final int RESULT_UNKNOWN_ERROR = -2;

        @Override
        protected String doInBackground(String... params) {
            // Attempt to create connection...
            try {
                String address = params[0];
                int port = Integer.parseInt(params[1]);

                String msg = "";
                String response = "";

                soc = new Socket(address, port);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                // Handle third message in array...
                String msgType = params[2];
                switch (msgType) {
                    case MESSAGE_UPDATE:
                        // Refresh data on the form...
                        publishProgress(RESULT_CONNECTION_SUCCESSFUL);
                        msg =  MessageHandler.structureBuilder(Build.MODEL, true,
                                msgType, " ", " ");
                        // Send message...
                        writer.write(msg + "\n");
                        writer.flush();

                        response = reader.readLine();
                        while (response == null) {
                            response = reader.readLine();
                        }

                        return response;
                    case MESSAGE_SET_STATUS:
                        // Send Status change request to server
                        publishProgress(RESULT_CONNECTION_SUCCESSFUL);
                        msg =  MessageHandler.structureBuilder(Build.MODEL, true,
                                msgType, params[3], " ");
                        sendMessage(msg);

                        writer.write(msg + "\n");
                        writer.flush();

                        response = reader.readLine();
                        while (response == null) {
                            response = reader.readLine();
                        }

                        return response;
                    default:
                        publishProgress(RESULT_MESSAGE_CREATION_ERROR);
                        break;
                }

                reader.close();
                writer.close();

            } catch (IOException e) {
                // Handle 'Server not found' error here...
                publishProgress(RESULT_CONNECTION_FAILED);
                close();
                Log.e(TAG, "Connection failed");
            } catch (Exception e) {
                // Handle Unknown error here...
                publishProgress(RESULT_UNKNOWN_ERROR);
                close();
                Log.e(TAG, "Unknown Error" + e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Error handling during doInBackground():
            switch (values[0]) {
                case RESULT_CONNECTION_SUCCESSFUL:
                    // No need to notify the user that it is successful...
                    break;
                case RESULT_MESSAGE_CREATION_ERROR:
                    Toast.makeText(getContext(), context.getString(R.string.connection_unknown), Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CONNECTION_FAILED:
                    Toast.makeText(getContext(), context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_UNKNOWN_ERROR:
                    Toast.makeText(getContext(), context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        private void sendMessage(String result) {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));

                // Send message...
                Log.d(TAG, "Sending Message: " + result);
                writer.write(result + "\n");
                writer.flush();

                // Read message and separate elements
                Log.d(TAG, "Reading Message...");
                String response = reader.readLine();
                while (response == null) {
                    response = reader.readLine();
                }
                Log.d(TAG, "Message: " + response);
                reader.close();
                writer.close();
                HashMap<String, String> responseData = MessageHandler.separateElements(response);
                Log.d(TAG, "Message Received: " + response);

                // Update name...
                if (responseData.containsKey("name")) {
                    updateDeviceName(responseData.get("name"));
                } else {
                    Log.e(TAG, "Could not find name in responseData");
                }
                // Update toggle with new status key...
                if (responseData.containsKey("status")) {
                    Log.d(TAG, "Settings Toggle");
                    updateToggle(Boolean.parseBoolean(responseData.get("status")));
                } else {
                    Log.e(TAG, "Could not find status in responseData");
                }
            } catch (IOException e) {
                Log.e(TAG, "Error in onPostExecute(): " + e.toString());
            } catch (Exception e) {
                Log.e(TAG, "Error in onPostExecute(): " + e.toString());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Update UI here...
            HashMap<String, String> responseData = MessageHandler.separateElements(result);
            // Update name...
            if (responseData != null) {
                if (responseData.containsKey("name")) {
                    updateDeviceName(responseData.get("name"));
                } else {
                    Log.e(TAG, "Could not find name in responseData");
                }
                // Update toggle with new status key...
                if (responseData.containsKey("status")) {
                    updateToggle(Boolean.parseBoolean(responseData.get("status")));
                } else {
                    Log.e(TAG, "Could not find status in responseData");
                }
            } else {
                Log.e(TAG, "responseData == null");
            }
        }

        public void close() {
            soc = null;
        }
    }
}
