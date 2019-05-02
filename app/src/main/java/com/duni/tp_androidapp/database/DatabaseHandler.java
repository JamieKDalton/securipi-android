package com.duni.tp_androidapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duni.tp_androidapp.devices.Device;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "deviceManager";
    private static final String TABLE_DEVICES = "devices";
    private static final String KEY_ID = "id";
    private static final String KEY_CUSTOM_NAME = "custom_name";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "status";
    private static final String KEY_IP_ADDRESS = "ip_address";
    private static final String KEY_PORT = "port";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the Devices table.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DEVICES_TABLE = "CREATE TABLE " + TABLE_DEVICES + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_CUSTOM_NAME + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_STATUS + " TEXT,"
                + KEY_IP_ADDRESS + " TEXT," + KEY_PORT + " INTEGER" + ")";
        db.execSQL(CREATE_DEVICES_TABLE);
    }

    // Upgrading the database.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);

        // Create tables again
        onCreate(db);
    }

    // Add new device to database
    public void addDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOM_NAME, device.getDeviceCustomName());
        values.put(KEY_NAME, device.getDeviceName());
        values.put(KEY_STATUS, String.valueOf(device.getStatus()));
        values.put(KEY_IP_ADDRESS, device.getDeviceIp());
        values.put(KEY_PORT, device.getDevicePort());

        // Insert the row
        db.insert(TABLE_DEVICES, null, values);
        db.close();
    }

    // Get device with ID = id
    Device getDevice(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DEVICES, new String[] {KEY_ID, KEY_CUSTOM_NAME,
                KEY_NAME, KEY_STATUS, KEY_IP_ADDRESS, KEY_PORT}, KEY_ID + "=?",
                new String[] { String.valueOf(id)}, null, null,
                null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Device device = new Device(cursor.getString(1),
                cursor.getString(4),
                Integer.parseInt(cursor.getString(5)));

        device.setDeviceName(cursor.getString(2));
        device.setStatus(Boolean.parseBoolean(cursor.getString(3)));

        return device;
    }

    // Get list of all devices
    public ArrayList<Device> getAllDevices() {
        ArrayList<Device> devices = new ArrayList<>();

        // Select ALL query
        String selectQuery = "SELECT * FROM " + TABLE_DEVICES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Device device = new Device();
                device.setId(Integer.parseInt(cursor.getString(0)));
                device.setDeviceCustomName(cursor.getString(1));
                device.setDeviceName(cursor.getString(2));
                device.setStatus(Boolean.parseBoolean(cursor.getString(3)));
                device.setDeviceIp(cursor.getString(4));
                device.setDevicePort(Integer.parseInt(cursor.getString(5)));
                devices.add(device);
            } while (cursor.moveToNext());
        }

        return devices;
    }

    // Update Device 'device' in database
    public int updateDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOM_NAME, device.getDeviceCustomName());
        values.put(KEY_NAME, device.getDeviceName());
        values.put(KEY_STATUS, device.getStatus());
        values.put(KEY_IP_ADDRESS, device.getDeviceIp());
        values.put(KEY_PORT, device.getDevicePort());

        // updating row
        return db.update(TABLE_DEVICES, values, KEY_ID + " = ?",
                new String[] {String.valueOf(device.getId())});
    }

    // Delete Device 'device'
    public void deleteDevice(Device device) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEVICES, KEY_ID + " = ?",
                new String[] { String.valueOf(device.getId())});
        db.close();
    }

    // Get total device count
    public int getDeviceCount() {
        String countQuery = "SELECT * FROM " + TABLE_DEVICES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}
