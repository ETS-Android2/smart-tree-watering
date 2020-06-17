package com.mahfuznow.iotree.model;

public class DeviceDataModel {
    String mac_address, moisture, temperature, pump_trig;

    public DeviceDataModel() {
    }

    public DeviceDataModel(String mac_address, String moisture, String temperature, String pump_trig) {
        this.mac_address = mac_address;
        this.moisture = moisture;
        this.temperature = temperature;
        this.pump_trig = pump_trig;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getMoisture() {
        return moisture;
    }

    public void setMoisture(String moisture) {
        this.moisture = moisture;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPump_trig() {
        return pump_trig;
    }

    public void setPump_trig(String pump_trig) {
        this.pump_trig = pump_trig;
    }
}
