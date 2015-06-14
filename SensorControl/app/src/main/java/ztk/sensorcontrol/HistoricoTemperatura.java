package ztk.sensorcontrol;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListFragment;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;

import java.util.ArrayList;


public class HistoricoTemperatura extends ActionBarActivity {

    public ArrayList<Temperatura> listaDeTemperatura = new ArrayList<Temperatura>();
    CustomAdapterTemperatura adapter;
    ListView listview;
    ConsultasBD db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume(){

        super.onResume();

        setContentView(R.layout.activity_historico_temperatura);

        db = new ConsultasBD(this);

        listview = (ListView) findViewById(R.id.listTemp);

        try{
            listaDeTemperatura = db.getAllTemp();
            for (int i=0;i<listaDeTemperatura.size(); i++){
                Log.d("PROPIOS", "MOSTRAR"+ listaDeTemperatura.get(i).id);
        }

        }catch(Exception e){
            Log.d("ArtSense", "ERROR al obtener Subjects");
        }

        if(listaDeTemperatura.size()>0) {
            adapter = new CustomAdapterTemperatura(this, listaDeTemperatura);
            listview.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_historico_temperatura, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}




