package com.duni.tp_androidapp.network;

import android.os.Build;

import java.util.HashMap;
import java.util.regex.Pattern;

public class MessageHandler {
    public MessageHandler() {

    }

    public static String structureBuilder(String name, boolean status, String req, String meta, String resp) {
        return "<name:" + name +
                ";status:" + Boolean.toString(status) +
                ";req:" + req +
                ";meta:" + meta +
                ";resp:" + resp + ">";
    }

    public static HashMap<String, String> separateElements(String r) {
        try {
            HashMap<String, String> details = new HashMap<>();

            String s = r.substring(1, r.length() - 1);
            String[] elements = s.split(Pattern.quote(";"));

            int size = elements.length;
            for (int i = 0; i < size; i++) {
                String[] vals = elements[i].split(Pattern.quote(":"));
                details.put(vals[0], vals[1]);
            }

            return details;
        } catch (Exception e) {
            return null;
        }
    }

    public static String handleResponse(String response, String deviceName) {
        HashMap<String, String> responseData = separateElements(response);

        String resp = responseData.get("resp");
        String req = responseData.get("req");
        return "";
    }
}
