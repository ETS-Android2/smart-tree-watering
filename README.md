# Smart Tree Watering

## Introduction

In this project, I've developed an **IoT device** and an **android app**. The IoT device should be attached with tree and capable of measuring temperature, moisture level of the soil, weather information. And the android app can connect with the IoT device remotely with the help of Internet or locally using Bluetooth. Users can **manually control the watering process** by toggling a switch in the app or can set **conditional watering** based on the sensor values or timing.

<!-- https://github.com/mahfuznow/smart-tree-watering/ -->
![block diagram](/images/block-diagram.jpg)


### Android App
The App connects with the IoT Device via Wi-Fi or Bluetooth
* It shows the temperature, moisture level data.
* It also shows weather forecast using Darksky.net API.
* It allows user to **turn On** or **turn Off** **the water Pump** attached to IoT device.
* User can set conditional watering based on the sensor value. For example when soil moisture level is less than a certain threshold or temperature is too high then it may automatically start the water pump.

![Application Screenshots](/images/screenshot.webp)

### IoT Device
The IoT Device consist of following things
* Esp-32 Node MCU
* Temperature sensor
* Soil Moisture sensor
* Water Pump


## Project Requirements

### Software
1. Android Studio.
1. Cloud Database (Firebase).
1. Weather forecast API (darksky.net)
1. Android Device.
1. Arduino IDE.

### Hardware
1. ESP-32 Node MCU.
1. Soil Moisture Sensor.
1. 5V DC Water Pump.

## Connection

The application can be connected to the device in two ways:

1. Local connection via Bluetooth.
2. Remote connection via Internet.

### Local connection via Bluetooth

In the first setup, after turning on the device, we need to open the application and select “Connect via Bluetooth”. After completing the bluetooth scan, we can see the available device. Then we need to select our desired device. After that we shall see the sensor information on screen. Here the location of the android device will be collected and saved into the IOT device. Now we need to provide the wifi credentials to the IOT device. After that the IOT device will be connected to the wifi. Then we need to set a password into the IOT device which will be needed in remote connection.

After the initial setup, the IOT device will be saved inside the app for easy connection in future.

### Remote connection via Internet

If the IOT device is already set up and connected with the internet then we can connect to it from the application remotely via “Connect via Internet” option. Device id and password will be required while remote connection.

## Working principles

1. Android app
2. IOT device

### Android app

If the app is connected via Bluetooth then it continuously keeps listening to the incoming data via bluetooth and display accordingly. Wifi credentials, remote access password, location, water pump triggering data over the bluetooth connection.

If the app is connected via the Internet then it continuously keeps listening to the cloud database for any changes and display accordingly. It also displays the weather forecast of that area where the tree was planted with the help of weather API. Water pump triggering data can be sent to the cloud database via the internet connection.

### IoT Device
The working principle of the IoT device is explained in the following flow chart.

#### Flow chart 
![Application Screenshots](/images/flow-chart.png)

