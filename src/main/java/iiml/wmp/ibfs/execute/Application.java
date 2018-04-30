package iiml.wmp.ibfs.execute;

import iiml.wmp.ibfs.base.MoneyControlAPI;
import iiml.wmp.ibfs.beans.ExcelBean;
import iiml.wmp.ibfs.beans.ScreenerBean;
import iiml.wmp.ibfs.beans.StockPricesEventDays;
import iiml.wmp.ibfs.http.client.HttpClientApache;
import iiml.wmp.ibfs.utils.ExcelBeanUtils;
import iiml.wmp.ibfs.utils.TaskExecutor;
import iiml.wmp.ibfs.utils.TestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
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
			FileUtils.deleteQuietly(new File("output"));
			List<ExcelBean> list = ExcelBeanUtils.getSheetContents("data.xlsm", "analysis", ExcelBean.class);
			for (ExcelBean eb : list)
			{
				try{
					System.out.println(eb);
					ScreenerBean sb = getCompanyDetails(eb.getCompany());
					StockPricesEventDays stockPricesEventDays = getStockPricesEventDays(eb);
					sb.setStockPricesEventDays(stockPricesEventDays);
					writeDataToFile(eb.getCompany(), TestUtils.gson.toJson(sb));
				}catch (Exception e){
					System.out.println("Error for: "+eb);
					e.printStackTrace();
				}

			}
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
		for (int i = 1; i < 52; i++)
		{
			LocalDate newdate = localDate.minusDays(i);
			String price = MoneyControlAPI.getStockPrices(sc_id, format(newdate)).get("Last Price");
			if (!"-".equals(price))
			{
				stocks.put(format(newdate), Double.parseDouble(price));
			}
		}
		for (int i = 0; i < 20; i++)
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
