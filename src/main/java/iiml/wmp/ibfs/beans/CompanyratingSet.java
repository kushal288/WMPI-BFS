
package iiml.wmp.ibfs.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanyratingSet {

    @SerializedName("rating__rating")
    @Expose
    private String ratingRating;
    @SerializedName("instrument")
    @Expose
    private String instrument;
    @SerializedName("rating__source")
    @Expose
    private String ratingSource;
    @SerializedName("link")
    @Expose
    private String link;

    public String getRatingRating() {
        return ratingRating;
    }

    public void setRatingRating(String ratingRating) {
        this.ratingRating = ratingRating;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getRatingSource() {
        return ratingSource;
    }

    public void setRatingSource(String ratingSource) {
        this.ratingSource = ratingSource;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
