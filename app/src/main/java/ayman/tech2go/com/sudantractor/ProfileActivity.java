package ayman.tech2go.com.sudantractor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.*;

public class ProfileActivity extends AppCompatActivity {
    private int GALLERY = 1, CAMERA = 2;
    Bitmap FixBitmap;
    String result;
    String ImageTag = "image_name" ;

    String ImageName = "image_path" ;
    private RVArrayAdapter_Profile adapter;

    ProgressDialog progressDialog ;
    private static final String PREFER_NAME = "Auth";
    ByteArrayOutputStream byteArrayOutputStream ;

    byte[] byteArray ;

    String ConvertImage ;

    String GetImageNameFromEditText;

    HttpURLConnection httpURLConnection ;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter ;

    int RC ;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;

    boolean check = true;
    ImageView AVTimage;
    private RequestPermissionHandler mRequestPermissionHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final RecyclerView profileRecyclerView = (RecyclerView) findViewById(R.id.profile_list);
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));

        AVTimage = (ImageView) findViewById(R.id.awareness_image);
        mRequestPermissionHandler = new RequestPermissionHandler();
        byteArrayOutputStream = new ByteArrayOutputStream();



        AVTimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mRequestPermissionHandler.requestPermission(ProfileActivity.this, new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA
                    }, 123, new RequestPermissionHandler.RequestPermissionListener() {
                        @Override
                        public void onSuccess() {
                            showPictureDialog();
                            Toast.makeText(ProfileActivity.this, "request permission success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed() {
                            Toast.makeText(ProfileActivity.this, "request permission failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    showPictureDialog();
                }

                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file*//*");
                startActivityForResult(intent, 10);
                Log.e("ss", R.string.URL+"/upload-image-to-server.php");*/
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        final SharedPreferences sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);

        Runnable profileRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(getString(R.string.URL)+"/getuser.php");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    JSONObject jsonParam = new JSONObject();
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    //Create JSONObject here

                    jsonParam.put("phone",  sharedPreferences.getString("Phone",""));

                    // Send POST output.
                    DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
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




                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                JSONObject object = new JSONObject(result);
                                JSONArray postsArray = object.getJSONArray("User");
                                Toast.makeText(ProfileActivity.this, "the data : "+object.getJSONArray("User"), Toast.LENGTH_LONG).show();
                                List<Information> inf = new ArrayList<>();


                                    JSONObject currentObject = postsArray.getJSONObject(0);
                                    String id = currentObject.getString("id");
                                    String name = currentObject.getString("name");
                                    String phone = currentObject.getString("phone");

                                    Information userInfo = new Information(Integer.parseInt(id), name, phone);
                                    inf.add(userInfo);
                                    // Toast.makeText(getBaseContext(),""+posts.get(i),Toast.LENGTH_LONG).show();
                                    adapter = new RVArrayAdapter_Profile(ProfileActivity.this,inf);
                                    profileRecyclerView.setAdapter(adapter);
                                    Toast.makeText(getBaseContext(),adapter.toString(),Toast.LENGTH_LONG).show();




                            } catch (Exception e) {

                            }

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        final Thread thread = new Thread(profileRunnable);
        thread.start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            /*if (requestCode == CAMERA) {
                try {

//                FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    FixBitmap = (Bitmap) data.getExtras().get("data");

                    byteArray = byteArrayOutputStream.toByteArray();

                    ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }catch (OutOfMemoryError error){
                    error.printStackTrace();

                }
            }
            else{*/
//            Log.e("File Path :", filePath);
                Uri uri = data.getData();
                Toast.makeText(this, "URI : "+ uri, Toast.LENGTH_LONG).show();
                String filePath = getPathfromURI(uri);
                hendiwareFileUpload(filePath);}
//        }
    }

    private void hendiwareFileUpload(final String filePath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection uploadConnection = null;
                DataOutputStream outputStream;
                String boundary = "********";
                String CRLF = "\r\n";
                String Hyphens = "--";
                int bytesRead, bytesAvailable, bufferSize;
                int maxBufferSize = 1024 * 1024;
                byte[] buffer;
               /* FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

                byteArray = byteArrayOutputStream.toByteArray();*/

//                ConvertImage =getStringImage(FixBitmap);
                SharedPreferences sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
                File File ;
                FileInputStream fileInputStream;
                try {
                    if (filePath == null){
                        fileInputStream = new FileInputStream(ConvertImage);

                    }
                    else{
                        File = new File(filePath);
                        fileInputStream = new FileInputStream(File);
                    }
                    URL url = new URL(getString(R.string.URL)+"/upload-image-to-server.php");
                    uploadConnection = (HttpURLConnection) url.openConnection();
                    uploadConnection.setDoInput(true);
                    uploadConnection.setDoOutput(true);
                    uploadConnection.setRequestMethod("POST");
                    uploadConnection.setRequestProperty("Connection", "Keep-Alive");
                    uploadConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    uploadConnection.setRequestProperty("uploaded_file", sharedPreferences.getString("Phone",""));
                    outputStream = new DataOutputStream(uploadConnection.getOutputStream());
                    outputStream.writeBytes(Hyphens + boundary + CRLF);

                    outputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" +
                            sharedPreferences.getString("Phone","") + "\"" + CRLF);
                    outputStream.writeBytes(CRLF);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    outputStream.writeBytes(CRLF);
                    outputStream.writeBytes(Hyphens + boundary + Hyphens + CRLF);
                    InputStreamReader resultReader = new InputStreamReader(uploadConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(resultReader);
                    ;
                    String line = "";
                    String response = "";
                    while ((line = reader.readLine()) != null) {
                        response += line;
                    }

                    final String finalResponse = response;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(),finalResponse,Toast.LENGTH_LONG).show();
                        }
                    });

                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }


    public String getPathfromURI(Uri uri) {
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//                    ShowSelectedImage.setImageBitmap(FixBitmap);
//                    UploadImageOnServerButton.setVisibility(View.VISIBLE);
//                        UploadImageToServer();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
//            UploadImageToServer();
//            ShowSelectedImage.setImageBitmap(FixBitmap);
                AVTimage.setImageBitmap(FixBitmap);
//            UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //  saveImage(thumbnail);
            Toast.makeText(ProfileActivity.this, "Image Data byte Size : " + FixBitmap.getByteCount(), Toast.LENGTH_SHORT).show();
        }

//        UploadImageToServer();
    }

    public void UploadImageToServer(){
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(ProfileActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(ProfileActivity.this,string1,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);

                HashMapParams.put(ImageName, ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(R.string.URL+"/upload-image-to-server.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(ProfileActivity.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }
        }
    }
}
