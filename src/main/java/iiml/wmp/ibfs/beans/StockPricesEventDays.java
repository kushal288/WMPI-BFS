package iiml.wmp.ibfs.beans;

import java.util.Map;

public class StockPricesEventDays

{

	private Map<String, Double> exRightsStocks;
	private Map<String, Double> anncStocks;
	private Map<String, Double> recordStocks;

	public Map<String, Double> getExRightsStocks()
	{
		return exRightsStocks;
	}

	public void setExRightsStocks(Map<String, Double> exRightsStocks)
	{
		this.exRightsStocks = exRightsStocks;
	}

	public Map<String, Double> getAnncStocks()
	{
		return anncStocks;
	}

	public void setAnncStocks(Map<String, Double> anncStocks)
	{
		this.anncStocks = anncStocks;
	}

	public Map<String, Double> getRecordStocks()
	{
		return recordStocks;
	}

	public void setRecordStocks(Map<String, Double> recordStocks)
	{
		this.recordStocks = recordStocks;
	}
}
