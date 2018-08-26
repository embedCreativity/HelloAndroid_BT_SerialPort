# HelloAndroid_BT_SerialPort
Starting point for (Kotlin) Android Bluetooth Serial Port Apps

This is a simple Kotlin-based Android application used for pushing WiFi credentials (SSID and password) over to an embedded Internet-of-Things (IoT) device for initial association with the local network.

This app uses two "Activities" to first connect to a previously paired Bluetooth device and then to finally send the SSID and password over to the IoT device using the Bluetooth serial port profile via a Bluetooth socket.

The IoT device must be configured to stand-up a Bluetooth service to pair to. The IoT device must listen for this connection and finally to parse the data received from this app, at which point it would be capable of configuring its own network interface.

If you are interested in seeing an example of the IoT side BlueZ scripting needed to stand up this connection, see my "Tinkerboard_BT_SP" repo. The Tinkerboard requires some custom stuff to get the interface up initially, but the scripts should be helpful.
