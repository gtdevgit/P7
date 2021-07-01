package com.example.go4lunch.ui.home.workmates;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.go4lunch.R;
import com.example.go4lunch.tag.Tag;

public class WorkmatesViewHolder extends ViewHolder {
    private static final String TAG = Tag.TAG;

    ImageView imageViewUserPicture;
    TextView textViewUserInformation;

    public WorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);
        Log.d(TAG, "WorkmatesViewHolder() called with: itemView = [" + itemView + "]");
        imageViewUserPicture = itemView.findViewById(R.id.row_workmate_user_picture);
        textViewUserInformation = itemView.findViewById(R.id.row_workmate_user_information);
    }

    public ImageView getImageViewUserPicture() {
        return imageViewUserPicture;
    }

    public TextView getTextViewUserInformation() {
        return textViewUserInformation;
    }
}
