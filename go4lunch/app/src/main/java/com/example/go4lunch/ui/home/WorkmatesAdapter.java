package com.example.go4lunch.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.tag.Tag;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {

    private static final String TAG = Tag.TAG;
    private List<User> users;

    public WorkmatesAdapter() {
        this.users = new ArrayList<>();
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_workmate, parent, false);
        WorkmatesViewHolder workmatesViewHolder = new WorkmatesViewHolder(view);
        return workmatesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder holder, int position) {
        User user = users.get(position);
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        Log.d(TAG, "onBindViewHolder() user.getUserName()  = [" + user.getUserName()+ "]");
        holder.textViewUserInformation.setText(user.getUserName() + " " + user.getUserEmail() );

        if (user.getUrlPicture() == null) {
            // Clear picture
            Glide.with(holder.imageViewUserPicture.getContext())
                    .load("")
                    .placeholder(R.drawable.ic_baseline_account_circle_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.imageViewUserPicture);
        } else {
            //load user picture
            Glide.with(holder.imageViewUserPicture.getContext())
                    .load(user.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.imageViewUserPicture);
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "WorkmatesAdapter. getItemCount() called with users.size() = " + users.size());
        return users.size();
    }

    public void updateData(List<User> users){
        this.users = users;
        this.notifyDataSetChanged();
    }
}
