
package iiml.wmp.ibfs.beans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NumberSet {

    @SerializedName("balancesheet")
    @Expose
    private List<List<Object>> balancesheet = null;
    @SerializedName("annual")
    @Expose
    private List<List<Object>> annual = null;
    @SerializedName("cashflow")
    @Expose
    private List<List<Object>> cashflow = null;
    @SerializedName("quarters")
    @Expose
    private List<List<Object>> quarters = null;

    public List<List<Object>> getBalancesheet()
    {
        return balancesheet;
    }

    public void setBalancesheet(List<List<Object>> balancesheet)
    {
        this.balancesheet = balancesheet;
    }

    public List<List<Object>> getAnnual()
    {
        return annual;
    }

    public void setAnnual(List<List<Object>> annual)
    {
        this.annual = annual;
    }

    public List<List<Object>> getCashflow()
    {
        return cashflow;
    }

    public void setCashflow(List<List<Object>> cashflow)
    {
        this.cashflow = cashflow;
    }

    public List<List<Object>> getQuarters()
    {
        return quarters;
    }

    public void setQuarters(List<List<Object>> quarters)
    {
        this.quarters = quarters;
    }
}
