package info.androidhive.speechtotext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;




import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
//import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends Activity {

	private TextView txtSpeechInput, txtResponse;
	private ImageButton btnSpeak;
	private EditText edtInput;
	private Button edtButton;
	private final int REQ_CODE_SPEECH_INPUT = 100;


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

	@SuppressWarnings("deprecation")
	private void ttsUnder20(String text) {
		HashMap<String, String> map = new HashMap<>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
		t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void ttsGreater21(String text) {
		String utteranceId=this.hashCode() + "";
		t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		txtResponse = (TextView)findViewById(R.id.txtResponse);
		edtInput = (EditText)findViewById(R.id.edtInput);
		edtButton = (Button)findViewById(R.id.edtButton);
		//textService = initTextToSpeechService();


		// hide the action bar
		getActionBar().hide();


		t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {if (status == TextToSpeech.SUCCESS) {
				int result = t1.setLanguage(Locale.US);
				if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

				}
				//speak("Hello");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					ttsGreater21("Hello");
				} else {
					ttsUnder20("Hello");
				}

			} else {

			}
			}
		});



		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();

			}
		});

		edtButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String workspaceId = "a12e9223-d15b-4bb9-a774-47720827580d";
				service.setUsernameAndPassword("90099f72-f54c-4a4f-a344-b92becd9805e", "J2Z0AURpOe3s");
				newMessage = new MessageRequest.Builder()
						.inputText(edtInput.getText().toString())
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

							for(int i = 0; i< text.length();i++) {
								textResult = text.getString(i);
								System.out.println("THE TEXT IS: " + textResult);
							}
							txtResponse.setText(String.valueOf(textResult));

							RESPONSE = textResult.toString();




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
						// Actions to do after 1 seconds
						String utteranceId=this.hashCode() + "";
						t1.speak(String.valueOf(textResult), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
					}
				}, 1000);


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
						// Actions to do after 1 seconds
						txtResponse.setText(String.valueOf(textResult));
						String utteranceId=this.hashCode() + "";
						t1.speak(String.valueOf(textResult), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
					}
				}, 1000);

			}
			break;
		}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
