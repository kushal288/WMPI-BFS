
package iiml.wmp.ibfs.beans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Analysis {

    @SerializedName("remarks")
    @Expose
    private List<Object> remarks = null;
    @SerializedName("bad")
    @Expose
    private List<String> bad = null;
    @SerializedName("good")
    @Expose
    private List<String> good = null;

    public List<Object> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<Object> remarks) {
        this.remarks = remarks;
    }

    public List<String> getBad() {
        return bad;
    }

    public void setBad(List<String> bad) {
        this.bad = bad;
    }

    public List<String> getGood() {
        return good;
    }

    public void setGood(List<String> good) {
        this.good = good;
    }

}
