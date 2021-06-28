package com.example.go4lunch.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.ui.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class ListViewRestaurantAdapter extends RecyclerView.Adapter<ListViewRestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private OnClickListenerRestaurant onClickListenerRestaurant;

    public ListViewRestaurantAdapter(OnClickListenerRestaurant onClickListenerRestaurant) {
        this.restaurants = new ArrayList<>();
        this.onClickListenerRestaurant = onClickListenerRestaurant;
    }

    @NonNull
    @Override
    public ListViewRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_view_restaurant, parent, false);
        ListViewRestaurantViewHolder listViewRestaurantViewHolder = new ListViewRestaurantViewHolder(view);

        listViewRestaurantViewHolder.cardViewRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = listViewRestaurantViewHolder.getAdapterPosition();
                Restaurant restaurant = restaurants.get(position);
                onClickListenerRestaurant.onCLickRestaurant(restaurant);
            }
        });
        return listViewRestaurantViewHolder;
    }

    private int getStarColorByLevel(Context context, int count, int level){
        if (count < level) {
            return ContextCompat.getColor(context, R.color.gray);
        } else {
            return ContextCompat.getColor(context, R.color.yellow);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewRestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.textViewRestaurantName.setText(restaurant.getName());
        holder.textViewRestaurantInfo.setText(restaurant.getInfo());
        holder.textViewRestaurantHours.setText(restaurant.getHours());
        holder.textViewRestaurantDistance.setText(restaurant.getFormatedDistance());
        holder.textViewRestaurantWorkmate.setText(String.format("(%d)",  restaurant.getWorkmatesCount()));

        holder.imageViewRestaurantStar1.setColorFilter(getStarColorByLevel(holder.imageViewRestaurantStar1.getContext(), restaurant.getCountLike(), 1));
        holder.imageViewRestaurantStar2.setColorFilter(getStarColorByLevel(holder.imageViewRestaurantStar2.getContext(), restaurant.getCountLike(), 2));
        holder.imageViewRestaurantStar3.setColorFilter(getStarColorByLevel(holder.imageViewRestaurantStar3.getContext(), restaurant.getCountLike(), 3));

        //todo : liste des restaurants : charger les horaires ou open/close

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

    public void updateData(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        this.notifyDataSetChanged();
    }
}
