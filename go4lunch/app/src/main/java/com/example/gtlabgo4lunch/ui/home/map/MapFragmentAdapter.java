package com.example.gtlabgo4lunch.ui.home.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.ui.home.listener.OnClickListenerRestaurant;
import com.example.gtlabgo4lunch.ui.main.model.SearchViewResultItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MapFragmentAdapter extends RecyclerView.Adapter<MapFragmentViewHolder>{

    private List<SearchViewResultItem> searchViewResultItems;
    private OnClickListenerRestaurant onClickListenerRestaurant;

    public MapFragmentAdapter(OnClickListenerRestaurant onClickListenerRestaurant) {
        searchViewResultItems = new ArrayList<>();
        this.onClickListenerRestaurant = onClickListenerRestaurant;
    }

    @NonNull
    @NotNull
    @Override
    public MapFragmentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search_view_result, parent, false);
        MapFragmentViewHolder mapFragmentViewHolder = new MapFragmentViewHolder(view);

        mapFragmentViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mapFragmentViewHolder.getAdapterPosition();
                String placeId = searchViewResultItems.get(position).getPlaceId();
                // !!! c'est pas ça qui faut faire mais positionner la carte
                // manque la location
                onClickListenerRestaurant.onCLickRestaurant(placeId);
            }
        });
        return mapFragmentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MapFragmentViewHolder holder, int position) {
        SearchViewResultItem searchViewResultItem = searchViewResultItems.get(position);
        holder.textViewDescription.setText(searchViewResultItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return searchViewResultItems.size();
    }

    public void updateData(List<SearchViewResultItem> searchViewResultItems) {
        this.searchViewResultItems = searchViewResultItems;
        notifyDataSetChanged();
    }
}
