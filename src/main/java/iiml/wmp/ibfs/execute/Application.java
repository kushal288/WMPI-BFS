package iiml.wmp.ibfs.execute;

import iiml.wmp.ibfs.base.MoneyControlAPI;
import iiml.wmp.ibfs.beans.ExcelBean;
import iiml.wmp.ibfs.beans.ScreenerBean;
import iiml.wmp.ibfs.beans.StockPricesEventDays;
import iiml.wmp.ibfs.http.client.HttpClientApache;
import iiml.wmp.ibfs.utils.ExcelBeanUtils;
import iiml.wmp.ibfs.utils.ExcelWriter;
import iiml.wmp.ibfs.utils.TaskExecutor;
import iiml.wmp.ibfs.utils.TestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static iiml.wmp.ibfs.base.ScreenerAPI.getCompanyDetails;

public class Application
{
	public static void main(String[] args)
	{
		try
		{
			//FileUtils.deleteQuietly(new File("output"));
			/*List<ExcelBean> list = ExcelBeanUtils.getSheetContents("data.xlsm", "bestcomp", ExcelBean.class);


			Map<String, ExcelBean> map = new HashMap<>();
			for (ExcelBean eb : list)
			{
				try
				{
					map.put(eb.getCompany() + ".json", eb);
					System.out.println(eb);
					ScreenerBean sb = getCompanyDetails(eb.getCompany());
					StockPricesEventDays stockPricesEventDays = getStockPricesEventDays(eb);
					sb.setStockPricesEventDays(stockPricesEventDays);
					sb.setExcelBean(eb);
					writeDataToFile(eb.getCompany(), TestUtils.gson.toJson(sb));
				}
				catch (Exception e)
				{
					System.out.println("Error for: " + eb);
					e.printStackTrace();
				}

			}*/
			String[] arr = { "json" };
			Collection<File> files = FileUtils.listFiles(new File("output"), arr, false);
			List<ScreenerBean> beans = new ArrayList<>();
			for (File file : files)
			{
				ScreenerBean sb = TestUtils.gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), ScreenerBean.class);
				beans.add(sb);

			}
			earningsData(beans);
			//writeDataToExcel(beans);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			cleanUp();
		}
	}

	private static void earningsData(List<ScreenerBean> ls) throws Exception
	{
		int coount = 0;
		for (ScreenerBean sb : ls)
		{
			try
			{
				EarningsMangmtData.getEarningsData(sb);
			//	System.out.println("earnings Data: " + TestUtils.gson.toJson(sb));
				coount++;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		System.out.println("Total data: " + coount);

		int[] time = { -3, -2, -1, 0, 1, 2, 3, 4 , 5};
		Map<Integer, Map<String, List<? extends Object>>> earnDataMap = new HashMap<>();
		for (int i = 0; i < time.length; i++)
		{
			int yearInst = time[i];
			System.out.println("Calculating for ith"+yearInst);
			List<String> company = new ArrayList<>();
			List<Double> op_asset_list = new ArrayList<>();
			List<Double> ebit_asset_List = new ArrayList<>();
			List<Double> ebit_sales_List = new ArrayList<>();
			List<Double> sales_asset_List = new ArrayList<>();
			List<Double> roe_List = new ArrayList<>();
			Map<String, List<? extends Object>> iData = new HashMap<>();
			iData.put("op/asset", op_asset_list);
			iData.put("ebit/asset", ebit_asset_List);
			iData.put("ebit/sales", ebit_sales_List);
			iData.put("sales/asset",sales_asset_List ); iData.put("company", company);
			iData.put("ROE", roe_List);
			earnDataMap.put(yearInst, iData);
			for (ScreenerBean sb : ls)
			{
				System.out.println("Company: "+sb.getShortName());
				String exRightsDate = sb.getExcelBean().getEx_Rights();
				System.out.println("Exrights Date str: " + exRightsDate);
				Date exRights = getDateFormat(exRightsDate);
				LocalDate localDate = Instant.ofEpochMilli(exRights.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
				int year = localDate.getYear();
				System.out.println("Initial Year: " + year);
				LocalDate refDate = LocalDate.of(year, 3, 31);
				if (refDate.isAfter(localDate))
					year = year - 1;
				System.out.println("computed Year: " + year);
				LocalDate dateRequired = LocalDate.of(year + yearInst, 03, 31);
				String dateKey = dateRequired.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				System.out.println("Required Date: " + dateKey);
				Double op = sb.getEarningsData().get("Operating Profit").get(dateKey);
				Double sales = sb.getEarningsData().get("Sales").get(dateKey);
				Double assets = sb.getEarningsData().get("Total Assets").get(dateKey);
				Double dep = sb.getEarningsData().get("Depreciation").get(dateKey);
				if (op == null || sales == null || assets == null || dep == null ||assets==0.0 ||sales==0.0)
					continue;
				Double ebit = op + dep;
				company.add(sb.getShortName());
				op_asset_list.add(op / assets);
				sales_asset_List.add(sales / assets);
				ebit_asset_List.add(ebit / assets);
				ebit_sales_List.add(ebit / sales);
				Double pat = sb.getEarningsData().get("Net Profit").get(dateKey);
				Double sharecap = sb.getEarningsData().get("Share Capital").get(dateKey);
				Double reserves = sb.getEarningsData().get("Reserves").get(dateKey);
				Double equity = sharecap + reserves;
				Double roe = pat/equity;
				roe_List.add(roe);
			}

		}
		System.out.println(TestUtils.gson.toJson(earnDataMap));
		// Write the output to a file
		writeDataToExcel(earnDataMap);

	}

	private static void writeDataToExcel(Map<Integer, Map<String, List<? extends Object>>> earnDataMap) throws Exception
	{
		Workbook wb = new XSSFWorkbook();
		int coount = 0;
		for (Integer year : earnDataMap.keySet())
		{
			try
			{
				Map<String, List<? extends Object>> dataMap = earnDataMap.get(year);

				ExcelWriter.writeDataToExcel(wb, dataMap, year);
				coount++;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		System.out.println("Total data: " + coount);
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("earnings-data-cumulative.xlsx");
		wb.write(fileOut);
		fileOut.close();

		// Closing the workbook
		wb.close();

	}

	private static void writeDataToExcel(List<ScreenerBean> ls) throws Exception
	{
		Workbook wb = new XSSFWorkbook();
		int coount = 0;
		for (ScreenerBean sb : ls)
		{
			try
			{
				System.out.println("File: " + sb.getExcelBean().getCompany() + " - " + TestUtils.gson.toJson(sb));
				ExcelWriter.writeDataToExcel(wb, sb);
				coount++;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		System.out.println("Total data: " + coount);
		// Write the output to a file
		FileOutputStream fileOut = new FileOutputStream("companies-data-cumulative.xlsx");
		wb.write(fileOut);
		fileOut.close();

		// Closing the workbook
		wb.close();

	}

	private static StockPricesEventDays getStockPricesEventDays(ExcelBean eb)
	{
		String sc_id = getIDFromHyperLink(eb.getHyperlink());
		System.out.println(sc_id);
		StockPricesEventDays stockPricesEventDays = new StockPricesEventDays();
		Date annc = getDateFormat(eb.getAnnounced());
		Date exRights = getDateFormat(eb.getEx_Rights());
		Date rec = getDateFormat(eb.getRecord());
		stockPricesEventDays.setAnncStocks(getStockPricesforDate(sc_id, annc));
		stockPricesEventDays.setExRightsStocks(getStockPricesforDate(sc_id, exRights));
		stockPricesEventDays.setRecordStocks(getStockPricesforDate(sc_id, rec));
		return stockPricesEventDays;
	}

	static Map<String, Double> getStockPricesforDate(String sc_id, Date date)
	{
		Map<String, Double> stocks = new HashMap<>();
		LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		for (int i = 1; i < 250; i++)
		{
			LocalDate newdate = localDate.minusDays(i);
			String price = MoneyControlAPI.getStockPrices(sc_id, format(newdate)).get("Last Price");
			if (!"-".equals(price))
			{
				stocks.put(format(newdate), Double.parseDouble(price));
			}
		}
		for (int i = 0; i < 50; i++)
		{
			LocalDate plusDays = localDate.plusDays(i);
			String price = MoneyControlAPI.getStockPrices(sc_id, format(plusDays)).get("Last Price");
			if (!"-".equals(price))
			{
				stocks.put(format(plusDays), Double.parseDouble(price));
			}
		}
		return stocks;
	}

	static String getIDFromHyperLink(String hyperlink)
	{
		int idx = hyperlink.lastIndexOf('=');
		if (idx > 0)
		{
			return hyperlink.substring(idx + 1);
		}
		return null;
	}

	static List<String> formatStrings = Arrays.asList("d/M/y", "d-M-y", "M/y", "dd/MM/yyyy");

	static Date getDateFormat(String date)
	{
		for (String formatString : formatStrings)
		{
			try
			{
				return new SimpleDateFormat(formatString).parse(date);
			}
			catch (ParseException e)
			{
			}
		}
		return null;
	}

	final static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

	static String format(Date date)
	{
		return sf.format(date);
	}

	static String format(LocalDate localDate)
	{

		return sf.format(java.util.Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
	}

	static void cleanUp()
	{
		if (TaskExecutor.getServiceExecutor() != null)
			TaskExecutor.getServiceExecutor().shutdown();
		for (HttpClientApache http : HttpClientApache.httpClients)
		{
			http.getExecutorService().shutdown();
		}
	}

	static void writeDataToFile(final String fileName, final String data)
	{
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					FileUtils.writeStringToFile(new File("output/" + fileName + ".json"), data, "UTF-8");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};

		TaskExecutor.getInstance().submit(task);
	}
}
