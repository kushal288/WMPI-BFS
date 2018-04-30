
package iiml.wmp.ibfs.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WarehouseSet {

    @SerializedName("high_price")
    @Expose
    private  Double highPrice;
    @SerializedName("low_price")
    @Expose
    private Float lowPrice;
    @SerializedName("sales_growth")
    @Expose
    private Float salesGrowth;
    @SerializedName("current_price")
    @Expose
    private Float currentPrice;
    @SerializedName("dividend_yield")
    @Expose
    private Float dividendYield;
    @SerializedName("face_value")
    @Expose
    private Double faceValue;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("sales_growth_3years")
    @Expose
    private Float salesGrowth3years;
    @SerializedName("profit_growth_5years")
    @Expose
    private Float profitGrowth5years;
    @SerializedName("average_return_on_equity_3years")
    @Expose
    private Float averageReturnOnEquity3years;
    @SerializedName("book_value")
    @Expose
    private Float bookValue;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("pair_url")
    @Expose
    private String pairUrl;
    @SerializedName("sales_growth_10years")
    @Expose
    private Float salesGrowth10years;
    @SerializedName("average_return_on_equity_10years")
    @Expose
    private Float averageReturnOnEquity10years;
    @SerializedName("profit_growth")
    @Expose
    private Float profitGrowth;
    @SerializedName("market_capitalization")
    @Expose
    private Float marketCapitalization;
    @SerializedName("profit_growth_10years")
    @Expose
    private Float profitGrowth10years;
    @SerializedName("price_to_earning")
    @Expose
    private Float priceToEarning;
    @SerializedName("industry")
    @Expose
    private String industry;
    @SerializedName("analysis")
    @Expose
    private Analysis analysis;
    @SerializedName("result_type")
    @Expose
    private String resultType;
    @SerializedName("profit_growth_3years")
    @Expose
    private Float profitGrowth3years;
    @SerializedName("sales_growth_5years")
    @Expose
    private Float salesGrowth5years;
    @SerializedName("return_on_equity")
    @Expose
    private Float returnOnEquity;
    @SerializedName("average_return_on_equity_5years")
    @Expose
    private Float averageReturnOnEquity5years;

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Float getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Float lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Float getSalesGrowth() {
        return salesGrowth;
    }

    public void setSalesGrowth(Float salesGrowth) {
        this.salesGrowth = salesGrowth;
    }

    public Float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Float getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(Float dividendYield) {
        this.dividendYield = dividendYield;
    }

    public Double getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Double faceValue) {
        this.faceValue = faceValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getSalesGrowth3years() {
        return salesGrowth3years;
    }

    public void setSalesGrowth3years(Float salesGrowth3years) {
        this.salesGrowth3years = salesGrowth3years;
    }

    public Float getProfitGrowth5years() {
        return profitGrowth5years;
    }

    public void setProfitGrowth5years(Float profitGrowth5years) {
        this.profitGrowth5years = profitGrowth5years;
    }

    public Float getAverageReturnOnEquity3years() {
        return averageReturnOnEquity3years;
    }

    public void setAverageReturnOnEquity3years(Float averageReturnOnEquity3years) {
        this.averageReturnOnEquity3years = averageReturnOnEquity3years;
    }

    public Float getBookValue() {
        return bookValue;
    }

    public void setBookValue(Float bookValue) {
        this.bookValue = bookValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPairUrl() {
        return pairUrl;
    }

    public void setPairUrl(String pairUrl) {
        this.pairUrl = pairUrl;
    }

    public Float getSalesGrowth10years() {
        return salesGrowth10years;
    }

    public void setSalesGrowth10years(Float salesGrowth10years) {
        this.salesGrowth10years = salesGrowth10years;
    }

    public Float getAverageReturnOnEquity10years() {
        return averageReturnOnEquity10years;
    }

    public void setAverageReturnOnEquity10years(Float averageReturnOnEquity10years) {
        this.averageReturnOnEquity10years = averageReturnOnEquity10years;
    }

    public Float getProfitGrowth() {
        return profitGrowth;
    }

    public void setProfitGrowth(Float profitGrowth) {
        this.profitGrowth = profitGrowth;
    }

    public Float getMarketCapitalization() {
        return marketCapitalization;
    }

    public void setMarketCapitalization(Float marketCapitalization) {
        this.marketCapitalization = marketCapitalization;
    }

    public Float getProfitGrowth10years() {
        return profitGrowth10years;
    }

    public void setProfitGrowth10years(Float profitGrowth10years) {
        this.profitGrowth10years = profitGrowth10years;
    }

    public Float getPriceToEarning() {
        return priceToEarning;
    }

    public void setPriceToEarning(Float priceToEarning) {
        this.priceToEarning = priceToEarning;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Float getProfitGrowth3years() {
        return profitGrowth3years;
    }

    public void setProfitGrowth3years(Float profitGrowth3years) {
        this.profitGrowth3years = profitGrowth3years;
    }

    public Float getSalesGrowth5years() {
        return salesGrowth5years;
    }

    public void setSalesGrowth5years(Float salesGrowth5years) {
        this.salesGrowth5years = salesGrowth5years;
    }

    public Float getReturnOnEquity() {
        return returnOnEquity;
    }

    public void setReturnOnEquity(Float returnOnEquity) {
        this.returnOnEquity = returnOnEquity;
    }

    public Float getAverageReturnOnEquity5years() {
        return averageReturnOnEquity5years;
    }

    public void setAverageReturnOnEquity5years(Float averageReturnOnEquity5years) {
        this.averageReturnOnEquity5years = averageReturnOnEquity5years;
    }

}
