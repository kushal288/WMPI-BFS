
package iiml.wmp.ibfs.beans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScreenerBean {

    @SerializedName("prime")
    @Expose
    private String prime;
    @SerializedName("number_set")
    @Expose
    private NumberSet numberSet;
    @SerializedName("bse_code")
    @Expose
    private String bseCode;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("nse_code")
    @Expose
    private String nseCode;
    @SerializedName("companyrating_set")
    @Expose
    private List<CompanyratingSet> companyratingSet = null;
    @SerializedName("annualreport_set")
    @Expose
    private List<AnnualreportSet> annualreportSet = null;
    @SerializedName("announcement_set")
    @Expose
    private List<AnnouncementSet> announcementSet = null;
    @SerializedName("warehouse_set")
    @Expose
    private WarehouseSet warehouseSet;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;

    private ScreenerStockPrice screenerStockPrice;

	private StockPricesEventDays stockPricesEventDays;

	public StockPricesEventDays getStockPricesEventDays()
	{
		return stockPricesEventDays;
	}

	public void setStockPricesEventDays(StockPricesEventDays stockPricesEventDays)
	{
		this.stockPricesEventDays = stockPricesEventDays;
	}

	public ScreenerStockPrice getScreenerStockPrice()
    {
        return screenerStockPrice;
    }

    public void setScreenerStockPrice(ScreenerStockPrice screenerStockPrice)
    {
        this.screenerStockPrice = screenerStockPrice;
    }

    public String getPrime() {
        return prime;
    }

    public void setPrime(String prime) {
        this.prime = prime;
    }

    public NumberSet getNumberSet() {
        return numberSet;
    }

    public void setNumberSet(NumberSet numberSet) {
        this.numberSet = numberSet;
    }

    public String getBseCode() {
        return bseCode;
    }

    public void setBseCode(String bseCode) {
        this.bseCode = bseCode;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getNseCode() {
        return nseCode;
    }

    public void setNseCode(String nseCode) {
        this.nseCode = nseCode;
    }

    public List<CompanyratingSet> getCompanyratingSet() {
        return companyratingSet;
    }

    public void setCompanyratingSet(List<CompanyratingSet> companyratingSet) {
        this.companyratingSet = companyratingSet;
    }

    public List<AnnualreportSet> getAnnualreportSet() {
        return annualreportSet;
    }

    public void setAnnualreportSet(List<AnnualreportSet> annualreportSet) {
        this.annualreportSet = annualreportSet;
    }

    public List<AnnouncementSet> getAnnouncementSet() {
        return announcementSet;
    }

    public void setAnnouncementSet(List<AnnouncementSet> announcementSet) {
        this.announcementSet = announcementSet;
    }

    public WarehouseSet getWarehouseSet() {
        return warehouseSet;
    }

    public void setWarehouseSet(WarehouseSet warehouseSet) {
        this.warehouseSet = warehouseSet;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
