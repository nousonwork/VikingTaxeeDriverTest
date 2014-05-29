package test.com.cabgurudriver.activity;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.cabgurudriver.pojo.gson.Driver;
import com.cabgurudriver.util.Constants;
import com.cabgurudriver.util.GsonJsonParser;
import com.cabgurudriver.util.HTTPConnectionManager;
import com.cabgurudriver.util.MySQLiteHelper;


public class CabGuruDriverDashBoardActivity extends Activity {
	/** Called when the activity is first created. */

	MySQLiteHelper mysqliteHelper;
	RadioGroup radioGroup1 = null;
	RelativeLayout relativeLayout = null;
	RadioButton free = null;
	RadioButton busy = null;
	RadioButton waiting = null;
	RadioButton ontheway = null;
	TextView textView1 = null;
	Driver driver = null;
	String driverPhone = null;
	String driverId = null;
	String phone = null;
	String driverStatus = null;
	String bookingStatus = null;

	String longt = null;
	String lat = null;
	boolean threadStatus = true;

	Handler handler = null;
	private Thread statusThread;
	String TAG = "CabGuruDriverDashBoardActivity";

	@SuppressWarnings("static-access")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.setTitle("Driver Dashboard");

		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");
		bookingStatus = intent.getStringExtra("bookingStatus");

		free = (RadioButton) getInstance().findViewById(R.id.free);
		busy = (RadioButton) getInstance().findViewById(R.id.busy);
		waiting = (RadioButton) getInstance().findViewById(R.id.waiting);
		ontheway = (RadioButton) getInstance().findViewById(R.id.ontheway);
		relativeLayout = (RelativeLayout) findViewById(R.id.driver_dashboard);
		textView1 = (TextView) getInstance().findViewById(R.id.textView1);
		
		toggelRadioButton(bookingStatus);

		/*LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				mlocListener);*/

		mysqliteHelper = new MySQLiteHelper(getApplicationContext());

		radioGroup1 = (RadioGroup) getInstance().findViewById(R.id.radioGroup1);
		driver = mysqliteHelper.getDriverData(phone);
		driverPhone = driver.getPhoneNumber();
		driverId = driver.getDriverId();

		if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_CONFORMED_MSG)) {
			toggleScreenColor(Constants.DRIVER_STATUS_BUSY_STR);
			mysqliteHelper.updateDriverData(phone, driverId, Constants.DRIVER_STATUS_BUSY_STR);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_DENIED_MSG)) {
			toggleScreenColor(Constants.DRIVER_STATUS_FREE_STR);
			mysqliteHelper.updateDriverData(phone, driverId, Constants.DRIVER_STATUS_FREE_STR);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_ON_THE_WAY_MSG)) {
			toggleScreenColor(Constants.DRIVER_STATUS_ON_THE_WAY_STR);
			mysqliteHelper.updateDriverData(phone, driverId, Constants.DRIVER_STATUS_ON_THE_WAY_STR);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_DROPPED_MSG)) {
			toggleScreenColor(Constants.DRIVER_STATUS_FREE_STR);
			mysqliteHelper.updateDriverData(phone, driverId, Constants.DRIVER_STATUS_FREE_STR);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_SCHEDULED_MSG)) {
			toggleScreenColor(Constants.DRIVER_STATUS_BUSY_STR);
			mysqliteHelper.updateDriverData(phone, driverId, Constants.DRIVER_STATUS_BUSY_STR);
		}

		radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// RadioButton radioButton =
				// (RadioButton)radioGroup1.getFocusedChild();

				// boolean checked = radioButton.isChecked();
				boolean checked = true;
				int selectedId = radioGroup1.getCheckedRadioButtonId();

				switch (selectedId) {
				case R.id.free:
					if (checked) {
						relativeLayout.setBackgroundColor(new Color()
								.parseColor("#008000"));
						free.setTextColor(Color.WHITE);
						busy.setTextColor(Color.WHITE);
						waiting.setTextColor(Color.WHITE);
						ontheway.setTextColor(Color.WHITE);
						textView1.setTextColor(Color.WHITE);
						mysqliteHelper.updateDriverData(driverPhone, driverId,
								Constants.DRIVER_STATUS_FREE_STR);
						driverStatus = Constants.DRIVER_STATUS_FREE_STR;

					}
					break;
				case R.id.busy:
					if (checked) {

						relativeLayout.setBackgroundColor(Color.RED);
						free.setTextColor(Color.WHITE);
						busy.setTextColor(Color.WHITE);
						waiting.setTextColor(Color.WHITE);
						ontheway.setTextColor(Color.WHITE);
						textView1.setTextColor(Color.WHITE);
						mysqliteHelper.updateDriverData(driverPhone, driverId,
								Constants.DRIVER_STATUS_BUSY_STR);
						driverStatus = Constants.DRIVER_STATUS_BUSY_STR;
					}
					break;
				case R.id.waiting:
					if (checked) {
						relativeLayout.setBackgroundColor(Color.YELLOW);
						free.setTextColor(Color.BLACK);
						busy.setTextColor(Color.BLACK);
						waiting.setTextColor(Color.BLACK);
						ontheway.setTextColor(Color.BLACK);
						textView1.setTextColor(Color.BLACK);
						mysqliteHelper.updateDriverData(driverPhone, driverId,
								Constants.DRIVER_STATUS_WAITING_STR);
						driverStatus = Constants.DRIVER_STATUS_WAITING_STR;
					}
					break;
				case R.id.ontheway:
					if (checked) {
						relativeLayout.setBackgroundColor(Color.BLUE);
						free.setTextColor(Color.WHITE);
						busy.setTextColor(Color.WHITE);
						waiting.setTextColor(Color.WHITE);
						ontheway.setTextColor(Color.WHITE);
						textView1.setTextColor(Color.WHITE);
						mysqliteHelper.updateDriverData(driverPhone, driverId,
								Constants.DRIVER_STATUS_ON_THE_WAY_STR);
						driverStatus = Constants.DRIVER_STATUS_ON_THE_WAY_STR;
					}
					break;
				}

				driver = mysqliteHelper.getDriverData(phone);
				// toastMaker(driver.getDriverStatus());
			}
		});

		ImageButton signupButton = (ImageButton) findViewById(R.id.currentBookingButton);
		signupButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ***Do what you want with the click here***

				Bundle sendBundle = new Bundle();
				sendBundle.putString("value", "");
				sendBundle.putString("phone", phone);

				Intent i = new Intent(CabGuruDriverDashBoardActivity.this,
						CustomerBookingDetailsActivity.class);
				i.putExtras(sendBundle);
				startActivity(i);
				finish();

			}
		});

		// check if the thread is already running
		statusThread = (Thread) getLastNonConfigurationInstance();
		if (statusThread != null && statusThread.isAlive()) {
			//toastMaker("Status Update Thread is Running.");
		} else {
			statusThread = new MyThread();
			statusThread.start();
			DEBUG("Driver Status update thread started");
		}
		
		
		
		
		 handler = new Handler() {
			 
	           // Create handleMessage function
	 
	          public void handleMessage(Message msg) {
	                   
	                  String aResponse = msg.getData().getString("message");
	                  String aResponseCode = msg.getData().getString("messageCode");
	                  String aResponseScreen = msg.getData().getString("message-for-screen");
	                  String aResponseButton = msg.getData().getString("message-for-button");
	 
	                  if ((null != aResponse)) {
	 
	                      //ALERT MESSAGE
	                     /* Toast.makeText(
	                              getBaseContext(),
	                              "Server Response: "+aResponse,
	                              Toast.LENGTH_SHORT).show();*/
	                      toastMakerLong(aResponse);
	                      
	                      if(aResponseScreen != null && !aResponseCode.equalsIgnoreCase(Constants.BOOKING_DROPPED_CODE)){
	                    	  toggleScreenColor(aResponseScreen);
	                      }
	                      if(aResponseButton != null && !aResponseCode.equalsIgnoreCase(Constants.BOOKING_DROPPED_CODE)){
	                    	  toggelRadioButton(aResponseButton);
	                      }
	                      
	                      
	                      
	                     
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

	private void toggleScreenColor(String driverStatus) {

		if (driverStatus.equalsIgnoreCase(Constants.DRIVER_STATUS_FREE_STR)) {
			relativeLayout
					.setBackgroundColor(new Color().parseColor("#008000"));
			free.setTextColor(Color.WHITE);
			busy.setTextColor(Color.WHITE);
			waiting.setTextColor(Color.WHITE);
			ontheway.setTextColor(Color.WHITE);
			textView1.setTextColor(Color.WHITE);
			radioGroup1.check(R.id.free);
		} else if (driverStatus.equalsIgnoreCase(Constants.DRIVER_STATUS_BUSY_STR)) {
			relativeLayout.setBackgroundColor(Color.RED);
			free.setTextColor(Color.WHITE);
			busy.setTextColor(Color.WHITE);
			waiting.setTextColor(Color.WHITE);
			ontheway.setTextColor(Color.WHITE);
			textView1.setTextColor(Color.WHITE);
			radioGroup1.check(R.id.busy);
		} else if (driverStatus.equalsIgnoreCase(Constants.DRIVER_STATUS_WAITING_STR)) {
			relativeLayout.setBackgroundColor(Color.YELLOW);
			free.setTextColor(Color.BLACK);
			busy.setTextColor(Color.BLACK);
			waiting.setTextColor(Color.BLACK);
			ontheway.setTextColor(Color.BLACK);
			textView1.setTextColor(Color.BLACK);
			radioGroup1.check(R.id.waiting);
		} else if (driverStatus.equalsIgnoreCase(Constants.DRIVER_STATUS_ON_THE_WAY_STR)) {
			relativeLayout.setBackgroundColor(Color.BLUE);
			free.setTextColor(Color.WHITE);
			busy.setTextColor(Color.WHITE);
			waiting.setTextColor(Color.WHITE);
			ontheway.setTextColor(Color.WHITE);
			textView1.setTextColor(Color.WHITE);
			radioGroup1.check(R.id.ontheway);
		}
	}

	private void toggelRadioButton(String bookingStatus) {

		if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_CONFORMED_MSG)) {
			free.setVisibility(View.GONE);
			busy.setVisibility(View.VISIBLE);
			ontheway.setVisibility(View.GONE);
			waiting.setVisibility(View.GONE);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_DENIED_MSG)) {
			free.setVisibility(View.VISIBLE);
			busy.setVisibility(View.VISIBLE);
			ontheway.setVisibility(View.VISIBLE);
			waiting.setVisibility(View.VISIBLE);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_ON_THE_WAY_MSG)) {
			free.setVisibility(View.GONE);
			busy.setVisibility(View.GONE);
			ontheway.setVisibility(View.VISIBLE);
			waiting.setVisibility(View.GONE);
		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_DROPPED_MSG)) {
			free.setVisibility(View.VISIBLE);
			busy.setVisibility(View.VISIBLE);
			ontheway.setVisibility(View.VISIBLE);
			waiting.setVisibility(View.VISIBLE);

		} else if (bookingStatus.equalsIgnoreCase(Constants.BOOKING_SCHEDULED_MSG)) {
			free.setVisibility(View.GONE);
			busy.setVisibility(View.VISIBLE);
			ontheway.setVisibility(View.GONE);
			waiting.setVisibility(View.GONE);
		} else if (bookingStatus.equalsIgnoreCase(Constants.DRIVER_STATUS_WAITING_STR)) {
			free.setVisibility(View.GONE);
			busy.setVisibility(View.GONE);
			ontheway.setVisibility(View.GONE);
			waiting.setVisibility(View.VISIBLE);
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
			driverStatusDetailsRequestJson.put("driverId", driverId);
			driverStatusDetailsRequestJson.put("phoneNumber", driverPhone);
			driverStatusDetailsRequestJson.put("driverStatus", mysqliteHelper
					.getDriverData(phone).getStatus());

			

			/*if (longt != null && longt.length() > 2
					&& lat != null && lat.length() > 2) {
				
				driverStatusDetailsRequestJson.put("currLat", lat);
				driverStatusDetailsRequestJson.put("currLongt", longt);
				driverStatusDetailsRequestJson.put("currAddr", GsonJsonParser.getAddressByLatLong(lat, longt).getCurrAddr());
				
			} else {
				driverStatusDetailsRequestJson.put("currLongt", "-93.28786478");
				driverStatusDetailsRequestJson.put("currLat", "44.86392773");
				driverStatusDetailsRequestJson.put("currAddr", GsonJsonParser.getAddressByLatLong("44.86392773", "-93.28786478").getCurrAddr());
			}
*/
			

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
				//toastMakerLong("code="+ code);
				
				
				
				if (code.equalsIgnoreCase("200")) {

				}else if (code.equalsIgnoreCase(Constants.BOOKING_SCHEDULED_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "New Booking assigned.");
			        b.putString("messageCode", Constants.BOOKING_SCHEDULED_CODE);
			        b.putString("message-for-screen", Constants.DRIVER_STATUS_BUSY_STR);
			        b.putString("message-for-button", Constants.BOOKING_SCHEDULED_MSG);
			        msgObj.setData(b);
			        handler.sendMessage(msgObj);
			        
			        
			        try {
			            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			            r.play();
			        } catch (Exception e) {}
			        
			        
			        
			        
				}else if (code.equalsIgnoreCase(Constants.BOOKING_CONFORMED_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "Booking confirmed. Please pick up the customer.");
			        b.putString("messageCode", Constants.BOOKING_CONFORMED_CODE);
			        b.putString("message-for-screen", Constants.DRIVER_STATUS_WAITING_STR);
			        b.putString("message-for-button", Constants.DRIVER_STATUS_WAITING_STR);
			        msgObj.setData(b);
			        handler.sendMessage(msgObj);
				}else if (code.equalsIgnoreCase(Constants.BOOKING_ON_THE_WAY_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "Press the \"Dropped\" button after customer is dropped.");
			        b.putString("messageCode", Constants.BOOKING_ON_THE_WAY_CODE);
			        b.putString("message-for-screen", Constants.DRIVER_STATUS_ON_THE_WAY_STR);
			        b.putString("message-for-button", Constants.BOOKING_ON_THE_WAY_MSG);
			        msgObj.setData(b);
			        handler.sendMessage(msgObj);
				}else if (code.equalsIgnoreCase(Constants.BOOKING_DROPPED_CODE)) {					
					Message msgObj = handler.obtainMessage();
			        Bundle b = new Bundle();
			        b.putString("message", "No bookings.");
			        b.putString("messageCode", Constants.BOOKING_DROPPED_CODE);
			        b.putString("message-for-screen", Constants.DRIVER_STATUS_FREE_STR);
			        b.putString("message-for-button", Constants.BOOKING_DROPPED_MSG);
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

	public CabGuruDriverDashBoardActivity getInstance() {
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
		// sendBundle.putString("phone", phone);
		Intent i = new Intent(CabGuruDriverDashBoardActivity.this,
				CabGuruDriverActivity.class);
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

			Intent i = new Intent(CabGuruDriverDashBoardActivity.this,
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