/*
 Example sketch for the RFCOMM/SPP Bluetooth library - developed by Kristian Lauszus
 For more information visit my blog: http://blog.tkjelectronics.dk/ or
 send me an e-mail:  kristianl@tkjelectronics.com
 */

#include <SPP.h>
#include <usbhub.h>
// Satisfy IDE, which only needs to see the include statment in the ino.
#ifdef dobogusinclude
#include <spi4teensy3.h>
#endif

//Sensores e i2c
#include <SparkFunTSL2561.h>
#include <HIH61XX.h>
#include <Wire.h>

// Create an SFE_TSL2561 object, here called "light":
SFE_TSL2561 light;

//  Create an HIH61XX with I2C address 0x27, powered by pin 8
HIH61XX hih(0x27, 8);

// Global variables:

boolean gain;     // Gain setting, 0 = X1, 1 = X16;
unsigned int ms;  // Integration ("shutter") time in milliseconds

USB Usb;
//USBHub Hub1(&Usb); // Some dongles have a hub inside

BTD Btd(&Usb); // You have to create the Bluetooth Dongle instance like so
/* You can create the instance of the class in two ways */
SPP SerialBT(&Btd,"MotaSensora","0000"); // This will set the name to the defaults: "MotaSensora" and the pin to "0000"
//SPP SerialBT(&Btd, "Lauszus's Arduino", "1234"); // You can also set the name and pin like so

boolean firstMessage = true;
char dato;
char cadena[255];
String estado = "*ok";
int i=0; //posicion del array

  long preMillis=millis();
  long intervalo=1000; //milisegundos entre medidas 
  //24h=24h*3600s/h=86400s=86400000 millis
  long dia=86400000;
  int numMedidas=dia/intervalo;
  int medidas=0;
  unsigned long nowMillis=0;
  
  float Tnow;
  float Tini;
  float Tvar;
  int TvarTotal=0;
  
  float Hnow;
  float Hini;
  float Hvar;
  int HvarTotal=0;
  float aux;
  float aux2;
  int auxint=0;
  
  unsigned int data0, data1;
  double lux;    // Resulting lux value
  boolean good;  // True if neither sensor is saturated
  double maxLux=200;
  int c=0;
  int d=0;

void setup() {
  Serial.begin(115200);
  Wire.begin();
  
  //INICIO BT Y SERIAL
  while (!Serial); // Wait for serial port to connect - used on Leonardo, Teensy and other boards with built-in USB CDC serial connection
  if (Usb.Init() == -1) {
    Serial.print(F("\r\nOSC did not start"));
    while (1); //halt
  }
  Serial.print(F("\r\nSPP Bluetooth Library Started"));
  
  //INICIO LUZ
  light.begin();
  gain = 0;
  unsigned char time = 2;
  Serial.println("Set timing...");
  light.setTiming(gain,time,ms);
  // To start taking measurements, power up the sensor: 
  Serial.println("Powerup...");
  light.setPowerUp();
  
  //INICIO HUMEDAD y TEMP (PUEDE QUE TENGA QUE IR EN LOOP
  hih.start();
  hih.update();
  if(hih.temperature()>=18 && hih.temperature()<=26){
    Tini=hih.temperature();
  }else if (hih.temperature()>26){
    Tini=26;
  }else {Tini=18;}
  
  aux=hih.humidity()*100;
  if(aux>=35 && aux<=65){
    Hini=aux;
  }else if (aux>65){
    Hini=65;
  }else {Hini=35;}
  Serial.print("H inicial: ");
  Serial.println(Hini);


}
void loop() {
  Usb.Task(); // The SPP data is actually not send until this is called, one could call SerialBT.send() directly as well

  if (SerialBT.connected) {
    if (firstMessage) {
      SerialBT.print("AskR*");
          SerialBT.print(Tini);
          SerialBT.print("*");
          SerialBT.print(Hini);
          SerialBT.print("*");
          SerialBT.print(maxLux);
          SerialBT.print("*");
          auxint=intervalo/1000;
          SerialBT.print(auxint);
          SerialBT.print("*");
          
          Serial.println("int enviado");
          Serial.println(auxint);
      
      firstMessage = false;
    }//fin if first message
    
    if (Serial.available())
      SerialBT.write(Serial.read());
    if (SerialBT.available()){//******************MENU DE USUARIO***************************
      dato = SerialBT.read(); //guarda datos caracter a caracter
      cadena[i++]=dato;//Añade el nuevo caracter a la cadena
      
        if(dato=='*'){
        Serial.println(cadena); 
        
        if(strstr(cadena,"AskQ")!=0){
          Serial.println("Peticion");
          SerialBT.print("AskR*");
          SerialBT.print(Tini);
          SerialBT.print("*");
          SerialBT.print(Hini);
          SerialBT.print("*");
          SerialBT.print(maxLux);
          SerialBT.print("*");
          auxint=intervalo/1000;
          SerialBT.print(auxint);
          SerialBT.print("*");
          
          Serial.println(auxint);

          c=1;    
        }
        
        if(strstr(cadena,"Hset")!=0){
          Serial.println("Recibido comando");       
          c=2;   
          d=0;         
        }
        
        if(strstr(cadena,"Tset")!=0){
          Serial.println("Recibido comando");       
          c=3;   
          d=0;         
        }
        
        if(strstr(cadena,"Iset")!=0){
          Serial.println("Recibido comando");        
          c=4;   
          d=0;         
        }
        
        if(strstr(cadena,"Lset")!=0){
          Serial.println("Recibido comando");        
          c=5;   
          d=0;         
        }
        
        if(c==0){        
          SerialBT.println("Comando no reconocido");
        }
 
       //FIN DE COMANDOS BASICOS       
            
        if((c==2) && (d==1)){//Opciones Hini
          aux=atof(cadena);

          if(aux>=35 && aux<=65){
            Hini=aux;
            Serial.print("Valor de humedad ideal fijado a: ");
            Serial.print(aux);
            Serial.println(" %");
            c=0; 
          }     
          if (c!=0){
            Serial.println("comando no reconocido o valor no valido");
            c=0;
          }
        }//FIN Hini
        
        if((c==3) && (d==1)){//Opciones Tini
          aux=atof(cadena);
          
          if(aux>=18 && aux<=26){
            Tini=aux;
            Serial.print("Valor de temperatura ideal fijado a: ");
            Serial.print(aux);
            Serial.println(" C");
            c=0; 
          }     
          if (c!=0){
            Serial.println("comando no reconocido o valor no valido");
            c=0;
          }
        }//FIN Tini
        
        if((c==4) && (d==1)){//Opciones Iini
          auxint=atoi(cadena);
          Serial.print("intervalo recibido; ");
          Serial.println(auxint);
          //intervalo en segundos debe ser un valor entre 30s y 3600s (1hora)
          if(auxint>=30 && auxint<=3600){
            intervalo=(long) auxint*1000;
            Serial.print("Intervalo de medida fijado a: ");
            Serial.print(intervalo);
            Serial.println(" s");
            c=0; 
          }     
          if (c!=0){
            Serial.println("comando no reconocido o valor no valido");
            c=0;
          }
        }//FIN Intervalo ini
        
        if((c==5) && (d==1)){//Opciones Lini
          auxint=atoi(cadena);
          //Luz maxima 2000
          if(auxint>=0 && auxint<=5000){
            maxLux=auxint;
            Serial.print("Luz maxima fijada a: ");
            Serial.print(maxLux);
            Serial.println(" ");
            c=0; 
          }     
          if (c!=0){
            Serial.println("comando no reconocido o valor no valido");
            c=0;
          }
        }//FIN Lini
        
        SerialBT.write("\r"); //retorno de carro a la app
        clean();//limpiamos array
        d=1;      
      }//FIN DEL IF PARA CADENAS
      
    }//******************FIN MENU DE USUARIO***************************
      
      
      
  }
  else
    firstMessage = true;

  //CONTROL AUTOMATICO***************************************
  //La temperatura debe ser un valor entre 17-27ºC con una oscilacion max +-2-5ºC
  //La humedad un valor entre 30-70% con oscilación max de +-5-10%HR
   nowMillis=millis();
  //Si se ha superado el tiempo sin comprobacion
  if (nowMillis-preMillis>intervalo){
    //medir todo
    hih.update();
    
    Tnow=hih.temperature();
    Tvar=Tnow-Tini;
    
    Hnow=hih.humidity()*100;
    Hvar=Hnow-Hini;
    
    //almacenar variaciones
    TvarTotal=TvarTotal+Tvar;
    HvarTotal=HvarTotal+Hvar;
    medidas++;
    
    if (medidas==numMedidas){
      //han pasado 24h actualizamos las medias de Hum y Tmp
      aux=TvarTotal/numMedidas; //Media de variación
      //aux2=Tini+aux;
      //if (aux2>18 && aux2<26){
        //Tini=aux2;
      //}else{
        //Serial.println("Temperatura media en zona peligrosa");
        ////Se mantiene Tini anterior
      //}
      
      aux=HvarTotal/numMedidas; //Media de variación
      //aux2=Hini+aux;
      //if (aux2>35 && aux2<65){
        //Hini=aux2;
      //}else{
        //Serial.println("Humedad media en zona peligrosa");
        ////Se mantiene Hini anterior
      //}
    }
        
    Serial.print("Temperature: ");
    //Serial.print(hih.temperature(), 5);
    Serial.print(Tnow, 2);
    Serial.print(" C ");
    Serial.print("- Variacion: ");
    Serial.print(Tvar, 2);
    Serial.println(" C ");
    
    //SerialBT.print("Temperatura*hoy*25.52*bien");    
    mostrarTemperatura();
    mostrarHumedad();
    mostrarLuz();
    
    Serial.print("Humidity: ");
    //Serial.print(hih.temperature(), 5);
    Serial.print(Hnow, 5);
    Serial.print(" % ");
    Serial.print("- Variacion: ");
    Serial.print(Hvar, 5);
    Serial.println(" % ");
    
    delay(ms);
    if (light.getData(data0,data1)){
      // Perform lux calculation:
      good = light.getLux(gain,ms,data0,data1,lux);
      // Print out the results:
      Serial.print(" lux: ");
      Serial.println(lux);
      Serial.println("");
      }
    
    //Si temperatura es alta--> refrigerar
    if (Tvar>2 || Tnow>26){
      Serial.println("Refrigerar");
    }
    //Si temperatura es baja-->calentar
    if (Tvar<-2 || Tnow<18){
      Serial.println("Calentar");
    }
    //Si humedad es alta
    if (Hvar>2 || Hnow>65){
      Serial.println("Secar");
    }
    //Si humedad es baja
    if (Hvar<-2 || Hnow<35){
      Serial.println("Humidificar");
    }
    //Si luz demasiada
    if (lux>maxLux){
      Serial.println("Demasiada luz");
    }
    //Luz poca?
    preMillis=millis();
  }//FIN CONTROL AUTOMATICO***************************************

    
}//fin loop


void clean(){
  //funcion de limpieza de array
  for (int cl=0; cl<=i; cl++){
    cadena[cl]=0;
  }
  i=0;
}

void mostrarTemperatura(){
  //hih.update();
  //Tnow=hih.temperature();
  //Si temperatura es alta--> refrigerar
    if (Tvar>2 || Tnow>26){
      estado = "*hi*";
    }
    //Si temperatura es baja-->calentar
    else if (Tvar<-2 || Tnow<18){
      estado = "*lo*";
    }else estado = "*ok*";
    
  if (SerialBT.connected) {
    SerialBT.print("Temperatura*");
    SerialBT.print(Tnow, 2);
    SerialBT.print(estado);
  }
  
}//FIN mostrarTemperatura

void mostrarHumedad(){ 
    if (Hvar>2 || Hnow>65){
      estado = "*hi*";
    }
    //Si humedad es baja
    else if (Hvar<-2 || Hnow<35){
      estado = "*lo*";
    }else estado = "*ok*";
    
  if (SerialBT.connected) {
    SerialBT.print("Humedad*");
    SerialBT.print(Hnow, 2);
    SerialBT.print(estado);
  }
  
}

void mostrarLuz(){  
    if (light.getData(data0,data1)){
      // Perform lux calculation:
      good = light.getLux(gain,ms,data0,data1,lux);
      // Print out the results:
      
      if (lux>maxLux){
        estado="*hi*";
      }else estado = "*ok*";
    
      if (SerialBT.connected) {
        SerialBT.print("Luz*");
        SerialBT.print(lux, 0);
        SerialBT.print(estado);
      }    
  }
}

//boolean ComprobarNum(char input[]){
//  boolean valido=false;
//  //Vamos a comprobar un dato de tipo XX.XX
//  //comprobamos primer dato entre 0 y 9
//  Serial.println("Comprobandovalor2");
//  Serial.write(input[0]);
//  Serial.write(input[1]);
//  Serial.write(input[2]);
//  Serial.write(input[3]);
//  Serial.write(input[4]);
//  
//  if (input[0]>=0 && input[0]<=9){
//    //comprobamos segundo dato
//    Serial.println("bien1");
//    if (input[1]>=48 && input[1]<=57){
//      //comprobamos punto 
//      if (input[2]==46){
//        //cuarta pos
//        if (input[3]>=48 && input[3]<=57){
//          //quinta pos
//          if (input[4]>=48 && input[4]<=57){
//            //Comprobacion completa
//            Serial.println("VALOR COJONUDO");
//            Serial.println(input);
//            valido=true;
//          }//quinta pos
//        }//cuarta pos
//      }//tercer pos
//    }//segunda pos
//  }//primera posicion   
// 
// return valido; 
//}
