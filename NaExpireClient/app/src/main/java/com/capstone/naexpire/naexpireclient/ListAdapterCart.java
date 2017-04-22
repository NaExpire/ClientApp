package com.capstone.naexpire.naexpireclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ListAdapterCart extends BaseAdapter {

    ArrayList<Item> items;
    FragmentShoppingCart cart;
    Context context;
    ListAdapterCart.Holder holder;

    private static LayoutInflater inflater = null;

    public ListAdapterCart(Context c, FragmentShoppingCart cart) {
        items = new ArrayList<>();
        this.cart = cart;
        context = c;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView in, rn, pr, qu;
        ImageView im;
        ImageButton ex;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = new ListAdapterCart.Holder();
        final View rowView = inflater.inflate(R.layout.list_shopping_cart, null);
        holder.in = (TextView) rowView.findViewById(R.id.lblCartName);
        holder.rn = (TextView) rowView.findViewById(R.id.lblCartRestName);
        holder.pr = (TextView) rowView.findViewById(R.id.lblCartPrice);
        holder.qu = (TextView) rowView.findViewById(R.id.lblCartQuantity);
        holder.im = (ImageView) rowView.findViewById(R.id.imgCart);
        holder.ex = (ImageButton) rowView.findViewById(R.id.imgbtnCartDelete);

        holder.in.setText(items.get(position).getName());
        holder.rn.setText(items.get(position).getRestaurant());
        holder.pr.setText("$"+items.get(position).getPrice()+" each");
        holder.qu.setText(items.get(position).getCartQuantity()+" -");
        Glide.with(context).load(items.get(position).getImage()).into(holder.im);

        holder.ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
                cart.setSubtotal();
            }
        });

        return rowView;
    }

    public String updateSubtotal(){
        double sum = 0.00;
        for(int i = 0; i < items.size(); i++){
            sum += items.get(i).getPrice() * items.get(i).getCartQuantity();
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return "Subtotal: $"+decimalFormat.format(sum);
    }

    public int getSize(){return items.size();}
    public int getId(int position){ return items.get(position).getId(); }
    public String getName(int position){ return items.get(position).getName(); }
    public Double getPrice(int position){ return items.get(position).getPrice(); }
    public String getRestaurant(int position){ return items.get(position).getRestaurant(); }
    public String getImage(int position){ return items.get(position).getImage(); }
    public String getDescription(int position){ return items.get(position).getDescription(); }
    public String getAddress(int position){ return items.get(position).getAddress(); }
    public int getQuantity(int position){ return items.get(position).getQuantity(); }
    public int getCartQuantity(int position){ return items.get(position).getCartQuantity(); }

    public void setQuantity(int position, int quantity){ items.get(position).setQuantity(quantity);}
    public void setCartQuantity(int position, int cartQuantity){ items.get(position).setCartQuantity(cartQuantity);}

    public void newItem(String id, String name, String restaurant, String address, String description,
                        String price, String quantity, String image, String cartQuantity){
        items.add(new Item(Integer.parseInt(id), name, restaurant, address, description,
                Double.parseDouble(price), Integer.parseInt(quantity), image, Integer.parseInt(cartQuantity)));
        notifyDataSetChanged();
    }

    public String getAllItemNames(){
        String names = "";
        if(items.size()>0){
            names += items.get(0).getName();
            for(int i = 1; i < items.size(); i++){
                names += ","+items.get(i).getName();
            }
            return names;
        }
        else return getName(0);
    }
    public String getAllRestaurantNames(){
        String names = "";
        if(items.size()>0){
            names += items.get(0).getRestaurant();
            for(int i = 1; i < items.size(); i++){
                names += ","+items.get(i).getRestaurant();
            }
            return names;
        }
        else return getRestaurant(0);
    }
    public String getAllPrices(){
        String prices = "";
        if(items.size()>0) {
            prices += items.get(0).getPrice();
            for(int i = 1; i < items.size(); i++){
                prices += ","+items.get(i).getPrice();
            }
            return prices;
        }
        else return ""+getPrice(0);
    }
    public String getAllQuantities(){
        String quantities = "";
        if(items.size()>0){
            quantities += items.get(0).getQuantity();
            for(int i = 1; i < items.size(); i++){
                quantities += ","+items.get(i).getQuantity();
            }
            return quantities;
        }
        else return ""+getQuantity(0);
    }

    public void deleteItem(int position){
        cart.deleteItem(position);
        items.remove(position);
        notifyDataSetChanged();
    }

    public class Item{
        private String name, restaurant, address, image, description;
        private double price;
        private int id, quantity, cartQuantity;

        Item(){
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

        Item(int id, String name, String restaurant, String address, String description,
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