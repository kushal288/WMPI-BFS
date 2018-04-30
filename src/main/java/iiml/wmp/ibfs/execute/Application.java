package iiml.wmp.ibfs.execute;

import iiml.wmp.ibfs.beans.ExcelBean;
import iiml.wmp.ibfs.http.client.HttpClientApache;
import iiml.wmp.ibfs.utils.ExcelBeanUtils;
import iiml.wmp.ibfs.utils.TaskExecutor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Application
{
	public static void main(String[] args)
	{
		try
		{
			FileUtils.deleteQuietly(new File("output"));
			List<ExcelBean> list = ExcelBeanUtils.getSheetContents("data.xlsm", "analysis", ExcelBean.class);
			for (ExcelBean eb : list){
				System.out.println(eb);
				String sc_id = getIDFromHyperLink(eb.getHyperlink());
				System.out.println(sc_id);
				Date annc = getDateFormat(eb.getAnnounced());
				Date exRights = getDateFormat(eb.getEx_Rights());
				Date rec = getDateFormat(eb.getRecord());
				System.out.println("Announce date "+ annc + " - "+format(annc));
				System.out.println("Ex Rights: "+exRights+ " - "+format(exRights));
				System.out.println("Record: "+rec+ " - "+ format(rec));
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

	static String getIDFromHyperLink(String hyperlink){
		int idx = hyperlink.lastIndexOf('=');
		if(idx>0){
			return hyperlink.substring(idx+1);
		}
		return null;
	}

	static List<String> formatStrings = Arrays.asList( "d/M/y", "d-M-y", "M/y", "dd/MM/yyyy");
	static Date getDateFormat(String date){
		for (String formatString : formatStrings)
		{
			try
			{
				return new SimpleDateFormat(formatString).parse(date);
			}
			catch (ParseException e) {}
		}
		return null;
	}

	final static SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
	static String format(Date date){
			return sf.format(date);
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
}
