# MotaSensora
Art monitoring for optimal conservation

This project was developed as final degree job for mi Telecommunication degree in University of Granada. It implements from scratch a complete art monitoring system capable of measuring light, humidity and temperature and ensure that the values are kept in an acceptable range.

The system raises an alert in case that any of the values get out of the safe range. It can also be scalated to control other devices in order to induce change to the monitoriced variables.

There are three diferents parts that form this project: Arduino code, Kicad files and an Android app.

The Kicad files are used to build a PCB, used as Arduino shield, that integrate the different sensors used in the project: 
Temperature, Humidity and ambient light.  

The Android app is completed and ready to install. With the app ArtSense, you will be able to communicate with the Arduino by 
Bluetooth, and to read the measurements and know about the state of the ambient of the art piece, in terms of preventive conservation.

The Arduino code will manage the shield and take care of reading the sensors. It will also control de Bluetooth connectivity and send
the data to the Android device.

This is registered in zenodo:
[![DOI](https://zenodo.org/badge/30555681.svg)](https://zenodo.org/doi/10.5281/zenodo.10204133)

