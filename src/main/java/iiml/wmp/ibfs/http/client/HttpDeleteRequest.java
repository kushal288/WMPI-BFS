package iiml.wmp.ibfs.http.client;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpDeleteRequest extends HttpEntityEnclosingRequestBase
{
	public static final String METHOD_NAME = "DELETE";

	public HttpDeleteRequest(final String uri)
	{
		super();
		setURI(URI.create(uri));
	}

	public HttpDeleteRequest(final URI uri)
	{
		super();
		setURI(uri);
	}

	public HttpDeleteRequest()
	{
		super();
	}

	public String getMethod()
	{
		return METHOD_NAME;
	}
}
