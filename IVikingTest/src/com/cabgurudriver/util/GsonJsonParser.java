package com.cabgurudriver.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;



import android.util.Log;

import com.cabgurudriver.pojo.gson.Driver;
import com.cabgurudriver.pojo.gson.GoogleDistanceMatrixResponse;
import com.cabgurudriver.pojo.gson.GoogleGeoCodeResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class GsonJsonParser {
	
	static String TAG = "GsonJsonParser";	
	
	public static void main_5(String args[]) {
		
		String from = "326, Road Number 34, Hindustan Steel Industries, Wagle Industrial Estate, Thane West, Thane, Maharashtra 400604, India";
		
		String to = "PN-6, Thane-Belapur Road, Subhash Nagar, Dighe, Navi Mumbai, Maharashtra 400708, India";
		
		try {
			Map latlng = getLatLongtByAddress(to);
			
			DEBUG(latlng.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void main_4(String args[]) {
		
		String fromAddr = "Ghansoli Railway Station, MIDC Industrial Area, Ghansoli, Navi Mumbai, Maharashtra 400701";
		String driverAddr = "PN-6, Thane-Belapur Road, Subhash Nagar, Dighe, Navi Mumbai, Maharashtra 400708, India";
		
		try {
			
			
			String distance = getDistanceByAddress(fromAddr,driverAddr);
			
			DEBUG("Distance = "+ distance);
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String args[]) {
		
		BufferedReader br = null;
		FileInputStream fis =  null;
		try {
			fis = new FileInputStream("E:/CabGuru/gpsdata.txt");
			
			br = new BufferedReader(new InputStreamReader(fis));
			
			String str=null;
			ArrayList<Driver> gpsDataArryList = new ArrayList<Driver>();
			int counter = 1;
			
			while((str = br.readLine())!= null){				
				//DEBUG(str);
				String tmpArry []= str.split(",");
				//DEBUG(tmpArry[2]+", "+ tmpArry[1] );
				
				
				gpsDataArryList.add(getAddressByLatLong(tmpArry[2].trim(),tmpArry[1].trim()));
				Thread.sleep(100);
			}
			
			
			for(Driver gd : gpsDataArryList){
				
				//DEBUG("Lat = " + gd.getCurrLat());
				//DEBUG("Longt = "+ gd.getCurrLongt());
				//DEBUG("Address = "+ gd.getCurrAddr());
				DEBUG(gd.getCurrAddr());
			}
			
			
		/*	int selectedIndex = 0;
			Random randomGenerator = new Random();
		    for (int idx = 1; idx <= 10; ++idx){
		      int randomInt = randomGenerator.nextInt(100);	      
		      if(randomInt <=35){
		    	  selectedIndex = randomInt;
		    	  break;
		      }
		    }
		    DEBUG("Generated : " + selectedIndex);
		    
		   // ArrayList<Double> distanceArryList = new ArrayList<Double>();
		    TreeMap<Double, DriverMaster> distMap = new TreeMap<Double, DriverMaster>();
		    
		    for(int i =0; i < gpsDataArryList.size() ; i++){
		    	
		    	if(i != selectedIndex){
		    		
		    		DriverMaster fromGD = gpsDataArryList.get(selectedIndex);
		    		DriverMaster direverGD = gpsDataArryList.get(i);
		    		
		    		String distanceStr = getDistanceByAddress(fromGD.getAddress(), 
		    				direverGD.getAddress());
		    		
		    		double distValue = Double.parseDouble(distanceStr.trim());
		    		direverGD.setDistance(distValue+"");
		    		//distanceArryList.add(distValue);
		    		distMap.put(new Double(distValue),  direverGD);    		
		    		
		    	}
		    	
		    }
		    
		    DEBUG(distMap);*/
			
			
			
		    
		    
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				
				if(br != null){
					br.close();
				}
				if(fis != null){
					fis.close();
				}
				
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	public static void main_2(String args[]) {
		int selectedIndex = 0;
		Random randomGenerator = new Random();
	    for (int idx = 1; idx <= 10; ++idx){
	      int randomInt = randomGenerator.nextInt(100);	      
	      if(randomInt <=35){
	    	  selectedIndex = randomInt;
	    	  break;
	      }
	    }
	    DEBUG("Generated : " + selectedIndex);
		
	}
	
	
	public static void main_1(String args[]) {

		try {
			//String address = "40.714224,-73.961452";
			String lat = "19.116299";
			String longt = "73.008021";

			Driver gd = getAddressByLatLong(lat,longt);
			
			
			DEBUG("Lat = " + gd.getCurrLat());
			DEBUG("Longt = "+ gd.getCurrLongt());
			DEBUG("Address = "+ gd.getCurrAddr());
			

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*public static void recordGPSLocation(GPSData gd) {

		try {
			
			DatabaseManager.insertGPSData(gd);
			
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	public static Driver getAddressByLatLong(String lat, String longt) throws IOException {
		
		String latlong = lat+","+longt;

		URL url = new URL(
				"http://maps.googleapis.com/maps/api/geocode/json?latlng="
						+ URLEncoder.encode(latlong, "UTF-8") + "&sensor=false");
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		String jsonResult = "";
		while ((inputLine = in.readLine()) != null) {
			jsonResult += inputLine;
		}
		in.close();
		
		//DEBUG(jsonResult);
		
		
		Gson gson = new Gson();
		GoogleGeoCodeResponse result = gson.fromJson(jsonResult,GoogleGeoCodeResponse.class);

		double latValue = Double
				.parseDouble(result.results[0].geometry.location.lat);

		double lngValue = Double
				.parseDouble(result.results[0].geometry.location.lng);
		
		String formatted_address = result.results[0].formatted_address;

		//DEBUG("formatted address = " + formatted_address);
		
		Driver gd = new Driver();
		
		gd.setCurrLat(latValue+"");
		gd.setCurrLongt(lngValue+"");		
		gd.setCurrAddr(formatted_address);
		
		return gd;
	}
	
	
	public static HashMap<String, String> getLatLongtByAddress(String addr) throws IOException {

		//System.getProperties().put("http.proxyHost", "rtecproxy.ril.com");
		//System.getProperties().put("http.proxyPort", "8080");
		//System.getProperties().put("http.proxyUser", "shankar.mohanty");
		//System.getProperties().put("http.proxyPassword", "cu141#123");
		HashMap<String, String> map = new HashMap<String, String>();
		URL url = new URL(
				"http://maps.googleapis.com/maps/api/geocode/json?address="
		+URLEncoder.encode(addr, "UTF-8")
		+"&sensor=false");
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		String jsonResult = "";
		while ((inputLine = in.readLine()) != null) {
			jsonResult += inputLine;
		}
		in.close();
		
		
		Gson gson = new Gson();
		GoogleGeoCodeResponse result = gson.fromJson(jsonResult,GoogleGeoCodeResponse.class);

		double latValue = Double
				.parseDouble(result.results[0].geometry.location.lat);

		double lngValue = Double
				.parseDouble(result.results[0].geometry.location.lng);

		DEBUG("latValue = " + latValue + ",lngValue ="+ lngValue);
		
		
		map.put("lat", latValue+"");
		map.put("longt", lngValue+"");
		
		
		return map;
	}
	
	
	
	
	public static String getDistanceByAddress(String fromAddr, String driverAddr) throws IOException {

		//System.getProperties().put("http.proxyHost", "rtecproxy.ril.com");
		//System.getProperties().put("http.proxyPort", "8080");
		//System.getProperties().put("http.proxyUser", "shankar.mohanty");
		//System.getProperties().put("http.proxyPassword", "cu141#123");
		URL url = new URL(
				"http://maps.googleapis.com/maps/api/distancematrix/json?origins="
		+URLEncoder.encode(fromAddr, "UTF-8")
		+"&destinations="
		+URLEncoder.encode(driverAddr, "UTF-8")
		+"&mode=driving&language=en-US&sensor=false");
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		String jsonResult = "";
		while ((inputLine = in.readLine()) != null) {
			jsonResult += inputLine;
		}
		in.close();
		
		
		Gson gson = new Gson();
		GoogleDistanceMatrixResponse result = gson.fromJson(jsonResult,GoogleDistanceMatrixResponse.class);

		/*double latValue = Double
				.parseDouble(result.results[0].geometry.location.lat);

		double lngValue = Double
				.parseDouble(result.results[0].geometry.location.lng);*/
		
		String distance = result.rows[0].elements[0].distance.value;

		DEBUG("distance = " + distance);
		
		
		
		return distance;
	}
	
	private static void DEBUG(String msg) {
		Log.i(TAG, msg);
	}

}
