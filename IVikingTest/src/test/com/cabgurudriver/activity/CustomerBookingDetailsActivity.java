package test.com.cabgurudriver.activity;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.cabgurudriver.pojo.gson.BookingDetails;
import com.cabgurudriver.pojo.gson.Driver;
import com.cabgurudriver.util.Constants;
import com.cabgurudriver.util.HTTPConnectionManager;
import com.cabgurudriver.util.MySQLiteHelper;

public class CustomerBookingDetailsActivity extends Activity {

	TextView fromTextViewValue = null;
	TextView toTextViewValue = null;
	TextView timeTextViewValue = null;
	TextView travellerNameTextViewValue = null;
	TextView travellerPhoneTextViewValue = null;
	MySQLiteHelper mysqliteHelper;
	// BookingDetails bd = null;
	Driver driver = null;
	String phone = null;
	String TAG = "CustomerBookingDetailsActivity";
	BookingDetails bookingDetails = null;

	String longt = null;
	String lat = null;
	boolean threadStatus = true;
	private Thread statusThread;

	Button acceptButton = null;
	Button pickedButton = null;
	Button droppedButton = null;
	Button denyBookingButton = null;
	Handler handler = null;
	
	JSONObject bookingStatusRequestJson = null;

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_booking_details);
		mysqliteHelper = new MySQLiteHelper(getApplicationContext());
		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");

		fromTextViewValue = (TextView) getInstance().findViewById(
				R.id.fromTextViewValue);

		toTextViewValue = (TextView) getInstance().findViewById(
				R.id.toTextViewValue);

		timeTextViewValue = (TextView) getInstance().findViewById(
				R.id.timeTextViewValue);

		travellerNameTextViewValue = (TextView) getInstance().findViewById(
				R.id.travellerNameTextViewValue);

		travellerPhoneTextViewValue = (TextView) getInstance().findViewById(
				R.id.travellerPhoneTextViewValue);

		/*LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);*/

		acceptButton = (Button) findViewById(R.id.acceptButton);
		pickedButton = (Button) findViewById(R.id.pickedButton);
		droppedButton = (Button) findViewById(R.id.droppedButton);
		denyBookingButton = (Button) findViewById(R.id.denyBookingButton);
		toggelButton(Constants.BOOKING_DROPPED_MSG);

		/** Fetching booking details Start **/

		try {

			try {

				driver = mysqliteHelper.getDriverData(phone);

			} catch (Exception e) {
				e.printStackTrace();
			}

			JSONObject bookingDetailsJson = new JSONObject();
			if (driver != null) {
				
				
				bookingDetails = (BookingDetails) new AsyncTask() {

					@Override
					protected BookingDetails doInBackground(Object... params) {

						BookingDetails bookingDetails_inner =HTTPConnectionManager.getBookings(
								getInstance(), driver.getPhoneNumber(), driver.getDriverId());

						return bookingDetails_inner;
					}

				}.execute("").get();				
			

				if (bookingDetails != null
						&& bookingDetails.getCode().equalsIgnoreCase("200")
						&& !bookingDetails.getBookingStatus().equalsIgnoreCase(Constants.BOOKING_DROPPED_MSG)) {

					fromTextViewValue.setText(bookingDetails.getFrom());
					toTextViewValue.setText(bookingDetails.getTo());
					timeTextViewValue.setText(bookingDetails.getTime());
					travellerNameTextViewValue.setText(bookingDetails
							.getTravellerName());
					travellerPhoneTextViewValue.setText(bookingDetails
							.getTravellerPhone());

					toggelButton(bookingDetails.getBookingStatus());

				} else {

					toastMaker("No Bookings.");
					DEBUG("No Bookings.");
				}

			} else {

				toastMaker("No driver details in cache.");
			}

		} catch (Exception e) {
			toastMaker("No bookings assigned.");
			DEBUG("No bookings assigned.");
			e.printStackTrace();

		}

		/** Fetching booking details end **/

		acceptButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ***Do what you want with the click here***

				try {
					if (bookingDetails != null) {
						mysqliteHelper.updateDriverData(driver.getPhoneNumber(),
								driver.getDriverId(), Constants.DRIVER_STATUS_BUSY_STR);
						// driver = mysqliteHelper.getDriverData(phone);
						// toastMaker(driver.getDriverStatus());
						bookingStatusRequestJson = new JSONObject();
						bookingStatusRequestJson.put("userId",
								bookingDetails.getUserId());
						bookingStatusRequestJson.put("bookingId",
								bookingDetails.getBookingId());
						bookingStatusRequestJson.put("bookingStatus",
								Constants.BOOKING_CONFORMED_MSG);
						bookingStatusRequestJson.put("bookingStatusCode",
								Constants.BOOKING_CONFORMED_CODE);							
						
						String responseData = (String) new AsyncTask() {

							@Override
							protected String doInBackground(Object... params) {

								JSONObject loginJson = new JSONObject();
								loginJson.put("phone", phone);

								// loginJson.put("phone", "1234567891");

								String responseData = HTTPConnectionManager
										.postDataToURL(getInstance(), "http://"
												+ Constants.CABGURU_SERVER_IP_PORT
												+ "/cabserver/drivers/bookings/status",
												bookingStatusRequestJson.toJSONString()); // http://localhost:9797/cabguru/mds

								return responseData;
							}

						}.execute("").get();
						
						
						
						
						
						
						if (responseData != null) {
							// toastMaker(responseData);

							JSONParser parser = new JSONParser();
							JSONObject obj = (JSONObject) parser
									.parse(responseData);

							String code = (String) obj.get("code");
							String msg = (String) obj.get("msg");

							if (code.equalsIgnoreCase("200")) {

								DEBUG("booking details received data ="
										+ responseData);
								toggelButton(Constants.BOOKING_CONFORMED_MSG);
								bookingDetails
										.setBookingStatus(Constants.BOOKING_CONFORMED_MSG);
							}

						}
					}
				} catch (Exception e) {
					DEBUG("exception while updating booking confirmed booking status = "
							+ e.getMessage());
					e.printStackTrace();
				}

			}
		});

		pickedButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ***Do what you want with the click here***
				// driver = mysqliteHelper.getDriverData(phone);
				// toastMaker(driver.getDriverStatus());

				try {
					if (bookingDetails != null) {
						mysqliteHelper.updateDriverData(driver.getPhoneNumber(),
								driver.getDriverId(), Constants.DRIVER_STATUS_ON_THE_WAY_STR);
						// driver = mysqliteHelper.getDriverData(phone);
						// toastMaker(driver.getDriverStatus());
						bookingStatusRequestJson = new JSONObject();
						bookingStatusRequestJson.put("userId",
								bookingDetails.getUserId());
						bookingStatusRequestJson.put("bookingId",
								bookingDetails.getBookingId());
						bookingStatusRequestJson.put("bookingStatus", Constants.BOOKING_ON_THE_WAY_MSG);
						bookingStatusRequestJson.put("bookingStatusCode",
								Constants.BOOKING_ON_THE_WAY_CODE);
												
						String responseData = (String) new AsyncTask() {

							@Override
							protected String doInBackground(Object... params) {

								JSONObject loginJson = new JSONObject();
								loginJson.put("phone", phone);

								// loginJson.put("phone", "1234567891");

								String responseData = HTTPConnectionManager
										.postDataToURL(getInstance(), "http://"
												+ Constants.CABGURU_SERVER_IP_PORT
												+ "/cabserver/drivers/bookings/status",
												bookingStatusRequestJson.toJSONString()); // http://localhost:9797/cabguru/mds

								return responseData;
							}

						}.execute("").get();
						
						
						
						
						
						if (responseData != null) {
							// toastMaker(responseData);

							JSONParser parser = new JSONParser();
							JSONObject obj = (JSONObject) parser
									.parse(responseData);

							String code = (String) obj.get("code");
							String msg = (String) obj.get("msg");

							if (code.equalsIgnoreCase("200")) {

								DEBUG("booking details received data ="
										+ responseData);
								toggelButton(Constants.BOOKING_ON_THE_WAY_MSG);
								bookingDetails.setBookingStatus(Constants.BOOKING_ON_THE_WAY_MSG);
							}

						}
					}
				} catch (Exception e) {
					DEBUG("exception while updating ontheway booking status = "
							+ e.getMessage());
					e.printStackTrace();
				}

			}
		});

		droppedButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ***Do what you want with the click here***
				// driver = mysqliteHelper.getDriverData(phone);
				// toastMaker(driver.getDriverStatus());

				try {
					if (bookingDetails != null) {
						mysqliteHelper.updateDriverData(driver.getPhoneNumber(),
								driver.getDriverId(), Constants.DRIVER_STATUS_FREE_STR);
						// driver = mysqliteHelper.getDriverData(phone);
						// toastMaker(driver.getDriverStatus());
						bookingStatusRequestJson = new JSONObject();
						bookingStatusRequestJson.put("userId",
								bookingDetails.getUserId());
						bookingStatusRequestJson.put("bookingId",
								bookingDetails.getBookingId());
						bookingStatusRequestJson.put("bookingStatus", Constants.BOOKING_DROPPED_MSG);
						bookingStatusRequestJson.put("bookingStatusCode",
								Constants.BOOKING_DROPPED_CODE);
												
						String responseData = (String) new AsyncTask() {

							@Override
							protected String doInBackground(Object... params) {

								JSONObject loginJson = new JSONObject();
								loginJson.put("phone", phone);

								// loginJson.put("phone", "1234567891");

								String responseData = HTTPConnectionManager
										.postDataToURL(getInstance(), "http://"
												+ Constants.CABGURU_SERVER_IP_PORT
												+ "/cabserver/drivers/bookings/status",
												bookingStatusRequestJson.toJSONString()); // http://localhost:9797/cabguru/mds

								return responseData;
							}

						}.execute("").get();
						
						
						
						
						if (responseData != null) {
							// toastMaker(responseData);

							JSONParser parser = new JSONParser();
							JSONObject obj = (JSONObject) parser
									.parse(responseData);

							String code = (String) obj.get("code");
							String msg = (String) obj.get("msg");

							if (code.equalsIgnoreCase("200")) {

								DEBUG("dropped booking details received data ="
										+ responseData);
								toggelButton(Constants.BOOKING_DROPPED_MSG);
								bookingDetails.setBookingStatus(Constants.BOOKING_DROPPED_MSG);
							}

						}
					}
				} catch (Exception e) {
					DEBUG("exception while updating dropped booking status = "
							+ e.getMessage());
					e.printStackTrace();
				}

			}
		});

		denyBookingButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ***Do what you want with the click here***
				// driver = mysqliteHelper.getDriverData(phone);
				// toastMaker(driver.getDriverStatus());

				try {
					if (bookingDetails != null) {
						mysqliteHelper.updateDriverData(driver.getPhoneNumber(),
								driver.getDriverId(), Constants.DRIVER_STATUS_FREE_STR);
						// driver = mysqliteHelper.getDriverData(phone);
						// toastMaker(driver.getDriverStatus());
						bookingStatusRequestJson = new JSONObject();
						bookingStatusRequestJson.put("userId",
								bookingDetails.getUserId());
						bookingStatusRequestJson.put("bookingId",
								bookingDetails.getBookingId());
						bookingStatusRequestJson
								.put("bookingStatus", Constants.BOOKING_DENIED_MSG);
						bookingStatusRequestJson.put("bookingStatusCode",
								Constants.BOOKING_DENIED_CODE);
												
						String responseData = (String) new AsyncTask() {

							@Override
							protected String doInBackground(Object... params) {

								JSONObject loginJson = new JSONObject();
								loginJson.put("phone", phone);

								// loginJson.put("phone", "1234567891");

								String responseData = HTTPConnectionManager
										.postDataToURL(getInstance(), "http://"
												+ Constants.CABGURU_SERVER_IP_PORT
												+ "/cabserver/drivers/bookings/status",
												bookingStatusRequestJson.toJSONString()); // http://localhost:9797/cabguru/mds

								return responseData;
							}

						}.execute("").get();
						
						
						if (responseData != null) {
							// toastMaker(responseData);

							JSONParser parser = new JSONParser();
							JSONObject obj = (JSONObject) parser
									.parse(responseData);

							String code = (String) obj.get("code");
							String msg = (String) obj.get("msg");

							if (code.equalsIgnoreCase("200")) {

								DEBUG("denied booking details received data ="
										+ responseData);
								toggelButton(Constants.BOOKING_DENIED_MSG);
								bookingDetails
										.setBookingStatus(Constants.BOOKING_DENIED_MSG);
							}

						}
					}
				} catch (Exception e) {
					DEBUG("exception while updating denied booking status = "
							+ e.getMessage());
					e.printStackTrace();
				}

			}
		});

		// check if the thread is already running
		statusThread = (Thread) getLastNonConfigurationInstance();
		if (statusThread != null && statusThread.isAlive()) {
			toastMaker("Status Update Thread is Running.");
		} else {
			statusThread = new MyThread();
			statusThread.start();
			DEBUG("Driver Status update thread started");
		}
		
		
		 handler = new Handler() {
			 
	           // Create handleMessage function
	 
	          public void handleMessage(Message msg) {
	                   
	                  String aResponse = msg.getData().getString("message");
	 
	                  if ((null != aResponse)) {
	 
	                      //ALERT MESSAGE
	                     /* Toast.makeText(
	                              getBaseContext(),
	                              "Server Response: "+aResponse,
	                              Toast.LENGTH_SHORT).show();*/
	                      toastMakerLong(aResponse);
	                  }
	                  else
	                  {
	                          //ALERT MESSAGE
	                         /* Toast.makeText(
	                                  getBaseContext(),
	                                  "Not Got Response From Server.",
	                                  Toast.LENGTH_SHORT).show();*/
	                          
	                          toastMakerLong("No bookings.");
	                  }    
	 
	              }
	          };

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return statusThread;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		threadStatus = false;
	}

	private void toggelButton(String bookingStatus) {

		if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_CONFORMED_MSG)) {
			acceptButton.setVisibility(View.GONE);
			pickedButton.setVisibility(View.VISIBLE);
			droppedButton.setVisibility(View.GONE);
			denyBookingButton.setVisibility(View.GONE);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_DENIED_MSG)) {
			acceptButton.setVisibility(View.GONE);
			pickedButton.setVisibility(View.GONE);
			droppedButton.setVisibility(View.GONE);
			denyBookingButton.setVisibility(View.GONE);
			toastMakerLong("Booking denied.");
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_ON_THE_WAY_MSG)) {
			acceptButton.setVisibility(View.GONE);
			pickedButton.setVisibility(View.GONE);
			droppedButton.setVisibility(View.VISIBLE);
			denyBookingButton.setVisibility(View.GONE);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_DROPPED_MSG)) {
			acceptButton.setVisibility(View.GONE);
			pickedButton.setVisibility(View.GONE);
			droppedButton.setVisibility(View.GONE);
			denyBookingButton.setVisibility(View.GONE);
			// toastMakerLong("Dropped.");
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_SCHEDULED_MSG)) {
			acceptButton.setVisibility(View.VISIBLE);
			pickedButton.setVisibility(View.GONE);
			droppedButton.setVisibility(View.GONE);
			denyBookingButton.setVisibility(View.VISIBLE);
		}

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

			Toast.makeText(getApplicationContext(),

			"Gps Disabled",

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

	// Utiliy method to download image from the internet
	@SuppressWarnings("unchecked")
	private void statusUpdate(String url) throws IOException {

		try {
			JSONObject driverStatusDetailsRequestJson = new JSONObject();
			driverStatusDetailsRequestJson
					.put("driverId", driver.getDriverId());
			driverStatusDetailsRequestJson
					.put("phoneNumber", driver.getPhoneNumber());
			driverStatusDetailsRequestJson.put("driverStatus", mysqliteHelper
					.getDriverData(phone).getStatus());

			/*driverStatusDetailsRequestJson.put("currAddr", "");

			if (longt != null && longt.length() > 2) {
				driverStatusDetailsRequestJson.put("currLongt", longt);
			} else {
				driverStatusDetailsRequestJson.put("currLongt", "-93.30805443");
			}

			if (lat != null && lat.length() > 2) {
				driverStatusDetailsRequestJson.put("currLat", lat);
			} else {
				driverStatusDetailsRequestJson.put("currLat", "44.86181571");
			}*/

			String responseData = HTTPConnectionManager.postDataToURL(
					getInstance(), url,
					driverStatusDetailsRequestJson.toJSONString());

			if (responseData != null) {
				// toastMaker(responseData);

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseData);

				String code = (String) obj.get("code");
				// String msg = (String) obj.get("msg");
				// String driverId = (String) obj.get("driverId");
				
				if (code.equalsIgnoreCase("200")) {

				}else if (code.equalsIgnoreCase(Constants.BOOKING_SCHEDULED_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "New Booking assigned.");
			        msgObj.setData(b);
			        handler.sendMessage(msgObj);
				}else if (code.equalsIgnoreCase(Constants.BOOKING_CONFORMED_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "Booking confirmed. Please pick up the customer.");
			        msgObj.setData(b);
			        handler.sendMessage(msgObj);
				}else if (code.equalsIgnoreCase(Constants.BOOKING_ON_THE_WAY_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "Press the \"Dropped\" button after customer is dropped.");
			        msgObj.setData(b);
			        handler.sendMessage(msgObj);
				}else if (code.equalsIgnoreCase(Constants.BOOKING_DROPPED_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "No bookings.");
			        msgObj.setData(b);
			        handler.sendMessage(msgObj);
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
							+ "/cabserver/drivers/statustemp");
					// Updates the user interface
					// handler.sendEmptyMessage(0);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {

			}
		}
	}

	public CustomerBookingDetailsActivity getInstance() {
		return this;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			Log.d(TAG, "onKeyDown Called");
			onBackPressed();

		}

		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		Log.d(TAG, "onBackPressed Called");

		Bundle sendBundle = new Bundle();
		sendBundle.putString("value", "");
		sendBundle.putString("phone", phone);
		if (bookingDetails != null) {
			sendBundle.putString("bookingStatus",
					bookingDetails.getBookingStatus());
		} else {
			sendBundle.putString("bookingStatus", Constants.BOOKING_DROPPED_MSG);
		}

		Intent i = new Intent(CustomerBookingDetailsActivity.this,
				CabGuruDriverDashBoardActivity.class);
		i.putExtras(sendBundle);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		finish();

		return;
	}

	void toastMaker(String text) {

		Context context = getApplicationContext();

		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	void toastMakerLong(String text) {

		Context context = getApplicationContext();

		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_customer_dash_board, menu);

		// menu.add("LogOut");

		CharSequence text = getInstance().getTitle();
		if (text.toString().contains("Guest")) {

		} else {
			menu.add("LogOut");
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		CharSequence text = item.getTitle();
		// toastMaker(text.toString());

		if (text.toString().equalsIgnoreCase("logout")) {
			Bundle sendBundle = new Bundle();
			sendBundle.putString("value", "");

			Intent i = new Intent(CustomerBookingDetailsActivity.this,
					CabGuruDriverActivity.class);
			i.putExtras(sendBundle);
			startActivity(i);
			finish();
		}

		return true;

		// Handle item selection
		/*
		 * switch (item.getItemId()) { case R.id.new_game: newGame(); return
		 * true; case R.id.help: showHelp(); return true; default: return
		 * super.onOptionsItemSelected(item); }
		 */
	}

	private void DEBUG(String msg) {
		Log.i(TAG, msg);
	}
}
