package com.example.go4lunch.models;

import androidx.annotation.NonNull;

public class Autocomplete {
    private String data;

    public Autocomplete(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "Autocomplet{" +
                "data=" + data +
                "}";
    }
}
