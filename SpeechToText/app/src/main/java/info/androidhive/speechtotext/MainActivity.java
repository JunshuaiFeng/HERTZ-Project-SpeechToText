package info.androidhive.speechtotext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends Activity {

	private TextView txtSpeechInput, txtResponse;
	private ImageButton btnSpeak;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	//private TextView txtMood;

	ConversationService service = new ConversationService("2016-09-20");
	MessageRequest newMessage;
	MessageResponse response;

	String textResult;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		txtResponse = (TextView)findViewById(R.id.txtResponse);

		// hide the action bar
		getActionBar().hide();





		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();
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

							int i = 0;
							if(text.getString(i)==""){
								i=1;
							}
								textResult = text.getString(i);
								System.out.println("THE TEXT IS: "+textResult);


							//System.out.println(textResult);


						}catch(Exception ex){

							}
						}



					@Override
					public void onFailure(Exception e) {

					}


			});

				txtResponse.setText(textResult);

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
