package com.example.go4lunch.ui.home.workmates;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.example.go4lunch.tag.Tag;
import com.example.go4lunch.ui.main.viewmodel.MainViewModel;
import com.example.go4lunch.ui.main.viewstate.MainViewState;

public class WorkmatesFragment extends Fragment {
    private static final String TAG = Tag.TAG;

    private MainViewModel mainViewModel;

    TextView TextViewUserList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WorkmatesAdapter workmatesAdapter;
    private ProgressBar workmateProgressBar;

    public WorkmatesFragment(MainViewModel mainViewModel) {
        // Required empty public constructor
        this.mainViewModel = mainViewModel;

    }

    // Todo: Workmates, adapter le style du texte en fonction du choix ou non d'un restaurant par le workmate. Utiliser Body1 quand un choix n'a pas été fait et Body 2 quand un restaurant à été choisi (android:textAppearance="@style/TextAppearance.AppCompat.Body1")
    // Todo : Si le workmate a choisi un restaurant afficher un indicateur
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
        mainViewModel.getMainViewStateMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<MainViewState>() {
            @Override
            public void onChanged(MainViewState mainViewState) {
                workmatesAdapter.updateData(mainViewState.getWorkmates());
                workmateProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onResume() called");
        mainViewModel.activateUsersRealTimeListener();
    }

    @Override
    public void onPause() {
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onPause() called");
        mainViewModel.removeUsersRealTimeListener();
        super.onPause();
    }
}