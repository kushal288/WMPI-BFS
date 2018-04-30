package iiml.wmp.ibfs.base;

import iiml.wmp.ibfs.beans.ScreenerBean;
import iiml.wmp.ibfs.beans.ScreenerStockPrice;
import iiml.wmp.ibfs.beans.ScreenerseachObj;
import iiml.wmp.ibfs.http.client.HttpClientApache;
import iiml.wmp.ibfs.http.client.WebHttpResponse;
import iiml.wmp.ibfs.utils.TestUtils;
import com.google.gson.JsonArray;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ScreenerAPI

{

	private final static Logger logger = LoggerFactory.getLogger(ScreenerAPI.class);


	private static final String screenerURL = "https://www.screener.in";

	private static HttpClientApache http = new HttpClientApache();
	static Map<String, String> headers = new HashMap<>();

	/*public static void main(String[] args)
	{
		headers.put("Accept","application/json");
		System.out.println(TestUtils.gson.toJson(getCompanyDetails("orient paper")));
	}*/


	public static ScreenerBean getCompanyDetails(String q){
		ScreenerBean sb = null;
		ScreenerseachObj obj = getCompany(q);
		if(obj!=null){
			sb= getCompanyNumbers(obj);
			ScreenerStockPrice ssp = getStockPrices(obj.getId());
			sb.setScreenerStockPrice(ssp);
		}
		return sb;
	}

	public static ScreenerBean getCompanyNumbers(ScreenerseachObj comp){
		WebHttpResponse whr = getCompDetails(comp);
		logger.info("Details Response: {}",whr.getResponse());
		//TestUtils.writeResultsAsync(comp.getName(), whr, comp.getName()+"_numbers_"+ScreenerAPI.class.getSimpleName());
		if(whr.getStatusCode()!=200)
			return null;
		ScreenerBean  arr = TestUtils.gson.fromJson(whr.getResponse(), ScreenerBean.class);
		return arr;
	}


	public static WebHttpResponse getCompDetails(ScreenerseachObj comp){
		WebHttpResponse whr = null;
		try
		{
			URIBuilder uriBuilder = new URIBuilder(screenerURL);
			uriBuilder.setCharset(StandardCharsets.UTF_8);
			uriBuilder.setPath("/api"+comp.getUrl());
			String ccurl = uriBuilder.build().toString();
			whr = http.get(ccurl, headers);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return whr;
	}


	public static ScreenerseachObj getCompany(String q){
		WebHttpResponse whr = getSearchResult(q);
		logger.info("Req: {}, Response: {}",q,whr.getResponse());
		TestUtils.writeResultsAsync(q, whr, "search_"+ScreenerAPI.class.getSimpleName());
		if(whr.getStatusCode()!=200)
			return null;
		JsonArray  arr = TestUtils.gson.fromJson(whr.getResponse(), JsonArray.class);
		if(arr!=null && arr.size()==1){
			return TestUtils.gson.fromJson(arr.get(0), ScreenerseachObj.class);
		}
		return null;
	}

	public static WebHttpResponse getSearchResult(String q)
	{
		WebHttpResponse whr = null;
		try
		{
			URIBuilder uriBuilder = new URIBuilder(screenerURL);
			uriBuilder.setPath("/api/company/search/");
			uriBuilder.addParameter("q", q);
			String ccurl = uriBuilder.build().toString();
			whr = http.get(ccurl, headers);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return whr;
	}

	public static ScreenerStockPrice getStockPrices(Integer id)
	{
		ScreenerStockPrice ssp = null;
		try
		{
			URIBuilder uriBuilder = new URIBuilder(screenerURL);
			uriBuilder.setPath("/api/company/"+id+"/prices/");
			uriBuilder.addParameter("what", "months");
			uriBuilder.addParameter("period", "175");
			String ccurl = uriBuilder.build().toString();
			 WebHttpResponse whr = http.get(ccurl, headers);
			 if(whr.getStatusCode()!=200)
			 	return null;
			 ssp = TestUtils.gson.fromJson(whr.getResponse(), ScreenerStockPrice.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ssp;
	}
}
