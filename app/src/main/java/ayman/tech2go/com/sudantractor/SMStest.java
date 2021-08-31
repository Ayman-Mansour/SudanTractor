package ayman.tech2go.com.sudantractor;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SMStest extends AppCompatActivity {
    private static final int RESOLVE_HINT = 1000;
    private GoogleApiClient apiClient;
    String result;
    String url;
    DataOutputStream printout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smstest);
        final EditText numberEditText = (EditText) findViewById(R.id.var_code);
        Button sendButton = (Button) findViewById(R.id.send_button);
        final TextView resultTextView = (TextView) findViewById(R.id.result_text);

        final Bundle extras =getIntent().getExtras();
        final String varCode = extras.getString("var_code");
        final Runnable registerRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(getString(R.string.URL)+"/register.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    JSONObject jsonParam = new JSONObject();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    //Create JSONObject here

                    jsonParam.put("name", extras.getString("name"));
                    jsonParam.put("password", extras.getString("password"));
                    jsonParam.put("phone", extras.getString("phone"));
                    jsonParam.put("admin", extras.getInt("admin"));
                    // Send POST output.
                    printout = new DataOutputStream(urlConnection.getOutputStream());
                    printout.writeBytes(jsonParam.toString());
                    printout.flush();
                    printout.close();

                    InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(inputStream);
                    final StringBuilder txtBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        txtBuilder.append(line);
                    }
                    result = txtBuilder.toString();

                    final JSONObject jObj = new JSONObject(result);
                    final int status = jObj.getInt("status");


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (status == 1) {
                                try {
                                    Toast.makeText(getBaseContext(), jObj.getString("massage").toString(), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if ( numberEditText.getText().toString().equals(varCode)) {

                                    Intent intent = new Intent(SMStest.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else
                                {
                                    Toast.makeText(getBaseContext(), "Wrong verification code !!", Toast.LENGTH_LONG).show();

                                }
                            } if(status == 2){
                                try {
                                    Toast.makeText(getBaseContext(), jObj.getString("massage").toString(), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            else {
                                try {
                                    Toast.makeText(getBaseContext(), jObj.getString("massage").toString(), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            Toast.makeText(getBaseContext(), "The status is " + status, Toast.LENGTH_LONG).show();

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        final Thread registerThread = new Thread(registerRunnable);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getBaseContext(),"The generated random number : "+ randonno,Toast.LENGTH_LONG).show();
                registerThread.start();
            }
        });

    }
}

