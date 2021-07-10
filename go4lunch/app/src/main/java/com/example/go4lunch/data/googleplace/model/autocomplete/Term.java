package com.example.go4lunch.data.googleplace.model.autocomplete;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Term implements Serializable
{

    @SerializedName("offset")
    @Expose
    private int offset;
    @SerializedName("value")
    @Expose
    private String value;
    private final static long serialVersionUID = 6296580836624556664L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Term() {
    }

    /**
     *
     * @param offset
     * @param value
     */
    public Term(int offset, String value) {
        super();
        this.offset = offset;
        this.value = value;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
