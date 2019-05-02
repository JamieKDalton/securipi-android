package com.duni.tp_androidapp.network;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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

            soc = new Socket(address, port);

            // Handle third message in array...
            String msgType = params[2];
            switch (msgType) {
                case "update":
                    // Refresh data on the form...
                    publishProgress(RESULT_CONNECTION_SUCCESSFUL);
                    return MessageHandler.structureBuilder(Build.MODEL, true,
                            msgType, " ", " ");
                case "addEmail":
                    // Add a new Email to the servers...
                    publishProgress(RESULT_CONNECTION_SUCCESSFUL);
                    return MessageHandler.structureBuilder(Build.MODEL, true,
                            msgType, params[3], " ");
                default:
                    publishProgress(RESULT_MESSAGE_CREATION_ERROR);
                    break;
            }

        } catch (IOException e) {
            // Handle 'Server not found' error here...
            publishProgress(RESULT_CONNECTION_FAILED);
        } catch (Exception e) {
            // Handle Unknown error here...
            publishProgress(RESULT_UNKNOWN_ERROR);
        }
        return "";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
            case RESULT_CONNECTION_SUCCESSFUL:
                break;
            case RESULT_MESSAGE_CREATION_ERROR:
                break;
            case RESULT_CONNECTION_FAILED:
                break;
            case RESULT_UNKNOWN_ERROR:
                break;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));

            writer.write(result);
            writer.flush();

            String response = reader.readLine();
            // Handle response here...

        } catch (IOException e) {
            Log.e(TAG, "Error in onPostExecute(): " + e.toString());
        }
    }
}
