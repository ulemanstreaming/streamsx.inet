//
// *******************************************************************************
// * Copyright (C)2014, International Business Machines Corporation and *
// * others. All Rights Reserved. *
// *******************************************************************************
//
package com.ibm.streamsx.inet.http;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
//import org.apache.http.conn.params
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

class HTTPRequest {

	static final String 
	MIME_JSON = "application/json",  
	MIME_FORM = "application/x-www-form-urlencoded";

	static final String VALUE_PARAM = "value";

	private String url = null;
	private Map<String, String> headers =
			new HashMap<String, String> ();

	public static enum RequestType {GET, POST};
	private RequestType type = RequestType.GET;
	private boolean insecure = false;

	private HttpUriRequest req = null;
	private HttpEntity entity = null;
	private double connectionTimeout = -1.0;
        private HttpParams httpParams = null;

	public HTTPRequest(String url) {
		this.url =  url;
	}

	public String getUrl() {
		return url;
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	HttpUriRequest getReq() {
		return req;
	}

	public boolean isInsecure() {
		return insecure;
	}

	public void setInsecure(boolean insecure) {
		this.insecure = insecure;
	}
	
	public void setConnectionTimeout(double connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}	

	/**
	 * Set the parameters for a POST request
	 * @param params
	 * @throws Exception
	 */
	public void setParams(Map<String, String> params) throws Exception {
		if(params != null && params.size() > 0) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
			for(Map.Entry<String, String> entry : params.entrySet()) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			entity =  new UrlEncodedFormEntity(nameValuePairs);
			headers.put("Content-Type", MIME_FORM);
		}
	}
	
	/**
	 * Set the parameters for a POST request as a single string
	 * @param value
	 * @throws Exception
	 */
	public void setParams(String value) throws Exception {
		if(value != null) {
			entity = new StringEntity(value, Charset.forName("UTF-8").toString());
		}
	}

	/**
	 * Sends the request to the server and gets a response.
	 * @param auth
	 * @return
	 * @throws Exception
	 */
	public HTTPResponse sendRequest(IAuthenticate auth) throws Exception {
		HttpClient client;
		if(insecure) {
			client = HTTPUtils.getHttpClientWithNoSSLValidation();
		}
		else {
			client = new DefaultHttpClient();
		}
		
		if(type == RequestType.GET) {
			HttpGet get = new HttpGet(url);
			req=get;
		}
		else {
			HttpPost post = new HttpPost(url);
			if(entity != null) {
			    post.setEntity(entity);				
			}
			req = post;
			if (httpParams == null) {
			    httpParams = new BasicHttpParams();		
			    int cto = (int)(connectionTimeout * 1000.0);
			    HttpConnectionParams.setConnectionTimeout(httpParams, cto );
			}
			post.setParams(httpParams);				
		}

		if(headers.size() > 0) {
			for(Entry<String, String> entry : headers.entrySet()) {
				req.setHeader(entry.getKey(), entry.getValue());
			}
		}
		
		//sign the request
		auth.sign(this);
		
		return new HTTPResponse(client.execute(req));
	}

	@Override
	public String toString() {
		return "URL: " + url;
	}
}
