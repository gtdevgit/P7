package com.example.go4lunch.ui.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.logout.LogoutViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkmatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkmatesFragment extends Fragment {
    private static final String TAG = Tag.TAG;

    private WorkmatesViewModel workmatesViewModel;
    private List<User> usersList;

    TextView TextViewUserList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WorkmatesAdapter workmatesAdapter;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        workmatesViewModel = new ViewModelProvider(this).get(WorkmatesViewModel.class);

        recyclerView = root.findViewById(R.id.fragment_workmates_recyclerview);
        layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        usersList = new ArrayList<>();
        workmatesAdapter = new WorkmatesAdapter(usersList);
        recyclerView.setAdapter(workmatesAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        workmatesViewModel.getWorkmates().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d(TAG, "WorkmatesFragment: onChanged() called with: users = [" + users + "]");
                usersList.clear();
                usersList.addAll(users);
                Log.d(TAG, "WorkmatesFragment: onChanged() called with: usersList = [" + usersList + "]");
                Log.d(TAG, "WorkmatesFragment: onChanged() usersList.size() = [" + usersList.size() + "]");
                workmatesAdapter.notifyDataSetChanged();
            }
        });

        workmatesViewModel.loadData();
        return root;
    }
}