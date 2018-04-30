package iiml.wmp.ibfs.utils;

import iiml.wmp.ibfs.http.client.WebHttpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils
{

	public static final Gson gson = new GsonBuilder().create();
	public static final Charset UTF_8 = StandardCharsets.UTF_8;
	static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
	private static final String LIST_SEPARATOR = ",";
	private static final String TAB_SEPARATOR = "\t";
	private static final String LINE_SEPARATOR = "\n";
	private final static Logger logger = LoggerFactory.getLogger(TestUtils.class);
	private static Map<String, String> MIME_TYPES;

	public static String getCurrentDate()
	{
		Instant instant = Instant.now();
		return instant.toString();
	}

	public static String encode(String text)
	{
		try
		{
			return URLEncoder.encode(text, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("Exception occured in encoding string, {}", text, e);
			return null;
		}
	}

	public static String toTitleCase(String input)
	{
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;

		for (char c : input.toCharArray())
		{
			if (Character.isSpaceChar(c))
			{
				nextTitleCase = true;
			}
			else if (nextTitleCase)
			{
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}

			titleCase.append(c);
		}

		return titleCase.toString();
	}

	public static boolean isAllNull(Iterable<?> list)
	{
		for (Object obj : list)
		{
			if (obj != null)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Parses an given string into a List using default List Separator as
	 * comma(,)
	 *
	 * @param dataField
	 * @return
	 */
	public static List<String> parseList(String dataField)
	{
		return parseList(dataField, LIST_SEPARATOR);
	}

	public static List<String> parseListwithLineSeparator(String dataField)
	{
		return parseList(dataField, "\\R");
	}

	public static List<String> parseList(String dataField, String separatorRegex)
	{
		// logger.debug("Parsing List {} with Separator {}", dataField, separatorRegex);
		List<String> list = new ArrayList<>();
		if (dataField == null || dataField.isEmpty())
		{
			return list;
		}
		String[] arr = dataField.split(separatorRegex);
		for (int i = 0; i < arr.length; i++)
		{
			list.add(arr[i].trim());
		}
		return list;

	}

	public static URL getUrl(String file)
	{
		return TestUtils.class.getClassLoader().getResource(file);
	}

	public static InputStream getStream(String file) throws FileNotFoundException
	{
		logger.debug("Loading Resource: {} as stream", file);
		InputStream is = TestUtils.class.getClassLoader().getResourceAsStream(file);
		if (is == null)
		{
			logger.error("File Not Found in Context: " + file);
			logger.warn("Not able to find the file {} in classpath. Trying to access the file as absolute path...", file);
			is = new FileInputStream(new File(file));
		}

		return is;
	}

	public static List<File> listFiles(String folder)
	{
		List<File> files = new ArrayList<>();
		try (Stream<Path> filePathStream = Files.walk(Paths.get(getUrl(folder).toURI())))
		{
			files = filePathStream.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
		}
		catch (Exception e)
		{
			logger.error("Error Occured in loading Files for folder {}", folder, e);
		}
		return files;
	}

	public static void writeResults(String request, final WebHttpResponse whr, String file)
	{
		File resFile = new File(file);
		writeResults( request, whr, resFile);
	}

	public static void writeResults(String request,final WebHttpResponse whr, final File file)
	{
		try
		{
			// logger.debug("writeResults {} ", file.getName());
			StringBuilder data = new StringBuilder();
			data.append(request).append(TAB_SEPARATOR);
			data.append(whr.getResponse()).append(TAB_SEPARATOR);
			data.append(whr.getStatusCode()).append(LINE_SEPARATOR);
			if (!file.exists())
			{
				// data.logger.debug("Creating file {} ", file.getName());
				FileUtils.write(file, getHeaderforResultFile(), "UTF-8", true);
			}
			logger.debug("writing content to file {} ", file.getName());
			FileUtils.write(file, data.toString(), "UTF-8", true);
		}
		catch (IOException e)
		{
			logger.error("Error Occured in writing Results File: {}", file, e);
		}
	}

	public static Instant parseDate(String dateString)
	{
		// Added until correct format is obtained for capture_date in case of LR

		if (dateString.length() <= 22 && !dateString.contains("Z"))
		{
			dateString += "Z";
		}
		TemporalAccessor accessor = DATE_TIME_FORMAT.parse(dateString);
		return Instant.from(accessor);
	}

	private static String getHeaderforResultFile()
	{
		StringBuilder data = new StringBuilder(1000);
		data.append("REQUEST").append(TAB_SEPARATOR);
		data.append("RESPONSE").append(TAB_SEPARATOR);
		data.append("HTTP STATUS").append(LINE_SEPARATOR);
		return data.toString();
	}

	public static Future<Boolean> writeResultsAsync(String req, WebHttpResponse whr, final String testClass)
	{
		Callable<Boolean> task = new Callable<Boolean>()
		{
			@Override
			public Boolean call()
			{
				try
				{

					Files.createDirectories(Paths.get("output/"));
					File file = new File("output/" + testClass + ".tsv");
					logger.debug("Logging file {}", file.getAbsolutePath());
					writeResults(req, whr, file);
					logger.debug("Logging file {} Successfull", file.getAbsolutePath());
				}
				catch (IOException e)
				{
					logger.error("Error Occured in directory creation...", e);
					return false;
				}
				return true;

			}

		};
		return TaskExecutor.getInstance().submit(task);
	}

	public static String getSysVal(String propertyName)
	{
		return getSysVal(propertyName, null);
	}

	public static String getSysVal(String propertyName, String configFolder)
	{
		String config = "config.properties";
		if (configFolder != null && !configFolder.isEmpty())
		{
			config = configFolder + "/" + config;
		}
		Properties testProperties = new Properties();
		try (InputStream testPropertyStream = getStream(config))
		{
			testProperties.load(testPropertyStream);
		}
		catch (Exception e)
		{
			logger.error("Error occured in getting property {}, from config {}", propertyName, configFolder, e);
			return null;
		}

		return testProperties.getProperty(propertyName);
	}

}
