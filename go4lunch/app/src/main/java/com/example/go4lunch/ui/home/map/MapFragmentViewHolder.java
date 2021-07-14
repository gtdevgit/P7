package com.example.go4lunch.ui.home.map;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

import org.jetbrains.annotations.NotNull;

public class MapFragmentViewHolder extends RecyclerView.ViewHolder{

    CardView cardView;
    TextView textViewDescription;
    ImageView imageViewMarker;

    public MapFragmentViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        cardView = itemView.findViewById(R.id.row_search_view_result_cardview);
        textViewDescription = itemView.findViewById(R.id.row_search_view_result_description);
        imageViewMarker = itemView.findViewById(R.id.row_search_view_result_marker);
    }
}
