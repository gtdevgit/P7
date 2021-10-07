package com.example.gtlabgo4lunch.ui.detailrestaurant.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.ui.detailrestaurant.viewstate.SimpleUserViewState;
import com.example.gtlabgo4lunch.ui.home.workmates.WorkmatesViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SimpleWorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder>{

    private List<SimpleUserViewState> users;

    public SimpleWorkmatesAdapter() {
        this.users = new ArrayList<>();
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_workmate, parent, false);
        WorkmatesViewHolder workmatesViewHolder = new WorkmatesViewHolder(view);
        return workmatesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        SimpleUserViewState user = users.get(position);
        holder.getTextViewUserInformation().setText(user.getName());

        if (user.getUrlPicture() == null) {
            // Clear picture
            Glide.with(holder.getImageViewUserPicture().getContext())
                    .load("")
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.getImageViewUserPicture());
        } else {
            //load user picture
            Glide.with(holder.getImageViewUserPicture().getContext())
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.getImageViewUserPicture());
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateData(List<SimpleUserViewState> users){
        this.users = users;
        this.notifyDataSetChanged();
    }
}
