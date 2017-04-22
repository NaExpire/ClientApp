package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListAdapterFoods extends BaseAdapter{

    ArrayList<String> types, checked;
    Context context;

    private static LayoutInflater inflater = null;

    public ListAdapterFoods(Context c, ArrayList<String> t, ArrayList<String> ch){
        types = t;
        checked = ch;
        context = c;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return types.size();
    }

    @Override
    public Object getItem(int position){
        return position;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public ArrayList<String> getChecked(){ return checked; }

    public class Holder{
        CheckBox box;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();
        final View rowView = inflater.inflate(R.layout.list_foods, null);
        holder.box = (CheckBox) rowView.findViewById(R.id.checkBox);

        holder.box.setText(types.get(position));
        for(int i = 0; i < checked.size(); i++){
            if(checked.get(i).equals(types.get(position))) holder.box.setChecked(true);
        }

        holder.box.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) { checked.add(types.get(position)); }
                else checked.remove(types.get(position));
            }
        });

        return rowView;
    }
}