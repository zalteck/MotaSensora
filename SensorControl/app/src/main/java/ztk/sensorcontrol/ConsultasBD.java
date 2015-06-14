package ztk.sensorcontrol;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ConsultasBD extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ArtSense";

    private static final String ID = "id";
    private static final String FECHA = "fecha";
    private static final String HORA = "hora";
    private static final String VALOR = "valor";
    private static final String ESTADO = "estado";

    public ConsultasBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_TEMP = "CREATE TABLE IF NOT EXISTS temperatura (id TEXT, fecha TEXT, hora TEXT, valor TEXT, estado TEXT, PRIMARY KEY(id))";

        db.execSQL(CREATE_TABLE_TEMP);
        Log.d("SQL", "Creada BD TEMP");

        /********************/

        String CREATE_TABLE_LUZ = "CREATE TABLE IF NOT EXISTS luz (id TEXT, fecha TEXT, hora TEXT, valor TEXT, estado TEXT, PRIMARY KEY(id))";

        db.execSQL(CREATE_TABLE_LUZ);
        Log.d("SQL", "Creada BD LUZ");

        /********************/

        String CREATE_TABLE_HUM = "CREATE TABLE IF NOT EXISTS humedad (id TEXT, fecha TEXT, hora TEXT, valor TEXT, estado TEXT, PRIMARY KEY(id))";

        db.execSQL(CREATE_TABLE_HUM);
        Log.d("SQL", "Creada BD HUM");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS luz");

        this.onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS temperatura");

        this.onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS humedad");

        this.onCreate(db);
    }

/**************/

    public void addTemperatura(Temperatura tp){

    // 1. get reference to writable DB
    SQLiteDatabase db = this.getWritableDatabase();

    // 2. create ContentValues to add key "column"/value
    ContentValues values = new ContentValues();
    values.put(ID, tp.id);
    values.put(FECHA, tp.fecha);
    values.put(HORA, tp.hora);
    values.put(VALOR, tp.valor);
    values.put(ESTADO, tp.estado);

    // 3. insert
    db.insert("temperatura", // table
            null, //nullColumnHack
            values); // key/value -> keys = column names/ values = column values

    // 4. close
    db.close();
}

    public ArrayList<Temperatura> getAllTemp() {
        ArrayList<Temperatura> listData = new ArrayList<Temperatura>();
        try {
            // 1. build the query
            String query = "SELECT * FROM temperatura";

            // 2. get reference to writable DB
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            // 3. go over each row, build book and add it to list
            Temperatura temp = null;

            if (cursor.moveToFirst()) {
                do {
                    temp = new Temperatura(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    listData.add(temp);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(Exception e){
            Log.d("SQL", "ERROR en getAllTemp" + e);
        }
        // return files
        return listData;
    }

    public Temperatura getLastTemp(){
        ArrayList<Temperatura> listData = new ArrayList<Temperatura>();
        try {
            // 1. build the query
            String query = "SELECT * FROM temperatura";

            // 2. get reference to writable DB
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            // 3. go over each row, build book and add it to list
            Temperatura temp = null;

            if (cursor.moveToFirst()) {
                do {
                    temp = new Temperatura(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    listData.add(temp);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(Exception e){
            Log.d("SQL", "ERROR en getAllTemp" + e);
        }

        if (listData.size()>0){
            return listData.get(listData.size()-1);
        }

        return null;
    }

    public void deleteAllTemp() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("temperatura", null, null);

        // 3. close
        db.close();

    }

/**************/

    /*********HUMEDAD*****/

    public void addHumedad(Humedad hm){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(ID, hm.id);
        values.put(FECHA, hm.fecha);
        values.put(HORA, hm.hora);
        values.put(VALOR, hm.valor);
        values.put(ESTADO,hm.estado);

        // 3. insert
        db.insert("humedad", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public ArrayList<Humedad> getAllHum() {
        ArrayList<Humedad> listData = new ArrayList<Humedad>();
        try {
            // 1. build the query
            String query = "SELECT * FROM humedad";

            // 2. get reference to writable DB
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            // 3. go over each row, build book and add it to list
            Humedad hum = null;

            if (cursor.moveToFirst()) {
                do {
                    hum = new Humedad(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    listData.add(hum);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(Exception e){
            Log.d("SQL", "ERROR en getAllHum" + e);
        }
        // return files
        return listData;
    }

    public Humedad getLastHum(){
        ArrayList<Humedad> listData = new ArrayList<Humedad>();
        try {
            // 1. build the query
            String query = "SELECT * FROM humedad";

            // 2. get reference to writable DB
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            // 3. go over each row, build book and add it to list
            Humedad hum = null;

            if (cursor.moveToFirst()) {
                do {
                    hum = new Humedad(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    listData.add(hum);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(Exception e){
            Log.d("SQL", "ERROR en getAllHum" + e);
        }

        if (listData.size()>0){
            return listData.get(listData.size()-1);
        }

        return null;
    }

    public void deleteAllHum() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("humedad",null,null);

        // 3. close
        db.close();

    }

/**************/

    /*********LUZ*****/

    public void addLuz(Luz lz){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(ID, lz.id);
        values.put(FECHA, lz.fecha);
        values.put(HORA, lz.hora);
        values.put(VALOR, lz.valor);
        values.put(ESTADO,lz.estado);

        // 3. insert
        db.insert("luz", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public ArrayList<Luz> getAllLuz() {
        ArrayList<Luz> listData = new ArrayList<Luz>();
        try {
            // 1. build the query
            String query = "SELECT * FROM luz";

            // 2. get reference to writable DB
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            // 3. go over each row, build book and add it to list
            Luz luz = null;

            if (cursor.moveToFirst()) {
                do {
                    luz = new Luz(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    listData.add(luz);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(Exception e){
            Log.d("SQL", "ERROR en getAllLuz" + e);
        }
        // return files
        return listData;
    }

    public Luz getLastLuz(){
        ArrayList<Luz> listData = new ArrayList<Luz>();
        try {
            // 1. build the query
            String query = "SELECT * FROM luz";

            // 2. get reference to writable DB
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            // 3. go over each row, build book and add it to list
            Luz luz = null;

            if (cursor.moveToFirst()) {
                do {
                    luz = new Luz(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    listData.add(luz);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(Exception e){
            Log.d("SQL", "ERROR en getAllLuz" + e);
        }

        if (listData.size()>0){
            return listData.get(listData.size()-1);
        }

        return null;
    }

    public void deleteAllLuz() {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("luz",null,null);

        // 3. close
        db.close();

    }

/**************/

}