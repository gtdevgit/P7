package com.example.go4lunch.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

public class ListViewRestaurantViewHolder extends RecyclerView.ViewHolder {

    CardView cardViewRestaurant;
    TextView textViewRestaurantName;
    TextView textViewRestaurantInfo;
    TextView textViewRestaurantHours;
    TextView textViewRestaurantDistance;
    TextView textViewRestaurantWorkmate;
    ImageView imageViewRestaurantStar1;
    ImageView imageViewRestaurantStar2;
    ImageView imageViewRestaurantStar3;

    ImageView imageViewRestaurant;

    public ListViewRestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        cardViewRestaurant = itemView.findViewById(R.id.row_restaurant_card_view);
        textViewRestaurantName = itemView.findViewById(R.id.row_restaurant_name);
        textViewRestaurantInfo = itemView.findViewById(R.id.row_restaurant_info);
        textViewRestaurantHours = itemView.findViewById(R.id.row_restaurant_hours) ;
        textViewRestaurantDistance = itemView.findViewById(R.id.row_restaurant_distance);
        textViewRestaurantWorkmate = itemView.findViewById(R.id.row_restaurant_workmate);
        imageViewRestaurantStar1 = itemView.findViewById(R.id.row_restaurant_star1);
        imageViewRestaurantStar2 = itemView.findViewById(R.id.row_restaurant_star2);
        imageViewRestaurantStar3 = itemView.findViewById(R.id.row_restaurant_star3);

        imageViewRestaurant = itemView.findViewById(R.id.row_restaurant_picture);
    }
}
