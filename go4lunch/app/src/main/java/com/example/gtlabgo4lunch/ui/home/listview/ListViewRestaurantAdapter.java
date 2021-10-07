package com.example.gtlabgo4lunch.ui.home.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.ui.home.listener.OnClickListenerRestaurant;
import com.example.gtlabgo4lunch.ui.main.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class ListViewRestaurantAdapter extends RecyclerView.Adapter<ListViewRestaurantViewHolder> implements Filterable {

    private List<Restaurant> restaurantsFull;
    private List<Restaurant> restaurantsFiltered;
    private OnClickListenerRestaurant onClickListenerRestaurant;
    private Context context;

    public ListViewRestaurantAdapter(Context context, OnClickListenerRestaurant onClickListenerRestaurant) {
        this.context = context;
        restaurantsFull = new ArrayList<>();
        restaurantsFiltered = new ArrayList<>();
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
                String placeId = restaurantsFiltered.get(position).getPlaceId();
                onClickListenerRestaurant.onCLickRestaurant(placeId);
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
        Restaurant restaurant = restaurantsFiltered.get(position);
        holder.textViewRestaurantName.setText(restaurant.getName());
        holder.textViewRestaurantInfo.setText(restaurant.getInfo());
        holder.textViewRestaurantHours.setText(context.getString(restaurant.getOpenNowResourceString()));
        holder.textViewRestaurantDistance.setText(restaurant.getFormatedDistance());
        holder.textViewRestaurantWorkmate.setText(String.format("(%d)",  restaurant.getWorkmatesCount()));

        holder.imageViewRestaurantStar1.setColorFilter(getStarColorByLevel(holder.imageViewRestaurantStar1.getContext(), restaurant.getCountLike(), 1));
        holder.imageViewRestaurantStar2.setColorFilter(getStarColorByLevel(holder.imageViewRestaurantStar2.getContext(), restaurant.getCountLike(), 2));
        holder.imageViewRestaurantStar3.setColorFilter(getStarColorByLevel(holder.imageViewRestaurantStar3.getContext(), restaurant.getCountLike(), 3));

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
        return restaurantsFiltered.size();
    }

    public void updateData(List<Restaurant> restaurants) {
        restaurantsFull = restaurants;
        updateDisplayedData(restaurants);
    }

    private void updateDisplayedData(List<Restaurant> restaurants){
        restaurantsFiltered.clear();
        restaurantsFiltered.addAll(restaurants);
        this.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return restaurantFilter;
    }

    private Filter restaurantFilter = new Filter() {
        /*
        worker thread
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Restaurant> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(restaurantsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Restaurant restaurant : restaurantsFull) {
                    if (restaurant.getName().toLowerCase().contains(filterPattern) ||
                            restaurant.getInfo().toLowerCase().contains(filterPattern)) {
                        filteredList.add(restaurant);
                       }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        /*
        ui thread
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            updateDisplayedData((List<Restaurant>) results.values);
        }
    };
}
