package iiml.wmp.ibfs.http.client;

import org.apache.http.HttpEntity;

import java.time.Instant;
import java.util.Map;

public class WebHttpResponse
{
	private int statusCode;
	private String response;
	private long timeTaken;
	private Map<String, String> headers;
	private HttpEntity entity;
	private Object body;
	private Instant timestamp;


	public WebHttpResponse(int statusCode, String response, long time)
	{
		this.statusCode = statusCode;
		this.response = response;
		timeTaken = time;
		timestamp = Instant.now();
	}

	public Object getBody()
	{
		return body;
	}

	public void setBody(Object body)
	{
		this.body = body;
	}

	public Instant getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(Instant timestamp)
	{
		this.timestamp = timestamp;
	}

	public Map<String, String> getHeaders()
	{
		return headers;
	}

	public void setHeaders(Map<String, String> headers)
	{
		this.headers = headers;
	}

	public String getHeader(String key)
	{
		if (key == null)
		{
			return null;
		}
		return headers.get(key.toLowerCase());
	}

	public HttpEntity getEntity()
	{
		return entity;
	}

	public void setEntity(HttpEntity entity)
	{
		this.entity = entity;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}

	public String getResponse()
	{
		return response;
	}

	public void setResponse(String response)
	{
		this.response = response;
	}

	public long getTimeTaken()
	{
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken)
	{
		this.timeTaken = timeTaken;
	}

	@Override
	public String toString()
	{
		return "WebHttpResponse [statusCode=" + statusCode + ", response=" + response + ", timeTaken=" + timeTaken + "]";
	}

}
