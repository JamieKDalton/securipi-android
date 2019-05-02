package com.duni.tp_androidapp.devices;

public class Device {

    private int id;
    private String deviceCustomName;
    private String deviceName;
    private String deviceIp;
    private int devicePort;
    private boolean status;

    public Device() {
        this.id = -1;
        this.deviceCustomName = null;
        this.deviceName = null;
        this.deviceIp = null;
        this.devicePort = -1;
        this.status = false;
    }

    public Device(String deviceCustomName, String deviceIp, int devicePort) {
        super();
        this.id = -1;
        this.deviceCustomName = deviceCustomName;
        this.deviceIp = deviceIp;
        this.devicePort = devicePort;
        this.deviceName = "Unknown";
        this.status = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceCustomName(String customName) {
        this.deviceCustomName = customName;
    }

    public String getDeviceCustomName() {
        return deviceCustomName;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDevicePort(int devicePort) {
        this.devicePort = devicePort;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }
}
