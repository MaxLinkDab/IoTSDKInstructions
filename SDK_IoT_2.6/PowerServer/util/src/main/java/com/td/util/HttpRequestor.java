package com.td.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP请求工具，HttpRequestor.newPost(...)是基于Apache HttpClient，其他都是基于{@link HttpURLConnection}
 */
public class HttpRequestor
{
	private final static Logger log = LoggerFactory.getLogger(HttpRequestor.class);

	public static final String http_content_type = "Content-Type";

	public static final String http_content_type_gzip = "application/x-gzip";

	public static final String http_content_type_encode = "text/html; charset=";

	public static final String UTF_8 = "UTF-8";

	/**
	 * 连接超时
	 */
	private static int connectTimeout = 30000;

	/**
	 * 读取数据超时
	 */
	private static int readTimeout = 60000;

	/**
	 * 发送HTTP请求
	 * @param reqUrl 请求地址
	 * @param content 请求内容
	 * @param reqMethod 请求方式:POST或者GET
	 * @param connectTimeout 连接超时时间限制
	 * @param readTimeout 读取数据超时时间限制
	 * @param encode 编码
	 * @param useGzip 是否使用Gzip进行压缩
	 * @return 响应内容
	 * @throws IOException
	 */
	private static String connect(String reqUrl, String content, String reqMethod, int connectTimeout, int readTimeout,
			String encode, boolean useGzip) throws IOException
	{
		HttpURLConnection connection = null;
		String resp = null;
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try
		{
			connection = (HttpURLConnection) new URL(reqUrl).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/4.0");
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod(reqMethod);
			connection.setDoOutput(true);
			if ((content != null) && !content.equalsIgnoreCase(""))
			{
				OutputStream out = connection.getOutputStream();
				if (useGzip)
				{
					connection.setRequestProperty(http_content_type, http_content_type_gzip);
					GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
					writer = new BufferedWriter(new OutputStreamWriter(gzipOutputStream));
				}
				else
				{
					writer = new BufferedWriter(new OutputStreamWriter(out));
				}
				writer.write(content);
				writer.flush();
			}

			connection.connect();
			InputStream in = connection.getInputStream();
			if (http_content_type_gzip.equalsIgnoreCase(connection.getRequestProperty(http_content_type)))
			{
				GZIPInputStream gzipInputStream = new GZIPInputStream(in);
				reader = new BufferedReader(new InputStreamReader(gzipInputStream, encode));
			}
			else
			{
				reader = new BufferedReader(new InputStreamReader(in, encode));
			}
			StringBuilder builder = new StringBuilder();
			char[] re = new char[1024];
			int len = 0;
			while ((len = reader.read(re)) != -1)
			{
				builder.append(re, 0, len);
			}
			resp = builder.toString();
		}
		finally
		{
			if (writer != null)
				writer.close();
			if (reader != null)
				reader.close();
			if (connection != null)
				connection.disconnect();
		}
		return resp;
	}

	/**
	 * 基于Apache HttpClient
	 */
	public static String newPost(String reqUrl, List<? extends NameValuePair> params, final String encode, boolean gzip)
			throws ClientProtocolException, IOException
	{
		HttpPost post = new HttpPost(reqUrl);
		post.setEntity(new UrlEncodedFormEntity(params, encode));
		return doRequest(post, encode, gzip);
	}

	public static String newPost(String reqUrl, List<? extends NameValuePair> params)
			throws ClientProtocolException, IOException
	{
		return newPost(reqUrl, params, UTF_8, false);
	}

	/**
	 * 基于Apache HttpClient
	 */
	public static String newGet(String reqUrl, List<? extends NameValuePair> params, final String encode, boolean gzip)
			throws ClientProtocolException, IOException
	{
		HttpGet get = new HttpGet(reqUrl + "?" + URLEncodedUtils.format(params, encode));
		return doRequest(get, encode, gzip);
	}

	public static String newGet(String reqUrl, List<? extends NameValuePair> params)
			throws ClientProtocolException, IOException
	{
		return newGet(reqUrl, params, UTF_8, false);
	}

	private static String doRequest(HttpUriRequest request, final String encode, boolean gzip)
			throws ClientProtocolException, IOException
	{
		if (gzip)
		{
			request.setHeader(http_content_type, http_content_type_gzip);
		}
		request.setHeader("User-Agent", "Mozilla/4.0");

		int timeout = HttpRequestor.connectTimeout;
		RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout)
				.setConnectionRequestTimeout(timeout).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return httpClient.execute(request, new ResponseHandler<String>()
		{

			@Override
			public String handleResponse(HttpResponse resp) throws ClientProtocolException, IOException
			{
				StatusLine status = resp.getStatusLine();
				log.info("响应{{} : {}}", status.getStatusCode(), status.getReasonPhrase());
				HttpEntity entity = resp.getEntity();
				BufferedReader reader = null;
				try
				{
					InputStreamReader in = null;
					Header contentType = entity.getContentType();
					if (contentType != null && contentType.getValue().startsWith(http_content_type_gzip))
					{
						in = new InputStreamReader(new GZIPInputStream(entity.getContent()), encode);
					}
					else
					{
						in = new InputStreamReader(entity.getContent(), encode);
					}
					reader = new BufferedReader(in);
					StringBuilder sb = new StringBuilder();
					char[] re = new char[1024];
					int size = 0;
					while ((size = reader.read(re)) != -1)
					{
						sb.append(re, 0, size);
					}
					return sb.toString();
				}
				finally
				{
					if (reader != null)
						reader.close();
				}
			}
		});
	}

	public static String doGet(String reqUrl, String ecode) throws IOException
	{
		int paramIndex = reqUrl.indexOf("?");
		if (paramIndex != -1)
		{
			return HttpRequestor.doGet(reqUrl.substring(0, paramIndex),
					reqUrl.substring(paramIndex + 1, reqUrl.length()), ecode);
		}
		else
		{
			String requestContent = null;
			return HttpRequestor.doGet(reqUrl, requestContent, ecode);
		}
	}

	public static String doGet(String reqUrl, Map<String, String> parameters) throws Exception
	{
		return doGet(reqUrl, parameters, UTF_8);
	}

	public static String doGet(String reqUrl, Map<String, String> params, String encode) throws Exception
	{
		return doGet(reqUrl, toUrlEncodeString(params, encode), encode);

	}

	public static String doPost(String reqUrl, String params) throws Exception
	{
		return HttpRequestor.connect(reqUrl, params, "POST", HttpRequestor.connectTimeout, HttpRequestor.readTimeout,
				UTF_8, false);
	}

	public static String doGet(String reqUrl, String params, String encode) throws IOException
	{
		return HttpRequestor.connect(reqUrl, params, "GET", HttpRequestor.connectTimeout, HttpRequestor.readTimeout,
				encode, false);
	}

	public static String doPost(String reqUrl, Map<String, String> params, String reqEncoding) throws Exception
	{
		return HttpRequestor.doPost(reqUrl, params, reqEncoding, false);
	}

	public static String doPost(String reqUrl, Map<String, String> parameters) throws Exception
	{
		return HttpRequestor.doPost(reqUrl, parameters, UTF_8, false);
	}

	public static String doPost(String reqUrl, Map<String, String> params, String encode, boolean gzip) throws Exception
	{
		String paramsStr = gzip ? toUrlString(params) : toUrlEncodeString(params, encode);
		return connect(reqUrl, paramsStr, "POST", HttpRequestor.connectTimeout, HttpRequestor.readTimeout, encode,
				gzip);
	}

	public static String doPost(String reqUrl, String param, String encode) throws IOException
	{
		return connect(reqUrl, param, "POST", HttpRequestor.connectTimeout, HttpRequestor.readTimeout, encode, false);
	}

	public static String getParamValue(String url, String paramName)
	{
		String paramValue = null;
		String[] keyvalus = url.split("&");
		for (int i = 0; i < keyvalus.length; i++)
		{
			String string = keyvalus[i];
			int index = string.indexOf("=");
			if (index > 0)
			{
				String key = string.substring(0, index);
				if (key.equalsIgnoreCase(paramName))
				{
					paramValue = string.substring(index + 1, string.length());
					break;
				}
			}
		}
		return paramValue;
	}

	public static String toUrlEncodeString(Map<String, String> params, String encode)
			throws UnsupportedEncodingException
	{
		StringBuilder builder = new StringBuilder();
		for (Iterator<Entry<String, String>> iter = params.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, String> element = iter.next();
			builder.append(element.getKey());
			builder.append("=");
			builder.append(URLEncoder.encode(element.getValue(), encode));
			builder.append("&");
		}

		if (builder.length() > 0)
		{
			builder = builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public static String toUrlString(Map<String, String> params)
	{
		StringBuilder builder = new StringBuilder();
		for (Iterator<Entry<String, String>> iter = params.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, String> element = iter.next();
			builder.append(element.getKey());
			builder.append("=");
			builder.append(element.getValue());
			builder.append("&");
		}

		if (builder.length() > 0)
		{
			builder = builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public static String toUrlEncodeString(List<? extends NameValuePair> params, String encode)
			throws UnsupportedEncodingException
	{
		StringBuilder builder = new StringBuilder();
		Iterator<? extends NameValuePair> iterator = params.iterator();
		while (iterator.hasNext())
		{
			NameValuePair pair = iterator.next();
			builder.append(pair.getName()).append("=");
			builder.append(URLEncoder.encode(pair.getValue(), encode)).append("&");
		}
		if (builder.length() > 0)
		{
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public static String toUrlString(List<? extends NameValuePair> params)
	{
		StringBuilder builder = new StringBuilder();
		Iterator<? extends NameValuePair> iterator = params.iterator();
		while (iterator.hasNext())
		{
			NameValuePair pair = iterator.next();
			builder.append(pair.getName()).append("=");
			builder.append(pair.getValue()).append("&");
		}
		if (builder.length() > 0)
		{
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public static String newPost(String reqUrl, String params) throws ClientProtocolException, IOException
	{
		HttpPost post = new HttpPost(reqUrl);
		post.setEntity(new StringEntity(params, UTF_8));
		return doRequest(post, UTF_8, false);
	}
}
