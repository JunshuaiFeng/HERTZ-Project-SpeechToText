package info.androidhive.speechtotext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
//import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import info.androidhive.speechtotext.Common.Common;
import info.androidhive.speechtotext.Helper.Helper;
import info.androidhive.speechtotext.Model.OpenWeatherMap;


public class MainActivity extends AppCompatActivity implements LocationListener {

	private TextView txtSpeechInput, txtResponse, txtTemp;
	ImageView imageView;
	private ImageButton btnSpeak;
	private EditText edtInput;
	private Button edtButton;
	private ImageButton btnStopMusic;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	MediaPlayer mp;
	boolean playingMusic = false;

	static private int port;
	static private Socket socket;
	static private BufferedReader in;
	static private PrintWriter out;
	static private boolean PiConnected = false;



	ConversationService service = new ConversationService("2016-09-20");
	MessageRequest newMessage;
	MessageResponse response;
	StreamPlayer streamPlayer;
	private StreamPlayer player = new StreamPlayer();
	private TextToSpeech textService;
	TextToSpeech t1;

	static String textResult;
	String context;
	static String RESPONSE;

	//Weather
	LocationManager locationManager;
	String provider;
	static double lat, lng;
	OpenWeatherMap openWeatherMap = new OpenWeatherMap();

	int MY_PERMISSION = 0;

	//Floating Buttons
	FloatingActionButton fab, fab_Left, fab_Top;
	Animation Move_Left, Move_Top, Back_Left, Back_Top;
	boolean moveBack = false;

	@SuppressWarnings("deprecation")
	private void ttsUnder20(String text) {
		HashMap<String, String> map = new HashMap<>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
		t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void ttsGreater21(String text) {
		String utteranceId = this.hashCode() + "";
		t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
	}


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		txtResponse = (TextView) findViewById(R.id.txtResponse);
		edtInput = (EditText) findViewById(R.id.edtInput);
		edtButton = (Button) findViewById(R.id.edtButton);
		btnStopMusic = (ImageButton) findViewById(R.id.btnStopMusic);
		txtTemp = (TextView) findViewById(R.id.txtTemp);
		imageView = (ImageView) findViewById(R.id.imageView);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		fab_Left = (FloatingActionButton) findViewById(R.id.fab_Left);
		fab_Top = (FloatingActionButton) findViewById(R.id.fab_Top);

		Move_Left = AnimationUtils.loadAnimation(this, R.anim.move_left);
		Move_Top = AnimationUtils.loadAnimation(this, R.anim.move_top);

		Back_Left = AnimationUtils.loadAnimation(this, R.anim.back_left);
		Back_Top = AnimationUtils.loadAnimation(this, R.anim.back_top);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (moveBack == false) {
					Move();
					moveBack = true;
				} else {
					Back();
					moveBack = false;
				}
			}
		});

		// Left Float Button
		fab_Left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this,"Calling Hertz Customer Service",Toast.LENGTH_SHORT).show();
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:8006544173"));
				if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				startActivity(callIntent);
			}
		});

		// Top Float Button
		fab_Top.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent myIntent = new Intent(MainActivity.this, QuestionList.class);
				startActivityForResult(myIntent, 0);
			}
		});



		t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {if (status == TextToSpeech.SUCCESS) {
				int result = t1.setLanguage(Locale.US);
				if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

				}

			} else {

			}
			}
		});

		//Get Coordinates
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = locationManager.getBestProvider(new Criteria(), false);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


			ActivityCompat.requestPermissions(MainActivity.this, new String[]{
					Manifest.permission.INTERNET,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_NETWORK_STATE,
					Manifest.permission.SYSTEM_ALERT_WINDOW,
					Manifest.permission.WRITE_EXTERNAL_STORAGE


			}, MY_PERMISSION);
		}
		Location location = locationManager.getLastKnownLocation(provider);

		Criteria locationCritera = new Criteria();
		locationCritera.setAccuracy(Criteria.ACCURACY_COARSE);
		locationCritera.setAltitudeRequired(false);
		locationCritera.setBearingRequired(false);
		locationCritera.setCostAllowed(true);
		locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

		String providerName = locationManager.getBestProvider(locationCritera, true);

		/*if (location == null)
			Log.e("TAG","No Location");
		*/

		if (providerName != null && locationManager.isProviderEnabled(providerName)) {
			// Provider is enabled
			locationManager.requestLocationUpdates(providerName, 20000, 100, this.locationListener);
			//lat = location.getLatitude();
			//lng = location.getLongitude();
		} else {
			// Provider not enabled, prompt user to enable it
			Toast.makeText(this, "Turn on the GPS", Toast.LENGTH_LONG).show();
			Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			this.startActivity(myIntent);
		}


		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				btnStopMusic.setVisibility(View.INVISIBLE);
				txtTemp.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.INVISIBLE);
				promptSpeechInput();

			}
		});

		btnStopMusic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(playingMusic == true){
					//mp.seekTo(0);
					mp.pause();
					btnStopMusic.setBackgroundResource(R.drawable.play96);
					playingMusic = false;
				}else {
					mp.start();
					btnStopMusic.setBackgroundResource(R.drawable.pause96);
					playingMusic = true;
				}
			}
		});

		edtButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));
			}
		});



	}

	/**
	 * Showing google speech input dialog
	 * */
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				getString(R.string.speech_prompt));
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.speech_not_supported),
					Toast.LENGTH_SHORT).show();
		}
	}



	/**
	 * Receiving speech input
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String workspaceId = "a12e9223-d15b-4bb9-a774-47720827580d";
		service.setUsernameAndPassword("90099f72-f54c-4a4f-a344-b92becd9805e", "J2Z0AURpOe3s");
		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				txtSpeechInput.setText(result.get(0));



				newMessage = new MessageRequest.Builder()
						.inputText(result.get(0))
						// Replace with the context obtained from the initial request
						//.context(...)
						.build();
				//response = service.message(workspaceId, newMessage).execute();

				service.message(workspaceId, newMessage).enqueue(new ServiceCallback<MessageResponse>() {
					@Override
					public void onResponse(MessageResponse response) {
						System.out.println(response);
						String string =  response.toString();
						try {
							JSONObject root = new JSONObject(string);
							JSONObject output = new JSONObject(root.getString("output"));
							JSONArray text = new JSONArray(output.getString("text"));

							for(int i = 0; i< text.length(); i++) {
								textResult = text.getString(i);
								System.out.println("THE TEXT IS: " + textResult);
							}

						}catch(Exception ex){

							}
						}



					@Override
					public void onFailure(Exception e) {

					}


			});
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						// Actions to do after 2.5 seconds
						txtResponse.setText(String.valueOf(textResult));
						String utteranceId=this.hashCode() + "";
						t1.speak(String.valueOf(textResult), TextToSpeech.QUEUE_FLUSH, null, utteranceId);

					}
				}, 2500);

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {

						// Play music
						if (String.valueOf(textResult).startsWith("Playing some")){
							mp = MediaPlayer.create(MainActivity.this, R.raw.janji_heroes_tonight);
							mp.start();
							btnStopMusic.setVisibility(View.VISIBLE);
							playingMusic = true;
						}

						// Check weather
						if(String.valueOf(textResult).startsWith("Finding information about current weather")){
							new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));
							txtTemp.setVisibility(View.VISIBLE);
							imageView.setVisibility(View.VISIBLE);
						}

						// Turn on the light
						if(String.valueOf(textResult).startsWith("Ok. Turning on the lights")) {
							if (PiConnected == false) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											//connect("192.168.1.20", 8888);

											socket = new Socket("192.168.1.20", 8888);
											out = new PrintWriter(socket.getOutputStream(), true);
											in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
											System.out.println("Connected to Rasberry Pi");
											PiConnected = true;
											System.out.println("Turn On");
											out.println("ON");
										} catch (Exception ex) {
											System.out.println("Cannot connect to the RasPi: " + ex.getMessage());
										}
									}
								}).start();
							}else {
								System.out.println("Turn On");
								out.println("ON");
							}
						}

						// Turn off the light
						if(String.valueOf(textResult).startsWith("Ok. Turning off the lights")){

							try{

								System.out.println("Turn Off");
								out.println("OFF");

							}catch(Exception ex){
								System.out.println(ex.getMessage());
							}

						}
					}
				}, 5500);

			}
			break;
		}

		}
	}

	public void connect(String ipAddress, int port) {
		// TODO Auto-generated method stub
		boolean success;

		// Attempt connection with server
		try {
			socket = new Socket(ipAddress, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			success = true;
		} catch (UnknownHostException ex) {
			System.out.println("Unknown Host. ");
			success = false;
		} catch (IOException ex) {
			System.out.println("Couldn't get I/O for the connection. ");
			success = false;
		}

		// Connection was successful
		if(success) {
			// Set connection status
			//statusLabel.setText("Connected");

			// Disable connect button
			//connectButton.setEnabled(false);

			// Enable all other buttons
			//btnDisconnect.setEnabled(true);
			//btnOn.setEnabled(true);
			//btnOff.setEnabled(true);

		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			//this.gpsLocationReceived(location);
			new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));
		}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

	};


	@Override
	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
		lng = location.getLongitude();

		//new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{
					Manifest.permission.INTERNET,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_NETWORK_STATE,
					Manifest.permission.SYSTEM_ALERT_WINDOW,
					Manifest.permission.WRITE_EXTERNAL_STORAGE


			}, MY_PERMISSION);
		}
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(MainActivity.this, new String[]{
					Manifest.permission.INTERNET,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_NETWORK_STATE,
					Manifest.permission.SYSTEM_ALERT_WINDOW,
					Manifest.permission.WRITE_EXTERNAL_STORAGE


			}, MY_PERMISSION);
		}
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	//Float Buttons moves
	private void Move(){
		RelativeLayout.LayoutParams paramsLeft = (RelativeLayout.LayoutParams) fab_Left.getLayoutParams();
		RelativeLayout.LayoutParams paramsTop = (RelativeLayout.LayoutParams) fab_Top.getLayoutParams();

		paramsLeft.rightMargin = (int) (fab_Left.getWidth() * 1.3);
		paramsTop.bottomMargin = (int) (fab_Top.getHeight() * 1.3);

		fab.setSize(FloatingActionButton.SIZE_MINI);

		fab_Left.setLayoutParams(paramsLeft);
		fab_Left.startAnimation(Move_Left);

		fab_Top.setLayoutParams(paramsTop);
		fab_Top.startAnimation(Move_Top);

	}

	//Float Buttons goes back
	private void Back(){
		RelativeLayout.LayoutParams paramsLeft = (RelativeLayout.LayoutParams) fab_Left.getLayoutParams();
		RelativeLayout.LayoutParams paramsTop = (RelativeLayout.LayoutParams) fab_Top.getLayoutParams();

		paramsLeft.rightMargin -= (int) (fab_Left.getWidth() * 1.3);
		paramsTop.bottomMargin -= (int) (fab_Top.getHeight() * 1.3);

		fab.setSize(FloatingActionButton.SIZE_AUTO);

		fab_Left.setLayoutParams(paramsLeft);
		fab_Left.startAnimation(Back_Left);

		fab_Top.setLayoutParams(paramsTop);
		fab_Top.startAnimation(Back_Top);

		fab_Top.setVisibility(View.INVISIBLE);
		fab_Left.setVisibility(View.INVISIBLE);
	}

	private class GetWeather extends AsyncTask<String,Void,String>{
		ProgressDialog pd = new ProgressDialog(MainActivity.this);


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd.setTitle("Please wait...");
			pd.show();

		}


		@Override
		protected String doInBackground(String... params) {
			String stream = null;
			String urlString = params[0];

			Helper http = new Helper();
			stream = http.getHTTPData(urlString);
			return stream;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if(s.contains("Error: Not found city")){
				pd.dismiss();
				return;
			}
			Gson gson = new Gson();
			Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
			openWeatherMap = gson.fromJson(s,mType);
			pd.dismiss();

			//txtCity.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
			//txtLastUpdate.setText(String.format("Last Updated: %s", Common.getDateNow()));
			//txtDescription.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
			//txtHumidity.setText(String.format("%d%%",openWeatherMap.getMain().getHumidity()));
			//txtTime.setText(String.format("%s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
			txtTemp.setText(String.format("%.2f °C",openWeatherMap.getMain().getTemp()));
			Picasso.with(MainActivity.this).load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon())).into(imageView);

		}

	}
}
