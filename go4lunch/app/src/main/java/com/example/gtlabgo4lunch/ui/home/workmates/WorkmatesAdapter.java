package com.example.gtlabgo4lunch.ui.home.workmates;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.tag.Tag;
import com.example.gtlabgo4lunch.ui.home.listener.OnClickListenerRestaurant;
import com.example.gtlabgo4lunch.ui.main.model.Workmate;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private static final String TAG = Tag.TAG;

    private List<Workmate> workmates;
    private OnClickListenerRestaurant onClickListenerRestaurant;

    public WorkmatesAdapter(OnClickListenerRestaurant onClickListenerRestaurant) {
        this.workmates = new ArrayList<>();
        this.onClickListenerRestaurant = onClickListenerRestaurant;
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_workmate, parent, false);
        WorkmatesViewHolder workmatesViewHolder = new WorkmatesViewHolder(view);

        workmatesViewHolder.cardViewRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = workmatesViewHolder.getAdapterPosition();
                final Workmate workmate = workmates.get(position);
                final String placeId = workmate.getPlaceId();
                if ((placeId != null) && (!placeId.isEmpty()))
                    onClickListenerRestaurant.onCLickRestaurant(placeId);
            }
        });
        return workmatesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        Workmate workmate = workmates.get(position);
        holder.textViewUserInformation.setText(workmate.getText());

        if (workmate.getUserUrlPicture() == null) {
            // Clear picture
            Glide.with(holder.imageViewUserPicture.getContext())
                    .load("")
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.imageViewUserPicture);
        } else {
            //load user picture
            Glide.with(holder.imageViewUserPicture.getContext())
                    .load(workmate.getUserUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.imageViewUserPicture);
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "WorkmatesAdapter. getItemCount() called with users.size() = " + workmates.size());
        return workmates.size();
    }

    public void updateData(List<Workmate> workmates){
        this.workmates = workmates;
        this.notifyDataSetChanged();
    }
}
