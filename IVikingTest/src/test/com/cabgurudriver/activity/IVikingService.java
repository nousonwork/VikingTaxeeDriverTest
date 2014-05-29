package test.com.cabgurudriver.activity;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import test.com.cabgurudriver.activity.CustomerBookingDetailsActivity.MyLocationListener;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.cabgurudriver.util.Constants;
import com.cabgurudriver.util.GsonJsonParser;
import com.cabgurudriver.util.HTTPConnectionManager;

public class IVikingService extends Service {

	String TAG = "IVikingService";
	boolean threadStatus = true;
	String longt = null;
	String lat = null;
	String driverId = null;
	private Thread statusThread;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		driverId = intent.getStringExtra("driverId");
		
		LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);
		

		if (statusThread != null && statusThread.isAlive()) {
			// toastMaker("Status Update Thread is Running.");
		} else {
			statusThread = new MyThread();
			statusThread.start();
			DEBUG("IVikingService thread started");
		}

		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		threadStatus = false;
	}

	/* Class My Location Listener */

	public class MyLocationListener implements LocationListener

	{

		@Override
		public void onLocationChanged(Location loc)

		{
			if (threadStatus) {

				lat = "" + loc.getLatitude();

				longt = "" + loc.getLongitude();

				// String Text = "My current location is: " +

				// "Latitud = " + loc.getLatitude() +

				// "Longitud = " + loc.getLongitude();

				// Toast.makeText(getApplicationContext(),Text,Toast.LENGTH_LONG).show();
			}

		}

		@Override
		public void onProviderDisabled(String provider) {

			Toast.makeText(getApplicationContext(), "Gps Disabled",
					Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onProviderEnabled(String provider) {

			Toast.makeText(getApplicationContext(),

			"Gps Enabled",

			Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}/* End of Class MyLocationListener */

	public IVikingService getInstance() {
		return this;
	}

	@SuppressWarnings("unchecked")
	private void statusUpdate(String url) throws IOException {

		try {
			JSONObject driverStatusDetailsRequestJson = new JSONObject();
			driverStatusDetailsRequestJson.put("driverId", driverId);

			if (longt != null && longt.length() > 2 && lat != null
					&& lat.length() > 2) {

				driverStatusDetailsRequestJson.put("currLat", lat);
				driverStatusDetailsRequestJson.put("currLongt", longt);
				driverStatusDetailsRequestJson.put("currAddr", GsonJsonParser
						.getAddressByLatLong(lat, longt).getCurrAddr());

			} else {
				/*driverStatusDetailsRequestJson.put("currLongt", "-93.28786478");
				driverStatusDetailsRequestJson.put("currLat", "44.86392773");
				driverStatusDetailsRequestJson.put("currAddr", GsonJsonParser
						.getAddressByLatLong("44.86392773", "-93.28786478")
						.getCurrAddr());*/
				
				driverStatusDetailsRequestJson.put("currLongt", "123");
				driverStatusDetailsRequestJson.put("currLat", "123");
				driverStatusDetailsRequestJson.put("currAddr", "NoAddress");
			}

			String responseData = HTTPConnectionManager.postDataToURL(
					getInstance(), url,
					driverStatusDetailsRequestJson.toJSONString());

			if (responseData != null) {
				// toastMaker(responseData);

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseData);

				String code = (String) obj.get("code");

				if (code.equalsIgnoreCase("200")) {

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class MyThread extends Thread {
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			try {

				while (threadStatus) {
					// Simulate a slow network
					try {
						new Thread().sleep(20000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					statusUpdate("http://" + Constants.CABGURU_SERVER_IP_PORT
							+ "/cabserver/drivers/myloc");
					// Updates the user interface
					// handler.sendEmptyMessage(0);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {

			}
		}
	}

	private void DEBUG(String msg) {
		Log.i(TAG, msg);
	}
}
