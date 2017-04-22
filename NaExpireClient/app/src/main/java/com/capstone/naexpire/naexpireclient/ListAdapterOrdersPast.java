package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListAdapterOrdersPast extends BaseAdapter{
    ArrayList<Order> orders;
    Context context;

    private static LayoutInflater inflater = null;

    public ListAdapterOrdersPast(Context c){
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

    public String getOrderId(int position){return orders.get(position).getOrderId();}
    public String getName(int position){ return orders.get(position).getName(); }
    public String getRestaurant(int position){ return orders.get(position).getRestaurant(); }
    public Double getPrice(int position){ return orders.get(position).getPrice(); }
    public String getTime(int position){ return orders.get(position).getTime(); }
    public Double getTotal(int position){ return orders.get(position).getTotal(); }
    public int getRating(int position){ return orders.get(position).getRating(); }

    public void setRating(int position, int stars){orders.get(position).setRating(stars);}

    public void newItem(String id, String items, String restaurant, String time, String price,
                        String quantity, String rating) {
        orders.add(new Order(id, items, restaurant, time, price, quantity, rating));
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
        TextView id, rn, tl;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new Holder();
        final View rowView = inflater.inflate(R.layout.list_orders_past, null);
        holder.id=(TextView) rowView.findViewById(R.id.lblPastListId);
        holder.rn=(TextView) rowView.findViewById(R.id.lblPastListRest);
        holder.tl=(TextView) rowView.findViewById(R.id.lblPastListTotal);

        holder.id.setText(getTime(position));
        holder.rn.setText(getRestaurant(position));
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        holder.tl.setText("$"+decimalFormat.format(orders.get(position).getTotal()));

        return rowView;
    }

    public class Order{
        private String id, items, restaurant, time;
        private double price, total;
        private int quantity, rating;

        Order(){
            id = "";
            items = "";
            restaurant = "";
            time = "";
            price = 0.0;
            total = 0.0;
            quantity = 0;
            rating = 0;
        }

        Order(String id, String items, String restName, String timePlaced, String price,
              String quantity, String rating){
            this.id = id;
            this.items = items;
            restaurant = restName;
            this.price  = Double.parseDouble(price);
            time = timePlaced;
            /*String[] strPrices = price.split(",");
            for(int q = 0; q < strPrices.length; q++){
                total += Double.parseDouble(strPrices[q]);
            }*/
            this.quantity = Integer.parseInt(quantity);
            total = this.price * this.quantity;
            this.rating = Integer.parseInt(rating);
        }

        String getOrderId(){return id;}
        String getName(){return items;}
        String getRestaurant(){return restaurant;}
        Double getPrice(){
            /*String[] s = price.split(",");
            String formattedPrice = "";
            for(int t = 0; t< s.length;t++){
                formattedPrice += "$"+s[t]+"\n";
            }
            return formattedPrice;*/
            return price;
        }
        String getTime(){return time;}
        Double getTotal(){return total;}
        int getRating(){return rating;}

        void setName(String items){this.items = items;}
        void setRestaurant(String r){restaurant = r;}
        void setPrice(String p){price = Double.parseDouble(p);}
        void setTotal(double t){total = t;}
        void setRating(int stars){rating = stars;}
    }
}
