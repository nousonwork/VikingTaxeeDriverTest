/*******************************************************************************
								(C)
					ZenMediaSolutions Pvt Ltd    	
								2008
            															30/6/2011
	All rights reserved. No part of this publication may be reproduced,translated
    or transmitted in any form or by any means, or stored in any retrieval
    system of any nature without prior written permission of the publisher.	
	The above statement would ensure that we give a copyright notice to the
	entire world that we are the original authors and publishers of the work
	and the copyright is vested on us.
 *******************************************************************************/

/*******************************************************************************
 *
 * FILE NAME   :  CommonUtils.java
 *
 * DESCRIPTION :  CommonUtils provides the utility functions required by the other
 *                modules of the project.
 *
 *                
 * AUTHOR      :  Shankar Prasad Mohanty
 *
 * DATE        :  January 3rd, 2012
 *
 ******************************************************************************/

package com.cabgurudriver.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;


//import vn.com.tma.mobile.service.LogService;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

//import com.zms.structures.FieldValuePair;
//import com.zms.structures.UserDetails;

public class CommonUtils {

	private static final String TAG = "CommonUtils";
	
	public static final String USER_AGENT_FILE_NAME = "user_agent.dat";

	private static String userAgent = null;
	private static int notificationCounter = (int) (new Date().getTime() / (int) 1000);

	private static StringBuilder formatBuilder = new StringBuilder();
	private static Formatter formatter = new Formatter(formatBuilder,
			Locale.getDefault());

	private static int fileCounter = 0;
	private static String deviceName;

	private static String guID;
	//private static UserDetails mUserDetails;

	//private static Context context;

	/*public static void setContext(Context ctx) {
		context = ctx;
	}*/

	public static void setUserAgent(String ua) {
		userAgent = ua;
	}

	public static String getUserAgent() {
		return userAgent;
	}

	public static synchronized int getUniqNotificationCounter() {

		return notificationCounter++;
	}

	public static synchronized int getFileCounter() {

		fileCounter = (fileCounter + 1) % 1000;
		return fileCounter;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];

			for (;;) {
				int count = is.read(bytes, 0, buffer_size);

				if (count == -1)
					break;

				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static String URLEncode(String url) {

		url = url.replaceAll("%", "%25");
		url = url.replaceAll(" ", "%20");
		url = url.replaceAll("&", "%26");
		url = url.replaceAll("<", "%3c");
		url = url.replaceAll(">", "%3e");

		if (url.indexOf('^') >= 0) {
			url = url.replaceAll("^", "%22"); // 5e
		}

		url = url.replaceAll("'", "%27");
		url = url.replaceAll("\"", "%22");
		url = url.replaceAll(",", "%2c");
		url = url.replaceAll(":", "%3a");

		if ((url.indexOf('{') >= 0) || (url.indexOf('}') >= 0)
				|| (url.indexOf('\\') >= 0) || (url.indexOf('|') >= 0)
				|| url.indexOf('^') >= 0 || (url.indexOf('`') >= 0)) {
			url = null;
			return url;
		}
		return url;
	}

	public static boolean checkConn(Context ctx) {
		ConnectivityManager conMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conMgr.getNetworkInfo(conMgr.TYPE_MOBILE).isConnectedOrConnecting()
				|| conMgr.getNetworkInfo(conMgr.TYPE_WIFI)
						.isConnectedOrConnecting()) {
			Log.v(TAG, "Able to connect to the network");
			return true;
		} else {
			Log.e(TAG, "Unable to connect to network");
		}
		return false;
	}

	public static String getDurationFormattedString(int durationInSeconds) {

		if (durationInSeconds < 0) {
			return "00:00";
		}

		int seconds = durationInSeconds % 60;
		int minutes = (durationInSeconds / 60) % 60;
		int hours = durationInSeconds / 3600;

		formatBuilder.setLength(0);

		if (hours > 0) {
			return formatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return formatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	public static String readMediaListLastUpdatedTimeFromFile(Context context,
			String listType) {

		FileInputStream fis = null;
		String lastUpdatedTime = null;
		try {
			// First read the user agent from private memory
			fis = context.openFileInput(listType);// listType as filename
			lastUpdatedTime = CommonUtils.convertStreamToString(fis);
			Log.v(TAG, "Read from media list lastupdated time = "
					+ lastUpdatedTime + " for listType=" + listType);

		} catch (Exception exp) {
			Log.v(TAG,
					"GetMedia list lastupdated time - exp:" + exp.getMessage());
		}
		return lastUpdatedTime; // returning null in case of exception occurred
	}

	public static void writeMediaListLastUpdatedTimeToFile(Context context,
			String listType, String lastUpdatedTime) {

		// writing to internal private memory
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(listType, Context.MODE_PRIVATE);
			fos.write(lastUpdatedTime.getBytes());
			fos.close();
			Log.v(TAG, "Lastupdated time " + lastUpdatedTime
					+ " successfully written to file for listType = "
					+ listType);

		} catch (Exception exp) {
			Log.v(TAG,
					"Failed to write lastUpdated time. exp:" + exp.getMessage());
			exp.printStackTrace();
		}
	}

	public static String convertStreamToString(InputStream is) throws Exception {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		String NL = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			sb.append(line + NL);
		}
		sb.replace(sb.length() - 1, sb.length(), "");
		is.close();
		return sb.toString();
	}

	public static void writeInputStreamToFile(InputStream is, String fileName,
			Context context) throws FileNotFoundException, IOException {

		FileOutputStream fos = null;

		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte buf[] = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
			fos.close();

		} catch (Exception exp) {
			Log.v(TAG, "Failed to write to file exp:" + exp.getMessage());
			exp.printStackTrace();
		}
	}

	public static InputStream readStreamFromFile(String fileName,
			Context context) {

		FileInputStream fis = null;
		try {
			// First read the user agent from private memory
			fis = context.openFileInput(fileName);// listType as filename

		} catch (Exception exp) {
			Log.v(TAG,
					"GetMedia list lastupdated time - exp:" + exp.getMessage());
		}
		return fis;
	}



	public static String getDeviceName() {
		return deviceName;
	}

	/*public static String loadGUID(Context appContext) {

		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {

			File sdDir = android.os.Environment.getExternalStorageDirectory();
			String filePath = sdDir + "/" + Constants.GUID_FILE_NAME;
			try {
				FileInputStream fileInputStream = new FileInputStream(filePath);
				DataInputStream in = new DataInputStream(fileInputStream);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));

				String strLine;
				// Read File Line By Line
				while ((strLine = br.readLine()) != null) {
					// Print the content on the console
					guID = strLine;
				}
				// Close the input stream
				in.close();

			} catch (FileNotFoundException exp) {
				Log.v(TAG, "File not found: " + exp.getMessage());

			} catch (IOException exp) {
				Log.v(TAG, "Unable to read the file [" + filePath + "]. exp: "
						+ exp.getMessage());
			}
		}
		return guID;
	}*/

	public static String getGUID() {

		return guID;
	}

	public static int getCost(String uniqID) {

		// Currently hard coding the cost value : // TODO: has to be removed
		int hashCode = uniqID.hashCode();
		if (hashCode < 0)
			hashCode = hashCode * -1;
		int cost = hashCode % 40 + 10;
		if (cost % 6 == 0 || cost % 8 == 0) {
			cost = 0;
		}
		return cost;
	}

	/*public static void setUserDetails(UserDetails userDetails) {
		mUserDetails = userDetails;
	}

	public static UserDetails getUserDetails() {
		return mUserDetails;
	}
*/
	public static void loadUserAgent(Context appContext) {

		if (getUserAgent() == null) {

			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				// First read the user agent from private memory
				fis = appContext.openFileInput(USER_AGENT_FILE_NAME);
				String ua = CommonUtils.convertStreamToString(fis);
				Log.v(TAG, "Read from file: Driver-Agent = " + ua);

				// setting the user agent in common utilities
				CommonUtils.setUserAgent(ua);

			} catch (Exception exp) {
				// File not found so create one and write the user agent to file
				// setting only once
				Looper.prepare();
				Log.v(TAG,
						"Driver-Agent not found in file, hence reading from WebView");
				String ua = new WebView(appContext).getSettings()
						.getUserAgentString();
				Log.v(TAG, "Driver-Agent = " + ua);

				// setting the user agent in common utilities
				setUserAgent(ua);

				// writing to internal private memory
				try {
					fos = appContext.openFileOutput(
							USER_AGENT_FILE_NAME,
							Context.MODE_PRIVATE);
					fos.write(ua.getBytes());
					fos.close();
					Log.v(TAG,
							"Driver-Agent successfully written to private memory");

				} catch (Exception exp1) {
					exp1.printStackTrace();
				}

			} finally {

				// fis is closed inside CommonUtils.convertStreamToString();
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException exp) {
						exp.printStackTrace();
					}
				}
			}
		}
	}

	/*public static String getRedirectedURL(String actualURL) {

		String redirectedURL = null;
		try {
			CustomHttpClientUtils httpClientUtils = new CustomHttpClientUtils();
			HttpClient client = httpClientUtils.getHttpClient();
			HttpGet request = new HttpGet();
			request.setHeader("Driver-Agent", CommonUtils.getUserAgent());

			request.setURI(new URI(actualURL));
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			Log.v(TAG, "status code = " + statusCode);
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {

				Header[] headers = response.getHeaders("Location");
				if (headers != null && headers.length != 0) {
					redirectedURL = headers[headers.length - 1].getValue();
				}
			}

		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return redirectedURL;
	}*/

	/*public static boolean isWifiEnabled() {
		Log.v(TAG, "-- WIFI STATUS --");

		if (context != null) {
			ConnectivityManager conMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnectedOrConnecting()) {
				WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
				Log.v(TAG, "-- WIFI is Enabled -- " + wifiInfo.getSSID());
				return true;
			}
		}
		return false;
	}*/

	public static String getWifiName(Context context) {
		Log.v(TAG, "-- Calling WIFI NAME --");
		if (context != null) {
			ConnectivityManager conMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnectedOrConnecting()) {
				WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
				Log.v(TAG, "-- WIFI is Enabled Name = " + wifiInfo.getSSID());
				return wifiInfo.getSSID();
			}
		}
		return "";
	}
	
	
public static DefaultHttpClient setHttpPostProxyParams(Context context, int connTimeout){
		
		Log.d(TAG, "ctx, connTimeout -- called");
	
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		 
		HttpParams params = new BasicHttpParams();
		
		/*Log.d(TAG, "----Add Proxy---");
		HttpHost proxy = new HttpHost(Constants.PROXY_HOST.trim(),
				Constants.PROXY_PORT);
		params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);*/		
		
		/*if ((getWifiName(context).trim()).equalsIgnoreCase("secure-impact")) {
			Log.d(TAG, "----Add Proxy---");
			HttpHost proxy = new HttpHost(Constants.PROXY_HOST.trim(),
					Constants.PROXY_PORT);
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}*/
		
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 
		HttpConnectionParams
		.setConnectionTimeout(params, connTimeout);
		HttpConnectionParams.setSoTimeout(params, connTimeout);
		
		
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);		
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient(cm, params);		
		
		return mHttpClient;		
	}
	
	
	
	
	public static DefaultHttpClient setHttpPostProxyParams(Context context){
		
		Log.d(TAG, "ctx -- called ");
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		 
		HttpParams params = new BasicHttpParams();
		
		/*Log.d(TAG, "----Add Proxy---");
		HttpHost proxy = new HttpHost(Constants.PROXY_HOST.trim(),
				Constants.PROXY_PORT);
		params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);*/		
		
		/*if ((getWifiName(context).trim()).equalsIgnoreCase("secure-impact")) {
			Log.d(TAG, "----Add Proxy---");
			HttpHost proxy = new HttpHost(Constants.PROXY_HOST.trim(),
					Constants.PROXY_PORT);
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}*/
		
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);		
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient(cm, params);		
		
		return mHttpClient;		
	}
	
	public static DefaultHttpClient setHttpPostProxyParams(){
		
		Log.d(TAG, "() default called ");
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		 
		HttpParams params = new BasicHttpParams();
		
		//Log.d(TAG, "----Add Proxy---");
		/*HttpHost proxy = new HttpHost(Constants.PROXY_HOST.trim(),
				Constants.PROXY_PORT);
		params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);*/
		
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);		
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient(cm, params);		
		
		return mHttpClient;
	}
	
	public static DefaultHttpClient setHttpPostProxyParams(int connTimeOut){
		
		Log.d(TAG, "connTimeout -- called");
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		 
		HttpParams params = new BasicHttpParams();
		
		/*Log.d(TAG, "----Add Proxy---");
		HttpHost proxy = new HttpHost(Constants.PROXY_HOST.trim(),
				Constants.PROXY_PORT);
		params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);*/
		
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 
		//HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, connTimeOut);
		HttpConnectionParams.setSoTimeout(params, connTimeOut);
		
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);		
		
		DefaultHttpClient mHttpClient = new DefaultHttpClient(cm, params);		
		
		return mHttpClient;
	}
	
	
	public static void putString(Context context, String category, String key, String value) {
		try {
			SharedPreferences setMyPref = context.getSharedPreferences(category,
					Activity.MODE_WORLD_READABLE);
			SharedPreferences.Editor prefsEditor = setMyPref.edit();
			prefsEditor.putString(key, value);
			prefsEditor.commit();
		} catch (Exception e) {
			Log.e(TAG, "Error when getString, return defaultValue ", e);

		}
	}
	
	
	
	public static String getString(Context context, String category, String key) {
		String defaultValue = "";
		try {
			SharedPreferences setMyPref = context.getSharedPreferences(category,
					Activity.MODE_WORLD_READABLE);
			return setMyPref.getString(key, defaultValue);
		} catch (Exception e) {
			Log.e(TAG, "Error when getString, return defaultValue "+defaultValue, e);
			return defaultValue;
		}
	}

	/*public static String getFieldValuePairAsXmlString(
			List<FieldValuePair> listFields) {

		if (listFields == null) {
			return null;
		}

		FieldValuePair[] fields = new FieldValuePair[listFields.size()];
		for (int i = 0; i < listFields.size(); i++) {
			fields[i] = listFields.get(i);
		}
		return getFieldValuePairAsXmlString(fields);
	}*/

	/*public static String getFieldValuePairAsXmlString(FieldValuePair[] fields) {

		String fieldsXmlData = null;
		if (fields != null && fields.length > 0) {
			// forming XML data : TODO: currently doing by self, later use
			// XstreamUtils
			fieldsXmlData = "<fieldValuePairData>";
			fieldsXmlData += "<fields>";
			for (int i = 0; i < fields.length; i++) {

				fieldsXmlData += "<field>";
				fieldsXmlData += "<fieldName>";
				fieldsXmlData += fields[i].fieldName;
				fieldsXmlData += "</fieldName>";

				fieldsXmlData += "<fieldValue>";
				fieldsXmlData += fields[i].fieldValue;
				fieldsXmlData += "</fieldValue>";

				fieldsXmlData += "</field>";
			}
			fieldsXmlData += "</fields>";
			fieldsXmlData += "</fieldValuePairData>";
		}
		return fieldsXmlData;
	}*/

	private void DEBUG(String msg){
		Log.i(TAG, msg);
	}
	
}
