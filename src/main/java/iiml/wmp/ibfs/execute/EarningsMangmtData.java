package iiml.wmp.ibfs.execute;

import com.google.common.collect.Lists;
import iiml.wmp.ibfs.beans.ScreenerBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EarningsMangmtData
{

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


	public static void getEarningsData(ScreenerBean sb){
		Map<String, Map<String, Double>> finData = new HashMap<>();
		createRows(finData, sb.getNumberSet().getBalancesheet());
		createRows(finData, sb.getNumberSet().getAnnual());
		sb.setEarningsData(finData);
	}

	private static void createRows(Map<String, Map<String, Double>> finData, List<List<Object>> bs)
	{
		for (List<Object> ls : bs)
		{
			String key = (String) ls.get(0);
			Map vals = (Map) ls.get(1);
			finData.put(key, vals);
		}
	}
}
