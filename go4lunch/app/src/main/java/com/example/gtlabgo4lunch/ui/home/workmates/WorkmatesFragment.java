package com.example.gtlabgo4lunch.ui.home.workmates;

import android.content.Intent;
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

import com.example.gtlabgo4lunch.R;
import com.example.gtlabgo4lunch.tag.Tag;
import com.example.gtlabgo4lunch.ui.detailrestaurant.view.DetailRestaurantActivity;
import com.example.gtlabgo4lunch.ui.home.listener.OnClickListenerRestaurant;
import com.example.gtlabgo4lunch.ui.main.viewmodel.MainViewModel;
import com.example.gtlabgo4lunch.ui.main.viewstate.MainViewState;

public class WorkmatesFragment extends Fragment {
    private static final String TAG = Tag.TAG;

    private MainViewModel mainViewModel;

    TextView TextViewUserList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WorkmatesAdapter workmatesAdapter;
    private ProgressBar workmateProgressBar;

    public WorkmatesFragment(){}

    public WorkmatesFragment(MainViewModel mainViewModel) {
        // Required empty public constructor
        this.mainViewModel = mainViewModel;
    }

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

        workmatesAdapter = new WorkmatesAdapter(new OnClickListenerRestaurant() {
            @Override
            public void onCLickRestaurant(String placeId) {
                showDetailRestaurant(placeId);
            }
        });
        recyclerView.setAdapter(workmatesAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureViewModel(){
        if (mainViewModel != null){
            mainViewModel.getMainViewStateMediatorLiveData().observe(getViewLifecycleOwner(), new Observer<MainViewState>() {
                @Override
                public void onChanged(MainViewState mainViewState) {
                    workmatesAdapter.updateData(mainViewState.getWorkmates());
                    workmateProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void showDetailRestaurant(String placeId){
        Intent intent;
        intent = new Intent(this.getActivity(), DetailRestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("placeid", placeId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onResume() called");
        mainViewModel.activateUsersRealTimeListener();
        mainViewModel.activateChosenRestaurantListener();
    }

    @Override
    public void onPause() {
        Log.d(Tag.TAG, "ListViewRestaurantFragment.onPause() called");
        mainViewModel.removeUsersRealTimeListener();
        mainViewModel.removerChosenRestaurantListener();
        super.onPause();
    }
}