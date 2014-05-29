package test.com.cabgurudriver.activity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.cabgurudriver.pojo.gson.BookingDetails;
import com.cabgurudriver.util.Constants;
import com.cabgurudriver.util.HTTPConnectionManager;
import com.cabgurudriver.util.MySQLiteHelper;

public class CabGuruDriverActivity extends Activity {
	/** Called when the activity is first created. */

	String TAG = "CabGuruDriverActivity";
	
	MySQLiteHelper mysqliteHelper;
	
	EditText phoneEditText = null;
	String phone = null;
	String driverId = null;
	BookingDetails bookingDetails = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_login);
		this.setTitle("Driver Dashboard");

		mysqliteHelper = new MySQLiteHelper(getApplicationContext());
		// SQLiteDatabase db = mysqliteHelper.getWritableDatabase();
		// mysqliteHelper.onCreate(db);
		// mysqliteHelper.onUpgrade(db, 1, 2);

		ImageButton loginButton = (ImageButton) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				// ***Do what you want with the click here***

				try {

					phoneEditText = (EditText) getInstance().findViewById(
							R.id.phoneEditText);
					phone = phoneEditText.getText().toString();

					if (phone.length() != 10) {

						toastMaker("Enter 10 digit phone number only.");
					} else {

						String responseData = (String) new AsyncTask() {

							@Override
							protected String doInBackground(Object... params) {

								JSONObject loginJson = new JSONObject();
								loginJson.put("phone", phone);

								// loginJson.put("phone", "1234567891");

								String responseData = HTTPConnectionManager
										.postDataToURL(
												getInstance(),
												"http://"
														+ Constants.CABGURU_SERVER_IP_PORT
														+ "/cabserver/drivers/login",
												loginJson.toJSONString()); // http://localhost:9797/cabguru/mds

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
							driverId = (String) obj.get("driverId");

							if (code.equalsIgnoreCase("200")) {

								// toastMaker(phoneEditText.getText().toString()+" , "+
								// driverId);

								DEBUG("Login successful");
								
								
								Intent ivSvc= new Intent(getInstance(), IVikingService.class);
								ivSvc.putExtra("driverId", driverId);
								getInstance().startService(ivSvc); 
								
								
								bookingDetails = (BookingDetails) new AsyncTask() {

									@Override
									protected BookingDetails doInBackground(Object... params) {

										BookingDetails bookingDetails_inner = HTTPConnectionManager
												.getBookings(getInstance(), phone,
														driverId);

										return bookingDetails_inner;
									}

								}.execute("").get();
								
								
								

								String driverStatus;
								String bookingStatus = null;

								if (bookingDetails != null
										&& bookingDetails.getCode()
												.equalsIgnoreCase("200")) {

									driverStatus = bookingDetails
											.getDriverStatus();

									bookingStatus = bookingDetails
											.getBookingStatus();

								} else {

									toastMaker("No Bookings.");
									driverStatus = Constants.DRIVER_STATUS_FREE_STR;
									bookingStatus = Constants.BOOKING_DROPPED_MSG;
									DEBUG("No Bookings.");
								}

								try {
									mysqliteHelper.insertDriverData(
											phoneEditText.getText().toString(),
											driverId, driverStatus);
									driverStatus = mysqliteHelper
											.getDriverData(phone).getStatus();
									/*
									 * toastMaker("While insert =" +
									 * driverStatus + " count = " +
									 * mysqliteHelper .getDriverDataCount());
									 */
									DEBUG("driver table data inserted");
								} catch (Exception e) {
									driverStatus = mysqliteHelper
											.getDriverData(phone).getStatus();
									/*
									 * toastMaker("While update=" + driverStatus
									 * + " count = " + mysqliteHelper
									 * .getDriverDataCount());
									 */

									mysqliteHelper.updateDriverData(phone,
											driverId, driverStatus);
									DEBUG("driver table data updated");
									e.printStackTrace();
								}

								// Driver driver =
								// mysqliteHelper.getDriverData();

								// toastMaker("From DB ="+driver.getPhone()+" , "+
								// driver.getDriverId());

								// mysqliteHelper.closeDB();

								Bundle sendBundle = new Bundle();
								sendBundle.putString("value", "");
								sendBundle.putString("phone", phone);
								if (bookingStatus != null) {
									sendBundle.putString("bookingStatus",
											bookingStatus);
								} else {
									sendBundle.putString("bookingStatus",
											Constants.BOOKING_DROPPED_MSG);
								}

								Intent i = new Intent(
										CabGuruDriverActivity.this,
										CabGuruDriverDashBoardActivity.class);
								i.putExtras(sendBundle);
								startActivity(i);
								finish();

							} else {
								DEBUG(msg);
								toastMaker(msg);
							}

						} else {
							toastMaker("Server Error.");
							DEBUG("Server error");
						}
					}
				} catch (Exception e) {
					toastMaker("Connection Error.");
					DEBUG("Connection error");
					e.printStackTrace();

				}
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public CabGuruDriverActivity getInstance() {
		return this;
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

	private void DEBUG(String msg) {
		Log.i(TAG, msg);
	}

}