# MotaSensora
Art monitoring for optimal conservation
There are three diferents parts that form this project: Arduino code, Kicad files and an Android app.

The Kicad files are used to build a PCB, used as Arduino shield, that integrate the different sensors used in the project: 
Temperature, Humidity and ambient light. 

The Android app is completed and ready to install. With the app ArtSense, you will be able to communicate with the Arduino by 
Bluetooth, and to read the measurements and know about the state of the ambient of the art piece, in terms of preventive conservation.

The Arduino code will manage the shield and take care of reading the sensors. It will also control de Bluetooth connectivity and send
the data to the Android device.
