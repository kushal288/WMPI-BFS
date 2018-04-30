package iiml.wmp.ibfs.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelBeanUtils
{
	private static final String REQUEST_TYPE_STRING = "string";
	private static final String REQUEST_TYPE_INTEGER = "integer";
	private static final String REQUEST_TYPE_DOUBLE = "double";
	private static final String REQUEST_TYPE_BOOLEAN = "boolean";
	private static final String REQUEST_TYPE_JSONOBJECT = "jsonobject";
	private static final String REQUEST_TYPE_JSONARRAY = "jsonarray";
	private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[(.*?)\\]");
	//private static final Pattern JSON_PATTERN = Pattern.compile("\\{(.*)\\}");
	private static final Pattern LIST_HEAD_PATTERN = Pattern.compile("(.*)\\[(\\d)\\]");
	private static final String SQUARE_PARENTHESIS = "[\\[\\]]";
	private static Gson gson = new GsonBuilder().setLenient().excludeFieldsWithModifiers(Modifier.ABSTRACT).create();
	private static Logger logger = LoggerFactory.getLogger(ExcelBeanUtils.class);

	public static void main(String[] args)
	{
		try
		{

		}
		catch (Exception e)
		{
			logger.error(
					"Error Occured in \n Class: {}, \n Method: {},\n Error: {}\n",
					ExcelBeanUtils.class.getClass().getName(),
					e.getStackTrace()[0].getMethodName(),
					e.getMessage(),
					e);
		}
	}

	private static <T> List<T> getSheetContents(String excelFile, String sheetName, Class<T> classOfT) throws Exception
	{
		if (classOfT == null)
		{
			classOfT = (Class<T>) Map.class;
		}
		try (InputStream excelis = TestUtils.getStream(excelFile); XSSFWorkbook workbook = new XSSFWorkbook(excelis))
		{
			List<T> ls = new ArrayList<>();
			XSSFSheet sheet = workbook.getSheet(sheetName);
			int idx = sheet.getFirstRowNum();
			Map<Integer, ExcelHead> headerMap = getHeader(sheet.getRow(idx), sheet.getRow(idx + 1));
			int rownums = sheet.getPhysicalNumberOfRows();
			for (int i = idx + 2; i < rownums; i++)
			{
				ls.add(getRowObj(headerMap, sheet.getRow(i), classOfT));
			}
			//logger.debug("Result: {}", ls);
			return ls;
		}
		catch (Exception e)
		{
			logger.error("Error occured in reading sheet: {}, for excel file: {}", sheetName, excelFile, e);
			throw e;
		}
	}

	private static <T> T getRowObj(Map<Integer, ExcelHead> headerMap, Row row, Class<T> classOfT) throws Exception
	{
		//Map<String, Object> rowValues = getRowObj(headerMap, row);
		Map<String, Object> rowValues = getRowObjList(headerMap, row);
		return convertObjToClass(rowValues, classOfT);
	}

	private static <T> T convertObjToClass(Object obj, Class<T> classOfT)
	{
		return gson.fromJson(gson.toJson(obj), classOfT);
	}

	private static Map<String, Object> getRowObjList(Map<Integer, ExcelHead> headerMap, Row row) throws Exception
	{
		Map<String, Object> rowValues = new HashMap<>();
		for (int i = 0; i < headerMap.size(); i++)
		{
			ExcelHead head = headerMap.get(i);
			String cellVal = cellToString(row.getCell(i));
			rowValues = updateObject(head.name, head.type, cellVal, rowValues);
		}
		return rowValues;
	}

	private static Map<String, Object> getRowObj(Map<Integer, ExcelHead> headerMap, Row row)
	{
		Map<String, Object> rowValues = new HashMap<>();
		for (int i = 0; i < headerMap.size(); i++)
		{
			ExcelHead head = headerMap.get(i);
			String cellVal = cellToString(row.getCell(i));
			String[] arr = head.name.split("\\.");
			Deque<Map<String, Object>> stack = new ArrayDeque<>();
			Map<String, Object> tempmap = rowValues;
			for (int j = 0; j < arr.length - 1; j++)
			{
				String string = arr[j];
				Matcher m = LIST_HEAD_PATTERN.matcher(string);
				int idx = Integer.MIN_VALUE;
				if (m.find())
				{
					string = m.group(1);
					idx = Integer.parseInt(m.group(2));
				}
				Map<String, Object> obj = null;
				if (idx > 0)
				{
					obj = (Map<String, Object>) ((List<Object>) tempmap.get(string)).get(idx);
				}
				else
				{
					obj = (Map<String, Object>) tempmap.get(string);
				}
				if (obj != null)
				{
					stack.push(obj);
				}
				else
				{
					Map<String, Object> map = new HashMap<>();
					if (idx > -1)
					{
						List<Object> ls = new ArrayList<>();
						tempmap.put(string, ls);
					}
					else
					{

					}

				}
				tempmap = stack.peek();
			}
			logger.debug("Interim row map: {}", TestUtils.gson.toJson(rowValues));
			tempmap = rowValues;
			for (int j = 0; j < arr.length; j++)
			{
				String string = arr[j];
				Matcher m = LIST_HEAD_PATTERN.matcher(string);
				int idx = Integer.MIN_VALUE;
				if (m.find())
				{
					string = m.group(1);
					idx = Integer.parseInt(m.group(2));
				}
				Map<String, Object> tmp = stack.peekLast();
				if (tmp == null)
				{
					tempmap.put(string, getActualElement(cellVal, head.type));
				}
				else
				{
					tempmap.put(string, stack.pollLast());
				}
				tempmap = tmp;
			}

		}
		return rowValues;
	}

	private static Map<Integer, ExcelHead> getHeader(XSSFRow dataTypeRow, XSSFRow headerRow)
	{
		Map<Integer, ExcelHead> headerMap = new HashMap<>();
		for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++)
		{
			headerMap.put(i, new ExcelHead(cellToString(dataTypeRow.getCell(i)), i, cellToString(headerRow.getCell(i))));

		}
		//logger.debug("Header Map: {}", headerMap.toString());
		return headerMap;
	}

	private static String cellToString(Cell cell)
	{
		if (cell == null)
		{
			return null;
		}
		switch (cell.getCellTypeEnum())
		{

		case BLANK:
			return null;
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case ERROR:
			return String.valueOf(cell.getErrorCellValue()).trim();
		case FORMULA:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case STRING:
			return cell.getStringCellValue().trim();
		default:
			logger.error("Unknown type cell data: {}", cell.getCellTypeEnum());
			return cell.getStringCellValue().trim();
		}
	}

	private static <T> T getActualElement(String item, String actualType)
	{

		if (item == null)
		{
			return null;
		}
		item = item.trim();
		if (item.isEmpty())
		{
			return null;
		}
		// logger.debug("to parse elem: {}, and type: {}", item, actualType);
		if (REQUEST_TYPE_JSONOBJECT.equalsIgnoreCase(actualType))
		{
			//   logger.debug("In Json Object");
			return getActualElementItem(item, REQUEST_TYPE_JSONOBJECT);
		}
		else if (REQUEST_TYPE_JSONARRAY.equalsIgnoreCase(actualType))
		{
			return getActualElementItem(item, REQUEST_TYPE_JSONARRAY);
		}
		else if (ARRAY_PATTERN.matcher(item).find())
		{
			item = item.replaceAll(SQUARE_PARENTHESIS, "");
			String array[] = item.split(",");
			List<Object> list = new ArrayList<>();
			for (int i = 0; i < array.length; i++)
			{
				String string = array[i];
				list.add(getActualElementItem(string, actualType));
			}
			return (T) list;
		}
		else
		{
			return getActualElementItem(item, actualType);
		}

	}

	private static <T> T getActualElementItem(String item, String actualType)
	{
		if (item == null)
		{
			return null;
		}
		item = item.trim();
		if (item.isEmpty())
		{
			return null;
		}
		//logger.debug("parsing elem: {}, and type: {}", item, actualType);
		switch (actualType.toLowerCase())
		{
		case REQUEST_TYPE_STRING:
			return (T) item;
		case REQUEST_TYPE_INTEGER:
			return (T) StringToInteger(item);
		case REQUEST_TYPE_DOUBLE:
			return (T) Double.valueOf(item);
		case REQUEST_TYPE_BOOLEAN:
			return (T) Boolean.valueOf(item);
		case REQUEST_TYPE_JSONOBJECT:
			JsonParser parser = new JsonParser();
			return (T) parser.parse(item).getAsJsonObject();
		case REQUEST_TYPE_JSONARRAY:
			parser = new JsonParser();
			return (T) parser.parse(item).getAsJsonArray();
		default:
			try
			{
				parser = new JsonParser();
				return (T) parser.parse(item);
			}
			catch (JsonParseException j)
			{
				logger.warn("Error on json parsing of elem: {}, and type: {}", item, actualType);
				return (T) item;
			}
		}

	}

	private static Integer StringToInteger(String value)
	{
		if (value == null || value.isEmpty())
		{
			return null;
		}
		Integer result = (int) (Double.parseDouble(value));
		return result;
	}

	private static Map<String, Object> updateObject(String headName, String headType, String cellVal, Map<String, Object> data) throws Exception
	{
		//logger.debug("Initial data: {}", TestUtils.gson.toJson(data));
		//logger.debug("Initial headName: {}", headName);
		//logger.debug("Initial headType: {}", headType);
		//logger.debug("Initial cellVal: {}", cellVal);

		int dotIdx = headName.indexOf('.');
		String key = headName;
		if (dotIdx > 0)
			key = headName.substring(0, dotIdx);
		Matcher m = LIST_HEAD_PATTERN.matcher(key);
		int lsIdx = Integer.MIN_VALUE;
		if (m.find())
		{
			key = m.group(1);
			lsIdx = Integer.parseInt(m.group(2));
		}
		if (key == null)
		{
			throw new Exception("Invalid Test excel format exception!!!");
		}
		else
		{
			if (data == null)
				throw new Exception("Invalid Test Data format exception!!! Map data should not be null!");
			Object obj = data.get(key);
			//logger.debug("updateObject obj: {}", obj);
			//logger.debug("updateObject key: {}", key);
			//logger.debug("updateObject lsIdx: {}", lsIdx);
			if (dotIdx > 0)
			{
				if (obj == null)
				{
					if (lsIdx > -1)
					{
						obj = new ArrayList<>();
					}
					else
					{
						obj = new HashMap<>();
					}
				}
				if (obj instanceof List)
				{
					List tempLsData = ((List<Object>) obj);
					tempLsData = add(tempLsData, lsIdx, get(tempLsData, lsIdx));
					updateObject(headName.substring(1 + dotIdx), headType, cellVal, (Map<String, Object>) get(tempLsData, lsIdx));
					//logger.debug("updateObject tempLsData data: {}", tempLsData);
					data.put(key, tempLsData);
				}
				else if (obj instanceof Map)
				{
					data.put(key, updateObject(headName.substring(1 + dotIdx), headType, cellVal, (Map<String, Object>) obj));
				}

			}
			else
			{
				data.put(key, updateActualValue(headName, headType, cellVal, data));
			}

		}
		return data;
	}

	private static List add(List ls, int i, Object obj)
	{
		if (obj == null)
			return ls;
		Map<Integer, Object> map = getMapFromList(ls);
		map.put(i, obj);
		int maxnum = -1;
		for (Integer intkey : map.keySet())
		{
			if (intkey > maxnum)
				maxnum = intkey;
		}
		List<Object> newList = new ArrayList<>(maxnum);
		for (int j = 0; j <= maxnum; j++)
		{
			Object o = map.get(j);
			if (o == null)
				o = new HashMap<>();
			newList.add(j, o);
		}
		return newList;
	}

	private static Object get(List ls, int i)
	{
		Map<Integer, Object> map = getMapFromList(ls);
		return map.get(i);
	}

	private static Map<Integer, Object> getMapFromList(List ls)
	{
		Map<Integer, Object> map = new HashMap();
		for (int i = 0; i < ls.size(); i++)
		{
			map.put(i, ls.get(i));
		}
		return map;
	}

	private static Object updateActualValue(String key, String headType, String cellVal, Map<String, Object> data)
	{
		//logger.debug("updateActualValue key: {}",key);
		//logger.debug("updateActualValue headType: {}",headType);
		//logger.debug("updateActualValue cellVal: {}",cellVal);
		//logger.debug("updateActualValue data: {}",data);

		Matcher m = LIST_HEAD_PATTERN.matcher(key);
		int idx = Integer.MIN_VALUE;
		if (m.find())
		{
			key = m.group(1);
			idx = Integer.parseInt(m.group(2));
		}
		if (idx > -1)
		{
			List<Object> objectList = (List<Object>) data.get(key);
			if (objectList == null)
			{
				objectList = new ArrayList<>();
			}
			objectList = add(objectList, idx, getActualElement(cellVal, headType));
			return objectList;
		}
		else
		{
			return getActualElement(cellVal, headType);
		}
	}

	private static class ExcelHead
	{
		String type;
		Integer idx;
		String name;

		public ExcelHead(String type, Integer idx, String name)
		{
			super();
			this.type = type;
			this.idx = idx;
			this.name = name;
		}

		@Override
		public int hashCode()
		{
			return ((name == null) ? 0 : name.hashCode());
		}

		@Override
		public String toString()
		{
			return "[type=" + type + ", idx=" + idx + ", name=" + name + "]";
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (!(obj instanceof ExcelHead))
			{
				return false;
			}
			ExcelHead other = (ExcelHead) obj;
			if (name == null)
			{
				return other.name == null;
			}
			else
			{
				return name.equals(other.name);
			}
		}

	}

}
