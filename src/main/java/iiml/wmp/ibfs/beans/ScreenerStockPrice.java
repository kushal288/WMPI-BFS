package iiml.wmp.ibfs.beans;

import java.util.List;

public class ScreenerStockPrice
{

	private List<Double> prices;
	private List<String> dates;
	private String id;

	public List<Double> getPrices()
	{
		return prices;
	}

	public void setPrices(List<Double> prices)
	{
		this.prices = prices;
	}

	public List<String> getDates()
	{
		return dates;
	}

	public void setDates(List<String> dates)
	{
		this.dates = dates;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
