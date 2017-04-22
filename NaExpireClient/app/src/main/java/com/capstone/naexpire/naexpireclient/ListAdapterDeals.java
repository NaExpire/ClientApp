package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListAdapterDeals extends BaseAdapter {

    ArrayList<Discount> discounts;
    Context context;

    private static LayoutInflater inflater = null;

    public ListAdapterDeals(Context c){
        discounts = new ArrayList<>();
        context = c;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){
        return discounts.size();
    }

    @Override
    public Object getItem(int position){
        return position;
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public int getId(int position){ return discounts.get(position).getId(); }
    public String getName(int position){ return discounts.get(position).getName(); }
    public Double getPrice(int position){ return discounts.get(position).getPrice(); }
    public String getRestaurant(int position){ return discounts.get(position).getRestaurant(); }
    public String getImage(int position){ return discounts.get(position).getImage(); }
    public String getDescription(int position){ return discounts.get(position).getDescription(); }
    public String getAddress(int position){ return discounts.get(position).getAddress(); }
    public int getQuantity(int position){ return discounts.get(position).getQuantity(); }
    public int getCartQuantity(int position){ return discounts.get(position).getCartQuantity(); }

    public void setQuantity(int position, int quantity){ discounts.get(position).setQuantity(quantity);}
    public void setCartQuantity(int position, int cartQuantity){ discounts.get(position).setCartQuantity(cartQuantity);}

    public void newItem(String id, String name, String restaurant, String address, String description,
                        String price, String quantity, String image, String cartQuantity){
        discounts.add(new Discount(Integer.parseInt(id), name, restaurant, address, description,
                Double.parseDouble(price), Integer.parseInt(quantity), image, Integer.parseInt(cartQuantity)));
        notifyDataSetChanged();
    }

    public void sortDiscounts(int sortBy){
        switch (sortBy){
            case 0: //alphabetical
                Collections.sort(discounts, new Comparator<Discount>(){
                    public int compare( Discount o1, Discount o2){
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                notifyDataSetChanged();
                break;
            case 1: //Fix this to actually sort by Distance
                Collections.sort(discounts, new Comparator<Discount>(){
                    public int compare( Discount o1, Discount o2){
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                notifyDataSetChanged();
                break;
            case 2: //high to low
                Collections.sort(discounts, new Comparator<Discount>(){
                    public int compare( Discount o1, Discount o2){
                        return -1*o1.getPrice().compareTo(o2.getPrice());
                    }
                });
                notifyDataSetChanged();
                break;
            case 3: //low to high
                Collections.sort(discounts, new Comparator<Discount>(){
                    public int compare( Discount o1, Discount o2){
                        return o1.getPrice().compareTo(o2.getPrice());
                    }
                });
                notifyDataSetChanged();
                break;
        }
    }

    public class Holder{
        TextView in, rn, pr, dl;
        ImageView im;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Holder holder = new ListAdapterDeals.Holder();
        final View rowView = inflater.inflate(R.layout.list_deals, null);
        holder.in=(TextView) rowView.findViewById(R.id.lblDiscountName);
        holder.rn=(TextView) rowView.findViewById(R.id.lblDiscountRest);
        holder.pr=(TextView) rowView.findViewById(R.id.lblDiscountPrice);
        holder.dl=(TextView) rowView.findViewById(R.id.lblDiscountQuantity);
        holder.im=(ImageView) rowView.findViewById(R.id.imgDiscountPicture);

        holder.in.setText(discounts.get(position).getName());
        holder.rn.setText(discounts.get(position).getRestaurant());
        holder.pr.setText("$"+discounts.get(position).getPrice());
        int left = (discounts.get(position).getQuantity() - discounts.get(position).getCartQuantity());
        if(left>0) holder.dl.setText(left+" left");
        else holder.dl.setText("Sold Out");
        Glide.with(context).load(discounts.get(position).getImage()).into(holder.im);

        return rowView;
    }

    public class Discount{
        private String name, restaurant, address, image, description;
        private double price;
        private int id, quantity, cartQuantity;

        Discount(){
            name = "";
            price = 0.00;
            restaurant = "";
            image = "";
            description = "";
            address = "";
            quantity = 0;
            id = 0;
            cartQuantity = 0;
        }

        Discount(int id, String name, String restaurant, String address, String description,
                 Double price, int quantity, String image, int cartQuantity){
            this.id = id;
            this.name = name;
            this.restaurant = restaurant;
            this.address = address;
            this.description = description;
            this.price  = price;
            this.quantity = quantity;
            this.image = image;
            this.cartQuantity = cartQuantity;
        }
        int getId(){return id;}
        String getName(){return name;}
        Double getPrice(){return price;}
        String getRestaurant(){return restaurant;}
        String getImage(){return image;}
        String getDescription(){return description;}
        String getAddress(){return address;}
        int getQuantity(){return quantity;}
        int getCartQuantity(){return cartQuantity;}

        void setName(String name){this.name = name;}
        void setPrice(Double price){this.price = price;}
        void setRestaurant(String restaurant){this.restaurant = restaurant;}
        void setImage(String image){this.image = image;}
        void setDescription(String description){this.description = description;}
        void setAddress(String address){this.address = address;}
        void setQuantity(int quantity){this.quantity = quantity;}
        void setCartQuantity(int cartQuantity){this.cartQuantity = cartQuantity;}
    }
}