package com.example.go4lunch.ui.home.search;


import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

public class SearchFragment extends Fragment {
    private SearchView searchView;

    public SearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }
}
