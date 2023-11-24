# MotaSensora
Art monitoring for optimal conservation

This project was developed as the final degree job for my Telecommunications degree at the University of Granada. It implements from scratch a complete art monitoring system capable of measuring light, humidity, and temperature and ensuring that the values are kept in an acceptable range.

The system raises an alert in case any values get out of the safe range. It can also be escalated to control other devices in order to induce change to the monitored variables.

There are three different parts that form this project: Arduino code, Kicad files, and an Android app.

The Kicad files are used to build a PCB, used as an Arduino shield, that integrates the different sensors used in the project: Temperature, Humidity, and ambient light.

The Android app is completed and ready to install. With the app ArtSense, you will be able to communicate with the Arduino by Bluetooth, read the measurements, and know about the state of the ambient of the art piece, in terms of preventive conservation.

The Arduino code will manage the shield and read the sensors. It will also control Bluetooth connectivity and send the data to the Android device.
This is registered in zenodo:
[![DOI](https://zenodo.org/badge/30555681.svg)](https://zenodo.org/doi/10.5281/zenodo.10204133)

