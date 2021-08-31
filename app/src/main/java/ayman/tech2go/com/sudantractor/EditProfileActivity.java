package ayman.tech2go.com.sudantractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static ayman.tech2go.com.sudantractor.RegisterationActivity.isValidPhone;

public class EditProfileActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private RadioButton radioButtonAdmin;
    private int adminval;
    private SharedPreferences.Editor editor;
    String result;
    String url;
    DataOutputStream printout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        RadioGroup RDGroup = (RadioGroup) findViewById(R.id.rd_group);
        radioButtonAdmin = (RadioButton) RDGroup.findViewById(R.id.admin);
        final Button Sumbitbtn = (Button) findViewById(R.id.submit);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Auth", 0);
        // get editor to edit in file
        editor = sharedPreferences.edit();
        editTextPhone.setText(sharedPreferences.getString("Phone",""));
        radioButtonAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    adminval = 1;
                }
                else{
                    adminval = 0;
                }
            }
        });


        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!isValidPhone(editTextPassword.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "please enter valid password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

  Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(getString(R.string.URL)+"/updateuser.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    JSONObject jsonParam = new JSONObject();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    //Create JSONObject here

                    jsonParam.put("name", editTextName.getText());
                    jsonParam.put("password", editTextPassword.getText());
                    jsonParam.put("phone", editTextPhone.getText());
                    jsonParam.put("admin", adminval);
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
                                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
//                                intent.putExtra("","");
                                editor.putString("txtPassword",editTextPassword.getText().toString());
                                editor.commit();
                                startActivity(intent);
                                finish();
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
  final Thread thread = new Thread(runnable);
        Sumbitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().equals("") || editTextPassword.getText().toString().equals("")
                        || editTextPhone.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
//                    editor.putString("Phone", editTextPhone.getText().toString());
//                    editor.putString("txtPassword",editTextPassword.getText().toString());
//                    editor.commit();
                    thread.start();}
            }
        });
    }
}
