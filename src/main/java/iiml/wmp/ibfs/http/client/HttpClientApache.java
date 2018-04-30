/**
 * ADOBE CONFIDENTIAL
 * __________________
 * Copyright 2016 Adobe Systems Incorporated
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains the property of
 * Adobe Systems Incorporated and its suppliers, if any. The intellectual
 * and technical concepts contained herein are proprietary to Adobe Systems
 * Incorporated and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or
 * copyright law. Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

package iiml.wmp.ibfs.http.client;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;

/**
 * @author kusagarw
 */
public class HttpClientApache
{

	public final static List<HttpClientApache> httpClients = new ArrayList<>();
	protected final static Logger logger = LoggerFactory.getLogger(HttpClientApache.class);
	private static String hostName = "10.0.0.1";

	private HttpClient client;
	private ExecutorService executorService = null;

	public HttpClientApache()
	{
		this(2);
	}

	/**
	 * @param threads = Number of threads that will be available for any http call using this instance.
	 *                For numbers less than 1, there would be no limit to the threads created for any number of async calls;
	 */
	public HttpClientApache(int threads)
	{
		if (threads < 1)
		{
			executorService = Executors.newCachedThreadPool();
		}
		else
		{
			executorService = Executors.newFixedThreadPool(threads);
		}
		createClient();
		httpClients.add(this);
	}

	private void createClient()
	{
		client = HttpClients.createDefault();

	}

	public WebHttpResponse get(String url, Map<String, String> headers) throws Exception
	{
		HttpGet req = new HttpGet(url);
		for (String str : headers.keySet())
		{
			req.addHeader(str, headers.get(str));
		}
		return sendRequest(req);
	}

	public WebHttpResponse post(String url, Map<String, String> headers, HttpEntity entity) throws Exception
	{
		HttpPost req = new HttpPost(url);

		if (headers != null)
		{
			for (String str : headers.keySet())
			{
				req.addHeader(str, headers.get(str));
			}
		}
		req.setEntity(entity);
		return sendRequest(req);
	}

	public WebHttpResponse delete(String url, HttpEntity entity, Map<String, String> headers) throws Exception
	{
		HttpDeleteRequest req = new HttpDeleteRequest(url);
		for (String str : headers.keySet())
		{
			req.addHeader(str, headers.get(str));
		}
		req.setEntity(entity);
		return sendRequest(req);
	}

	public WebHttpResponse delete(String url, Map<String, String> headers) throws Exception
	{
		HttpDelete req = new HttpDelete(url);
		for (String str : headers.keySet())
		{
			req.addHeader(str, headers.get(str));
		}

		return sendRequest(req);
	}

	public WebHttpResponse options(String url, Map<String, String> headers) throws Exception
	{
		HttpOptions req = new HttpOptions(url);
		for (String str : headers.keySet())
		{
			req.addHeader(str, headers.get(str));
		}
		return sendRequest(req);
	}

	public WebHttpResponse head(String url, Map<String, String> headers) throws Exception
	{
		HttpHead req = new HttpHead(url);
		for (String str : headers.keySet())
		{
			req.addHeader(str, headers.get(str));
		}
		return sendRequest(req);
	}

	public WebHttpResponse put(String url, Map<String, String> headers, HttpEntity entity) throws Exception
	{

		// logger.debug("Entity for PUT: {}", entity.toString());
		HttpPut req = new HttpPut(url);
		for (String str : headers.keySet())
		{
			req.addHeader(str, headers.get(str));
		}
		req.setEntity(entity);
		return sendRequest(req);
	}

	private WebHttpResponse sendRequest(final HttpRequestBase request) throws Exception
	{

		WebHttpResponse whr;
		try
		{
			whr = asyncSendRequest(request).get(250, TimeUnit.SECONDS);
			if (whr.getStatusCode() > 501)
			{
				Thread.sleep(5000);
				whr = asyncSendRequest(request).get(250, TimeUnit.SECONDS);
			}

			return whr;
		}
		catch (Exception e)
		{
			request.abort();
			request.reset();
			throw e;
		}

	}

	private Future<WebHttpResponse> asyncSendRequest(final HttpRequestBase request)
	{
		Callable<WebHttpResponse> callable = new Callable<WebHttpResponse>()
		{

			@Override
			public WebHttpResponse call() throws Exception
			{

				return executeHTTPRequest(request);
			}
		};
		return executorService.submit(callable);
	}

	private WebHttpResponse executeHTTPRequest(HttpRequestBase request) throws Exception
	{
		Header[] headers = request.getAllHeaders();

		logger.info("HTTP Request: " + request.toString());

		HttpResponse response = null;
		final long startTime = System.nanoTime();
		response = client.execute(request);
		final long endTime = System.nanoTime();
		Instant inst = Instant.ofEpochMilli(System.currentTimeMillis() - ((endTime - startTime) / 2000000));
		// logger.debug("Response from HTTP Request: " + response);
		WebHttpResponse wr = converHttpResptoWebHttpResp(response, (endTime - startTime) / 1000000);
		wr.setTimestamp(inst);
		if (wr.getStatusCode() > 499)
		{
			logger.error("Error Occured for Request: {}", request);
			logger.error("WebResponse: " + wr);
		}

		return wr;
	}

	private WebHttpResponse converHttpResptoWebHttpResp(HttpResponse response, long time) throws Exception
	{
		if (response == null)
		{

			throw new Exception("Null Response obtained for http request!");
		}
		int responseCode = response.getStatusLine().getStatusCode();
		// logger.info("Response code obtained: {}", responseCode);
		HttpEntity entity = response.getEntity();

		WebHttpResponse wr = new WebHttpResponse(responseCode, null, time);
		wr.setEntity(response.getEntity());
		Header[] headersArray = response.getAllHeaders();
		Map<String, String> headersMap = new HashMap<>();
		for (Header header : headersArray)
		{
			headersMap.put(header.getName().toLowerCase(), header.getValue());
		}
		wr.setHeaders(headersMap);
		Header respHeader = response.getFirstHeader("Content-Type");
		String encoding = headersMap.get("Content-Encoding");
		if (respHeader != null)
		{
			String respValue = respHeader.getValue();
			if (entity != null)
			{
				if (respValue != null && (respValue.contains("json") || respValue.contains("text") || respValue.contains("html")))
				{
					String result = inputStreamToString(entity.getContent() , encoding );
					// logger.debug("Response Message: " + result);
					wr.setResponse(result);
				}
				else
				{
					EntityUtils.consumeQuietly(entity);
				}

			}
		}
		EntityUtils.consumeQuietly(entity);
		logger.info("Request Completed, status={}, in time {} ms, Response Headers: {}", wr.getStatusCode(), wr.getTimeTaken(), response.getAllHeaders());
		return wr;
	}

	private String inputStreamToString(InputStream is, String encoding) throws IOException
	{

		if (is == null)
		{
			logger.warn("Cannot convert null InputStream  to String: {}");
			return null;
		}

		StringBuilder result = new StringBuilder();
		if ("gzip".equals(encoding))
			is = new GZIPInputStream(is);
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8192))
		{
			String line = "";
			while ((line = rd.readLine()) != null)
			{
				result.append(line);
			}
			rd.close();
		}
		catch (Exception e)
		{
			logger.warn("Cannot convert InputStream to String: {}", e.getMessage());
		}
		return result.toString();
	}

	public ExecutorService getExecutorService()
	{
		return executorService;
	}

}
