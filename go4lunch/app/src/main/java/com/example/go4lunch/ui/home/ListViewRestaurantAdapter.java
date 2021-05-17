package com.example.go4lunch.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.Restaurant;

import java.util.List;

public class ListViewRestaurantAdapter extends RecyclerView.Adapter<ListViewRestaurantViewHolder> {

    private List<Restaurant> restaurants;

    public ListViewRestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ListViewRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant, parent, false);
        ListViewRestaurantViewHolder listViewRestaurantViewHolder = new ListViewRestaurantViewHolder(view);
        return listViewRestaurantViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewRestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.textViewRestaurantName.setText(restaurant.getName());
        holder.textViewRestaurantInfo.setText(restaurant.getInfo());
        holder.textViewRestaurantHours.setText(restaurant.getHours());
        holder.textViewRestaurantDistance.setText(restaurant.getDistance());
        holder.textViewRestaurantWorkmate.setText("(" + restaurant.getWorkmate() + ")");

        /*
        holder.imageViewRestaurantRating1.setBackgroundTintList(
            holder.imageViewRestaurantRating1.getContext().getResources().getColorStateList(R.color.colorAccent));
        holder.imageViewRestaurantRating2.setBackgroundTintList(
            holder.imageViewRestaurantRating1.getContext().getResources().getColorStateList(R.color.colorAccent));
        holder.imageViewRestaurantRating3.setBackgroundTintList(
            holder.imageViewRestaurantRating1.getContext().getResources().getColorStateList(R.color.colorAccent));
         */
        if (restaurant.getUrlPicture() == null) {
            // Clear picture
            Glide.with(holder.imageViewRestaurant.getContext())
                    .load("")
                    .placeholder(R.drawable.ic_baseline_restaurant_24)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(holder.imageViewRestaurant);
        } else {
            //load restaurant picture
            Glide.with(holder.imageViewRestaurant.getContext())
                    .load(restaurant.getUrlPicture())
                    .apply(RequestOptions.fitCenterTransform())
                    .into(holder.imageViewRestaurant);
        }
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}
