package ztk.sensorcontrol;

/**
 * Created by ztk on 4/6/15.
 */
public class Humedad {
    String id;
    String fecha;
    String hora;
    String valor;
    String estado;

    public Humedad(String id, String fecha, String hora, String valor, String estado){
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.valor = valor;
        this.estado = estado;
    }
}
