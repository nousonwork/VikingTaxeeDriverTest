package com.cabgurudriver.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.xml.sax.InputSource;

import com.cabgurudriver.pojo.gson.BookingDetails;


import android.content.Context;
import android.util.Log;
import android.view.View;

public class HTTPConnectionManager {
	
	static String TAG = "HTTPConnectionManager";

	public static String getDataFromURL(Context context, String url) {

		StringBuffer sBuff = null;

		try {

			DefaultHttpClient httpClientS = CommonUtils
					.setHttpPostProxyParams(context); // Added by shankar for
														// proxy

			HttpPost httpPostS = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					10);

			// Adding JSON string data:
			// nameValuePairs.add(new BasicNameValuePair("json", jsonData));

			httpPostS.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse httpResponseS = httpClientS.execute(httpPostS);
			HttpEntity responseEntityS = httpResponseS.getEntity();

			InputSource isrc = new Utility()
					.retrieveInputStream(responseEntityS);

			InputStream is = isrc.getByteStream();

			int i = 0;

			sBuff = new StringBuffer();

			while ((i = is.read()) != -1) {

				sBuff.append((char) i);

			}

			Log.d("HTTPConnectionManager", sBuff.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sBuff.toString();

	}

	public static String postDataToURL(Context context, String url,
			String jsonData) {

		StringBuffer sBuff = null;

		try {

			DefaultHttpClient httpClientS = CommonUtils
					.setHttpPostProxyParams(context); // Added by shankar for
														// proxy

			HttpPost httpPostS = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					10);

			// Adding JSON string data:
			nameValuePairs.add(new BasicNameValuePair("json", jsonData));

			httpPostS.setEntity(new UrlEncodedFormEntity(
					nameValuePairs,
					org.apache.http.protocol.HTTP.DEFAULT_CONTENT_CHARSET));

			HttpResponse httpResponseS = httpClientS.execute(httpPostS);
			HttpEntity responseEntityS = httpResponseS.getEntity();

			InputSource isrc = new Utility()
					.retrieveInputStream(responseEntityS);

			InputStream is = isrc.getByteStream();

			int i = 0;

			sBuff = new StringBuffer();

			while ((i = is.read()) != -1) {

				sBuff.append((char) i);

			}

			Log.d("HTTPConnectionManager", sBuff.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

		if(sBuff != null){
			return sBuff.toString();
		}else{
			return null;
		}
		

	}

	
	public static BookingDetails getBookings(Context context, String phoneStr, String driverIdStr){
		BookingDetails bookingDetails = null;
		try{
			
			JSONObject bookingDetailsJson = new JSONObject();			
			bookingDetailsJson.put("phone", phoneStr);
			bookingDetailsJson.put("driverId", driverIdStr);
			DEBUG("bookingDetailsJson = " + bookingDetailsJson);
			
			String responseData = postDataToURL(
					context, "http://" + Constants.CABGURU_SERVER_IP_PORT
							+ "/cabserver/drivers/bookings/get",
					bookingDetailsJson.toJSONString()); // http://localhost:9797/cabguru/mds

			if (responseData != null) {
				// toastMaker(responseData);

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseData);

				String code = (String) obj.get("code");
				String msg = (String) obj.get("msg");

				if (code.equalsIgnoreCase("200")) {

					DEBUG("booking details received data =" + responseData);

					String bookingId = (String) obj.get("bookingId");
					String userId = (String) obj.get("userId");
					String driverId = (String) obj.get("driverId");
					String from = (String) obj.get("from");
					String to = (String) obj.get("to");
					String time = (String) obj.get("time");
					String bookingStatus = (String) obj.get("bookingStatus");
					String travellerName = (String) obj.get("travellerName");
					String driverName = (String) obj.get("driverName");
					String driverPhone = (String) obj.get("driverPhone");
					String vehicleNo = (String) obj.get("vehicleNo");
					String driverStatus = (String) obj.get("driverStatus");
					String driverCurrAddr = (String) obj.get("driverCurrAddr");
					String travellerPhone = (String) obj.get("travellerPhone");

					bookingDetails = new BookingDetails();
					bookingDetails.setBookingId(bookingId);
					bookingDetails.setUserId(userId);
					bookingDetails.setDriverId(driverId);
					bookingDetails.setFrom(from);
					bookingDetails.setTo(to);
					bookingDetails.setTime(time);
					bookingDetails.setBookingStatus(bookingStatus);
					bookingDetails.setTravellerName(travellerName);
					bookingDetails.setDriverName(driverName);
					bookingDetails.setDriverPhone(driverPhone);
					bookingDetails.setVehicleNo(vehicleNo);
					bookingDetails.setDriverStatus(driverStatus);
					bookingDetails.setDriverCurrAddr(driverCurrAddr);
					bookingDetails.setTravellerPhone(travellerPhone);
					
					bookingDetails.setCode(code);
					bookingDetails.setMsg(msg);

				

				} else {				
					DEBUG(msg);
				}

			} else {				
				DEBUG("No bookings assigned.");
			}		
			
		}catch(Exception e){
			e.printStackTrace();			
		}
				
		return bookingDetails;
	}
	
	
	private static void DEBUG(String msg){
		Log.i(TAG, msg);
	}
}
