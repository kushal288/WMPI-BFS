package iiml.wmp.ibfs.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils
{

	public static String decode(String encodedData)
	{
		return new String(Base64.getDecoder().decode(encodedData), StandardCharsets.UTF_8);
	}
}
