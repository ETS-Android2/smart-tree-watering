package com.mahfuznow.iotree;

class FirebaseModel {
    String mac_address, pass;
    int moisture;
    float temperature;
    boolean pump_trig;

    public FirebaseModel() {
    }

    public FirebaseModel(String mac_address, String pass, int moisture, float temperature, boolean pump_trig) {
        this.mac_address = mac_address;
        this.pass = pass;
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getMoisture() {
        return moisture;
    }

    public void setMoisture(int moisture) {
        this.moisture = moisture;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public boolean isPump_trig() {
        return pump_trig;
    }

    public void setPump_trig(boolean pump_trig) {
        this.pump_trig = pump_trig;
    }
}
