package iiml.wmp.ibfs.utils;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import iiml.wmp.ibfs.beans.ExcelBean;
import iiml.wmp.ibfs.beans.ScreenerBean;
import iiml.wmp.ibfs.beans.ScreenerStockPrice;
import iiml.wmp.ibfs.beans.StockPricesEventDays;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;

public class ExcelWriter
{
	List<String> header = Lists.newArrayList(
			"Date",
			"Share Capital",
			"Reserves",
			"Borrowings",
			"Other Liabilities",
			"Total Liabilities",
			"Fixed Assets",
			"CWIP",
			"Investments",
			"Other Assets",
			"Total Assets",
			"Sales",
			"Expenses",
			"Operating Profit",
			"OPM",
			"Other Income",
			"Interest",
			"Depreciation",
			"Profit before tax",
			"Tax",
			"Net Profit",
			"EPS (unadj)",
			"Dividend Payout",
			"Cash from Operating Activity",
			"Cash from Investing Activity",
			"Cash from Financing Activity",
			"Net Cash Flow");

	static List<String> dates = Lists.newArrayList(
			"Dates",
			"2017-03-31",
			"2016-03-31",
			"2015-03-31",
			"2014-03-31",
			"2013-03-31",
			"2012-03-31",
			"2011-03-31",
			"2010-03-31",
			"2009-03-31",
			"2008-03-31",
			"2007-03-31",
			"2006-03-31",
			"2005-03-31");

	static List<String> headR = Lists.newArrayList("company","op/asset", "ebit/asset", "ebit/sales","sales/asset","ROE");

	public static void writeDataToExcel(Workbook wb, Map<String, List<? extends Object>> data, Integer year) throws Exception
	{
		Sheet sheet = wb.createSheet("Year = "+year);
		int rowCount = 0;
		Row headerRow = sheet.createRow(rowCount);
		for (int i = 0; i < headR.size(); i++)
		{
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headR.get(i));
		}
		rowCount++;
		int rowSize = data.get("company").size();
		for (int i = 0; i <rowSize ; i++, rowCount++)
		{
			Row dataRow = sheet.createRow(rowCount);
			for (int j = 0; j <headR.size() ; j++)
			 {
				Object val = data.get(headR.get(j)).get(i);
				Cell dataCell = dataRow.createCell(j);
				 dataCell.setCellValue(String.valueOf(val));
			}
			
		}
	}

	public static void writeDataToExcel(Workbook wb, ScreenerBean sb) throws Exception
	{
		int rowCount = 0;
		Sheet sheet = wb.createSheet(sb.getShortName());
		Row headerRow = sheet.createRow(rowCount);
		for (int i = 0; i < dates.size(); i++)
		{
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(dates.get(i));
		}
		rowCount++;
		rowCount = createRows(sheet, sb.getNumberSet().getBalancesheet(), rowCount);
		sheet.createRow(rowCount);
		rowCount++;
		rowCount = createRows(sheet, sb.getNumberSet().getAnnual(), rowCount);
		sheet.createRow(rowCount);
		rowCount++;
		rowCount = createRows(sheet, sb.getNumberSet().getCashflow(), rowCount);
		sheet.createRow(rowCount);
		rowCount++;
		Row eb = sheet.createRow(rowCount);
		int idx = 1;
		idx = createCells(eb, "FV", sb.getExcelBean().getFV(), idx);
		idx = createCells(eb, "Premium", sb.getExcelBean().getPremium(), idx);
		idx = createCells(eb, "Subscription Price", getSubsPrice(sb.getExcelBean()), idx);
		idx = createCells(eb, "Announce Date", sb.getExcelBean().getAnnounced(), idx);
		idx = createCells(eb, "Ex-Rights Date", sb.getExcelBean().getEx_Rights(), idx);
		idx = createCells(eb, "Record Date", sb.getExcelBean().getRecord(), idx);
		rowCount++;
		sheet.createRow(rowCount);
		rowCount++;

		List<String> stockprice = Lists.newArrayList("Stock Prices");
		rowCount = createRow(sheet, stockprice, rowCount);
		ScreenerStockPrice ssp = sb.getScreenerStockPrice();
		rowCount = createRow(sheet, ssp.getDates(), rowCount);
		rowCount = createRow(sheet, ssp.getPrices(), rowCount);
		sheet.createRow(rowCount);
		rowCount++;

		StockPricesEventDays stockPricesEventDays = sb.getStockPricesEventDays();

		List<String> annc = Lists.newArrayList("Announcement Stock Prices");
		rowCount = createRow(sheet, annc, rowCount);
		rowCount = updateeventStockPrices(sheet, stockPricesEventDays.getAnncStocks(), rowCount);
		sheet.createRow(rowCount);
		rowCount++;

		List<String> exRights = Lists.newArrayList("Ex-Rights Stock Prices");
		rowCount = createRow(sheet, exRights, rowCount);
		rowCount = updateeventStockPrices(sheet, stockPricesEventDays.getExRightsStocks(), rowCount);
		sheet.createRow(rowCount);
		rowCount++;

		List<String> rec = Lists.newArrayList("Record Date Stock Prices");
		rowCount = createRow(sheet, rec, rowCount);
		rowCount = updateeventStockPrices(sheet, stockPricesEventDays.getRecordStocks(), rowCount);
		sheet.createRow(rowCount);
		rowCount++;

	}

	private static int updateeventStockPrices(Sheet sheet, Map<String, Double> data, int rowCount) throws Exception
	{
		if(data==null || data.isEmpty())
			throw new Exception("Null or empty data.. Skipp!");
		TreeMap<String, Double> sorted = new TreeMap<>(data);
		List<String> dates = new ArrayList<>();
		List<Double> prices = new ArrayList<>();
		for (String key : sorted.keySet())
		{
			dates.add(key);
			prices.add(sorted.get(key));
		}
		rowCount = createRow(sheet, dates, rowCount);
		rowCount = createRow(sheet, prices, rowCount);

		return rowCount;
	}

	private static int createRow(Sheet sheet, List<? extends Object> vals, int rowCount)
	{

		Row row = sheet.createRow(rowCount);
		for (int i = 0; i < vals.size(); i++)
		{
			Cell cell = row.createCell(i);
			cell.setCellValue(vals.get(i).toString());
		}
		return ++rowCount;
	}

	private static int createCells(Row row, String key, Object val, int startidx)
	{
		Cell keyCell = row.createCell(startidx);
		keyCell.setCellValue(key);
		startidx++;
		Cell valcall = row.createCell(startidx++);
		valcall.setCellValue(val.toString());
		return startidx++;
	}

	private static int getSubsPrice(ExcelBean eb)
	{
		try
		{
			return eb.getFV() + eb.getPremium();
		}
		catch (Exception e)
		{
			return 0;
		}

	}

	private static int createRows(Sheet sheet, List<List<Object>> bs, int rowCount)
	{
		for (List<Object> ls : bs)
		{
			Row datarow = sheet.createRow(rowCount);
			String key = (String) ls.get(0);
			Cell cell = datarow.createCell(0);
			cell.setCellValue(key);

			Map vals = (Map) ls.get(1);
			for (int i = 1; i < dates.size(); i++)
			{
				String date = dates.get(i);
				if (date == null)
				{
					continue;
				}

				Object val = vals.get(date);
				Cell datacell = datarow.createCell(i);
				if (val != null)
				{
					datacell.setCellValue(val.toString());
				}
				else
				{
					datacell.setCellValue("-");
				}
			}
			rowCount++;
		}
		return rowCount;
	}
}
