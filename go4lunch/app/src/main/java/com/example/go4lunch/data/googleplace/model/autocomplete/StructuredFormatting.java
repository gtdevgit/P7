package com.example.go4lunch.data.googleplace.model.autocomplete;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructuredFormatting implements Serializable
{

    @SerializedName("main_text")
    @Expose
    private String mainText;
    @SerializedName("main_text_matched_substrings")
    @Expose
    private List<MainTextMatchedSubstring> mainTextMatchedSubstrings = null;
    @SerializedName("secondary_text")
    @Expose
    private String secondaryText;
    private final static long serialVersionUID = 4468361396320727310L;

    /**
     * No args constructor for use in serialization
     *
     */
    public StructuredFormatting() {
    }

    /**
     *
     * @param mainText
     * @param mainTextMatchedSubstrings
     * @param secondaryText
     */
    public StructuredFormatting(String mainText, List<MainTextMatchedSubstring> mainTextMatchedSubstrings, String secondaryText) {
        super();
        this.mainText = mainText;
        this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
        this.secondaryText = secondaryText;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public List<MainTextMatchedSubstring> getMainTextMatchedSubstrings() {
        return mainTextMatchedSubstrings;
    }

    public void setMainTextMatchedSubstrings(List<MainTextMatchedSubstring> mainTextMatchedSubstrings) {
        this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }

}
