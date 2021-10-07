package com.example.gtlabgo4lunch.data.googleplace.model.autocomplete;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainTextMatchedSubstring implements Serializable
{

    @SerializedName("length")
    @Expose
    private int length;
    @SerializedName("offset")
    @Expose
    private int offset;
    private final static long serialVersionUID = -2016017220262010622L;

    /**
     * No args constructor for use in serialization
     *
     */
    public MainTextMatchedSubstring() {
    }

    /**
     *
     * @param offset
     * @param length
     */
    public MainTextMatchedSubstring(int length, int offset) {
        super();
        this.length = length;
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}

