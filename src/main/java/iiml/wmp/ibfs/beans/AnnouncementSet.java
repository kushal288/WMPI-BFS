
package iiml.wmp.ibfs.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnnouncementSet {

    @SerializedName("ann_date")
    @Expose
    private String annDate;
    @SerializedName("announcement")
    @Expose
    private String announcement;
    @SerializedName("link")
    @Expose
    private String link;

    public String getAnnDate() {
        return annDate;
    }

    public void setAnnDate(String annDate) {
        this.annDate = annDate;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
