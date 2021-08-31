package ayman.tech2go.com.sudantractor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
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
import java.util.Random;

public class RegisterationActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextPhone;
    private RadioButton radioButtonAdmin;
    private static final String PREFER_NAME = "Auth";
    private int adminval;
    String result;
    private SharedPreferences.Editor editor;
    String url;
    DataOutputStream printout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        final EditText editTextName = (EditText) findViewById(R.id.editTextName);
        final EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        final EditText editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        RadioGroup RDGroup = (RadioGroup) findViewById(R.id.rd_group);
        radioButtonAdmin = (RadioButton) RDGroup.findViewById(R.id.admin);
        final Button Sumbitbtn = (Button) findViewById(R.id.submit);
        SharedPreferences sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
//        Toast.makeText(this,sharedPreferences.getAll().toString(),Toast.LENGTH_LONG).show();
        if (sharedPreferences.contains("Phone")&& sharedPreferences.contains("txtPassword")) {
            Intent intent = new Intent(RegisterationActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this,sharedPreferences.getAll().toString(),Toast.LENGTH_LONG).show();
        }
        else{
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
            Random r = new Random();
            final int i1 = r.nextInt(80 - 65) + 20;
            final int i2 = r.nextInt(80 - 65) + 50;
            final int i3 = r.nextInt(80 - 65) + 70;

            final int randonno = Integer.parseInt(""+i1+i2+i3);

            sharedPreferences = getApplicationContext().getSharedPreferences("Auth", 0);
            // get editor to edit in file
            editor = sharedPreferences.edit();
            Runnable smsRunnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        URL url = new URL(getString(R.string.SMSURL)+"username=Tech 2 Go&password=123123&text="
                                +getString(R.string.SMSbody)+randonno+
                                "&numbers=249"+editTextPhone.getText()+"&sender=Tech 2 Go");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(inputStream);
                        final StringBuilder txtBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            txtBuilder.append(line);
                        }
                        result = txtBuilder.toString();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {



                                String writer = null;
                                try {
//                                        JSONArray arr = new JSONArray(result);
                                    JSONObject jObj = new JSONObject(result);
//                                        Toast.makeText(getContext(), "the data : "+result, Toast.LENGTH_LONG).show();
                                    boolean status = jObj.getBoolean("success");
                                    if (!status) {
                                        Toast.makeText(RegisterationActivity.this, "sorry your message did not send !!", Toast.LENGTH_SHORT).show();
//                                    numberEditText.setText(result);
                                    }else{
                                        Toast.makeText(RegisterationActivity.this, "congrats your message has sent !!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterationActivity.this,SMStest.class);
                                        intent.putExtra("name",editTextName.getText().toString());
                                        intent.putExtra("phone",editTextPhone.getText().toString());
                                        intent.putExtra("password",editTextPassword.getText().toString());
                                        intent.putExtra("admin",adminval);
                                        intent.putExtra("var_code",String.valueOf(randonno));
                                        startActivity(intent);}
                                        finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            };

            final Thread smsThread = new Thread(smsRunnable);
            Sumbitbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editTextName.getText().toString().equals("") || editTextPassword.getText().toString().equals("")
                            || editTextPhone.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "please fill all the fields", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(),"The generated random number : "+ randonno,Toast.LENGTH_LONG).show();
                        editor.putString("Phone", editTextPhone.getText().toString());
                        editor.putString("txtPassword",editTextPassword.getText().toString());
                        editor.commit();
                        smsThread.start();}
                }
            });}
    }

    public final static boolean isValidPhone(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
    }
}
