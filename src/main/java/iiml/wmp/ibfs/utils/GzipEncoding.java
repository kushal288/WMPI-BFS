package iiml.wmp.ibfs.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipEncoding
{

	public static byte[] compress(String str) throws Exception
	{

		System.out.println("String length : " + str.length());
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		// System.out.println("Output Compressed String : "+ obj.toString("UTF-8"));
		return obj.toByteArray();
	}

	public byte[] gzipFile(String data)
	{

		try
		{

			ByteArrayOutputStream out = new ByteArrayOutputStream(data.length() / 2);
			GZIPOutputStream gzipOuputStream = new GZIPOutputStream(out);

			gzipOuputStream.write(data.getBytes());
			gzipOuputStream.close();

			return out.toByteArray();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
