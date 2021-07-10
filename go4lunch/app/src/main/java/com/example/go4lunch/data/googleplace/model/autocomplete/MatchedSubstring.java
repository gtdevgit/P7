package com.example.go4lunch.data.googleplace.model.autocomplete;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchedSubstring implements Serializable
{

    @SerializedName("length")
    @Expose
    private int length;
    @SerializedName("offset")
    @Expose
    private int offset;
    private final static long serialVersionUID = -9169484653218652816L;

    /**
     * No args constructor for use in serialization
     *
     */
    public MatchedSubstring() {
    }

    /**
     *
     * @param offset
     * @param length
     */
    public MatchedSubstring(int length, int offset) {
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
