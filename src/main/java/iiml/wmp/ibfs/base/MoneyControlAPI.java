package iiml.wmp.ibfs.base;

import iiml.wmp.ibfs.beans.ScreenerseachObj;
import iiml.wmp.ibfs.http.client.HttpClientApache;
import iiml.wmp.ibfs.http.client.WebHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoneyControlAPI
{
	private final static Logger logger = LoggerFactory.getLogger(MoneyControlAPI.class);

	private static final String monerctrlURL = "http://www.moneycontrol.com";

	private static HttpClientApache http = new HttpClientApache();
	static Map<String, String> headers = new HashMap<>();

	public static void main(String[] args)
	{
		System.out.println(getStockPrices("JEC", "20180429"));
	}

	public static Map<String, String> getStockPrices(String sc_id, String date)
	{
		Map<String, String> priceMap = new HashMap<>();
		try
		{
			WebHttpResponse whr = getStockPrice(sc_id, date);
			logger.info("Stock price table: {}", whr.getResponse());
			if (whr.getStatusCode() != 200)
			{
				return priceMap;
			}
			Document doc = Jsoup.parse(whr.getResponse());
			Element table = doc.body().tagName("table").child(0);
			Elements elems = table.children();
			elems = elems.get(0).children();
			for (int i = 1; i < 6; i++)
			{
				Element ind = elems.get(i);
				Elements units = ind.children();
				priceMap.put(units.get(0).ownText(), units.get(1).ownText());
			}
			return priceMap;
		}
		catch (Exception e)
		{
			logger.error("Error in retrieving stock price for: {}, date {} ", sc_id, date,e);
		}
		return priceMap;
	}

	public static WebHttpResponse getStockPrice(String sc_id, String date)
	{
		WebHttpResponse whr = null;
		try
		{
			URIBuilder uriBuilder = new URIBuilder(monerctrlURL);
			uriBuilder.setCharset(StandardCharsets.UTF_8);
			uriBuilder.setPath("/stocks/company_info/get_histprices.php");
			uriBuilder.addParameter("ex", "B");
			uriBuilder.addParameter("sc_id", sc_id);
			uriBuilder.addParameter("range", "0");
			uriBuilder.addParameter("sel_date", date);
			String ccurl = uriBuilder.build().toString();
			whr = http.get(ccurl, headers);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return whr;
	}
}
