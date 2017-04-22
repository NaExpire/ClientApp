package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListAdapterOrdersCurrent extends BaseAdapter {
    ArrayList<Order> orders;
    Context context;

    private static LayoutInflater inflater = null;

    public ListAdapterOrdersCurrent(Context c){
        orders = new ArrayList<>();
        context = c;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return orders.size();
    }

    @Override
    public Object getItem(int position){
        return position;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public String getId(int position){return orders.get(position).getId();}
    public String getItemName(int position){ return orders.get(position).getItemName(); }
    public String getRestaurantName(int position){ return orders.get(position).getRestaurantName(); }
    public String getImage(int position){ return orders.get(position).getImage(); }
    public String getPrice(int position){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(Double.parseDouble(orders.get(position).getPrice()));
    }
    public String getTime(int position){ return orders.get(position).getTime(); }
    public String getAddress(int position){ return orders.get(position).getAddress(); }
    public String getPhone(int position){ return orders.get(position).getPhone(); }
    public String getTotal(int position){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(orders.get(position).getTotal());
    }
    public String getQuantity(int position){ return orders.get(position).getQuantity(); }

    public void newItem(String id, String itemName, String restaurantName, String address, String phone,
                        String price, String quantity, String image, String time) {
        orders.add(new Order(id, itemName, restaurantName, address, phone, price, quantity,
                image, time));
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        orders.remove(position);
        notifyDataSetChanged();
    }

    public void sortOrders() { //sort orders by time
        Collections.sort(orders, new Comparator<Order>() {
            public int compare(Order o1, Order o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        notifyDataSetChanged();
    }

    public class Holder{
        TextView rn, tm, pr;
        ImageView im;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();
        final View rowView = inflater.inflate(R.layout.list_orders_current, null);
        holder.rn=(TextView) rowView.findViewById(R.id.lblOrdersRest);
        holder.tm=(TextView) rowView.findViewById(R.id.lblOrdersTime);
        holder.pr=(TextView) rowView.findViewById(R.id.lblOrdersPrice);
        holder.im=(ImageView) rowView.findViewById(R.id.imgOrdersPic);

        holder.rn.setText(orders.get(position).getRestaurantName());
        holder.tm.setText(orders.get(position).getTime());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        holder.pr.setText("$"+decimalFormat.format(orders.get(position).getTotal()));
        Glide.with(context).load(orders.get(position).getImage()).into(holder.im);

        return rowView;
    }

    public class Order{
        private String id, itemName, restaurantName, address, phone, price, quantity, image, time;
        private double total;

        Order(){
            id = "";
            itemName = "";
            restaurantName = "";
            address = "";
            phone = "";
            price = "";
            quantity = "";
            image = "";
            time = "";
            total = 0.00;
        }

        Order(String id, String itemName, String restaurantName, String address, String phone,
              String price, String quantity, String image, String time){
            this.id = id;
            this.itemName = itemName;
            this.restaurantName = restaurantName;
            this.address = address;
            this.phone = phone;
            this.price  = price;
            this.quantity = quantity;
            this.image = image;
            this.time = time;
            total = Integer.parseInt(quantity) * Double.parseDouble(price); //calculate total
            DecimalFormat decimalFormat = new DecimalFormat("0.00"); //format total
            total = Double.parseDouble(decimalFormat.format(total));
        }
        String getId(){return id;}
        String getItemName(){return itemName;}
        String getRestaurantName(){return restaurantName;}
        String getImage(){return image;}
        String getPrice(){return price;}
        String getTime(){return time;}
        String getAddress(){return address;}
        String getPhone(){return phone;}
        String getQuantity(){return quantity;}
        Double getTotal(){return total;}

        void setItemName(String itemName){this.itemName = itemName;}
        void setRestaurantName(String restaurantName){this.restaurantName = restaurantName;}
        void setImage(String image){this.image = image;}
        void setPrice(String price){this.price = price;}
        void setAddress(String address){this.address = address;}
        void setQuantity(String quantity){this.quantity = quantity;}
        void setTotal(double t){total = t;}
    }
}
