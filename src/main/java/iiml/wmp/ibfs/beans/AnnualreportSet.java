
package iiml.wmp.ibfs.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnnualreportSet {

    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("report_date")
    @Expose
    private Integer reportDate;
    @SerializedName("link")
    @Expose
    private String link;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getReportDate() {
        return reportDate;
    }

    public void setReportDate(Integer reportDate) {
        this.reportDate = reportDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
