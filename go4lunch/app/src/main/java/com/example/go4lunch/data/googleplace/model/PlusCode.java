package com.example.go4lunch.data.googleplace.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlusCode implements Serializable
{

    @SerializedName("compound_code")
    @Expose
    private String compoundCode;
    @SerializedName("global_code")
    @Expose
    private String globalCode;
    private final static long serialVersionUID = -8958827678573281285L;

    /**
     * No args constructor for use in serialization
     *
     */
    public PlusCode() {
    }

    /**
     *
     * @param globalCode
     * @param compoundCode
     */
    public PlusCode(String compoundCode, String globalCode) {
        super();
        this.compoundCode = compoundCode;
        this.globalCode = globalCode;
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    public PlusCode withCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
        return this;
    }

    public String getGlobalCode() {
        return globalCode;
    }

    public void setGlobalCode(String globalCode) {
        this.globalCode = globalCode;
    }

    public PlusCode withGlobalCode(String globalCode) {
        this.globalCode = globalCode;
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.compoundCode == null)? 0 :this.compoundCode.hashCode()));
        result = ((result* 31)+((this.globalCode == null)? 0 :this.globalCode.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PlusCode) == false) {
            return false;
        }
        PlusCode rhs = ((PlusCode) other);
        return (((this.compoundCode == rhs.compoundCode)||((this.compoundCode!= null)&&this.compoundCode.equals(rhs.compoundCode)))&&((this.globalCode == rhs.globalCode)||((this.globalCode!= null)&&this.globalCode.equals(rhs.globalCode))));
    }

}
