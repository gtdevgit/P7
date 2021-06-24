package com.example.go4lunch.ui.home;

import android.os.Bundle;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.example.go4lunch.data.firestore.model.User;
import com.example.go4lunch.tag.Tag;

import java.util.List;

public class WorkmatesFragment extends Fragment {
    private static final String TAG = Tag.TAG;

    private WorkmatesViewModel workmatesViewModel;

    TextView TextViewUserList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WorkmatesAdapter workmatesAdapter;
    private ProgressBar workmateProgressBar;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    // Todo: Workmates, adapter le style du texte en fonction du choix ou non d'un restaurant par le workmate. Utiliser Body1 quand un choix n'a pas été fait et Body 2 quand un restaurant à été choisi (android:textAppearance="@style/TextAppearance.AppCompat.Body1")
    // Todo : Si le workmate a choisi un restaurant afficher un idicateur
    // Todo : Optionnel : Ouvrir le détail du restaurant choisi.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "WorkmatesFragment.onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_workmates, container, false);
        workmateProgressBar = root.findViewById(R.id.fragment_workmates_progress_bar);

        configureRecyclerView(root);
        configureViewModel();
        loadViewModel();

        return root;
    }

    private void configureRecyclerView(View view){
        recyclerView = view.findViewById(R.id.fragment_workmates_recyclerview);

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        workmatesAdapter = new WorkmatesAdapter();
        recyclerView.setAdapter(workmatesAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureViewModel(){
        workmatesViewModel = new ViewModelProvider(this).get(WorkmatesViewModel.class);

        workmatesViewModel.getWorkmatesLiveData().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d(TAG, "WorkmatesFragment: onChanged() users.size() = [" + users.size() + "]");
                workmatesAdapter.updateData(users);
                workmateProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void loadViewModel(){
        workmateProgressBar.setVisibility(View.VISIBLE);
        workmatesViewModel.loadWorkmates();
    }

    @Override
    public void onResume() {
        super.onResume();
        workmatesViewModel.activateWorkmatesListener();
        Log.d(TAG, "WorkmatesFragment.onResume() called");

    }

    @Override
    public void onPause() {
        Log.d(TAG, "WorkmatesFragment.onPause() called");
        workmatesViewModel.removeWorkmatesListener();
        super.onPause();
    }
}