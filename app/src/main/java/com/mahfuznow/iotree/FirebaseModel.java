package com.mahfuznow.iotree;

class FirebaseModel {
    String mac, pass;
    int moisture;
    float temperature;
    boolean pump_trig;

    public FirebaseModel() {
    }

    public FirebaseModel(String mac, String pass, int moisture, float temperature, boolean pump_trig) {
        this.mac = mac;
        this.pass = pass;
        this.moisture = moisture;
        this.temperature = temperature;
        this.pump_trig = pump_trig;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
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
