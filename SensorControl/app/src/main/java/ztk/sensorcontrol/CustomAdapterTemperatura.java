package ztk.sensorcontrol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapterTemperatura extends BaseAdapter {

    Context context;
    ArrayList<Temperatura> rowItem;

    CustomAdapterTemperatura(Context context, ArrayList<Temperatura> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
    }

    @Override
    public int getCount() {

        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {

        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {

        return rowItem.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = new View(context);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.lista_temp, null);

        }

        TextView fecha = (TextView) convertView.findViewById(R.id.fecha);
        TextView hora = (TextView) convertView.findViewById(R.id.hora);
        TextView valor = (TextView) convertView.findViewById(R.id.valor);

        int nuevaPosition = rowItem.size()-1-position;
        fecha.setText(rowItem.get(nuevaPosition).fecha);
        hora.setText(rowItem.get(nuevaPosition).hora);
        valor.setText(rowItem.get(nuevaPosition).valor);

        if (rowItem.get(nuevaPosition).estado.equals("hi")){
            fecha.setTextColor(Color.parseColor("#ff0000"));
            hora.setTextColor(Color.parseColor("#ff0000"));
            valor.setTextColor(Color.parseColor("#ff0000"));
        }
        else if (rowItem.get(nuevaPosition).estado.equals("lo")){
            fecha.setTextColor(Color.parseColor("#0000ff"));
            hora.setTextColor(Color.parseColor("#0000ff"));
            valor.setTextColor(Color.parseColor("#0000ff"));
        }
        else{
            fecha.setTextColor(Color.parseColor("#000000"));
            hora.setTextColor(Color.parseColor("#000000"));
            valor.setTextColor(Color.parseColor("#000000"));
        }

        return convertView;

    }

}