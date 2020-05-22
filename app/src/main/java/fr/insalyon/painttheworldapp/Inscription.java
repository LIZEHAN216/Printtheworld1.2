package fr.insalyon.painttheworldapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import fr.insalyon.painttheworldapp.util.HttpUtils;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.insalyon.painttheworldapp.util.PermissionUtils;

public class Inscription extends AppCompatActivity {
    private EditText Email,Password,Nom,Prenom,Username;
    private Button Inscription;
    File ext = Environment.getExternalStorageDirectory();
    private static String baseURL = "https://paint.antoine-rcbs.ovh/";
    private static URL url;
    public Inscription(){}

//    static{
//        try {
//            url = new URL(PATH);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Nom = findViewById(R.id.prenom);
        Prenom = findViewById(R.id.nom);
        Inscription = findViewById(R.id.inscription);
        Username  =findViewById(R.id.username);

        Inscription.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                try {
                    String email = Email.getText().toString().trim();
                    String password = Password.getText().toString().trim();
                    String nom = Nom.getText().toString().trim();
                    String prenom = Prenom.getText().toString().trim();
                    String username = Username.getText().toString().trim();
                    File file = new File(ext, "user.txt");
                    Map<String, String> data =  new HashMap<String, String>();
                    PermissionUtils.verify(Inscription.this);
                    OutputStream out = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                    BufferedWriter writer = new BufferedWriter(osw);
                    writer.write(email + "#" + password + "#" + nom + "#" + prenom +
                            "#" + username);
                    writer.flush();
                    writer.close();

                    Map<String,String> params = new HashMap<String, String>();
                    params.put("name", nom);
                    params.put("p_nom", prenom);
                    params.put("email", email);
                    params.put("username", username);
                    params.put("password1", password);
                    params.put("password2", password);

                    String strResult=HttpUtils.submitPostData(baseURL,params, "utf-8");

                } catch (Exception e) {
                    e.printStackTrace();
                }

//                if (!isEmail(Email.getText().toString())) {
//                    Toast.makeText(Inscription.this, "Votre email est non correct.", Toast.LENGTH_SHORT).show();
                if (!isPassword(Password.getText().toString())) {
                    Toast.makeText(Inscription.this, "Votre password < 5 bytes", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(Inscription.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean isPassword(String password){
        if(password.length()<5){
            return false;
        }else{
            return true;
        }
    }

//    public static String sendPostMessage(Map<String, String> params,String email, String password,String
//            nom,String prenom, String username,String encode){
//        params.put("name", nom);
//        params.put("p_nom", prenom);
//        params.put("email", email);
//        params.put("username", username);
//        params.put("password1", password);
//        params.put("password2", password);
//        StringBuffer buffer = new StringBuffer();
//        try {
//            if(params != null&&!params.isEmpty()){
//                for(Map.Entry<String, String> entry : params.entrySet()){
//                    buffer.append(entry.getKey()).append("=").
//                            append(URLEncoder.encode(entry.getValue(),encode)).
//                            append("&");
//                }
//            }
//
//            buffer.deleteCharAt(buffer.length()-1);
//            byte[] mydata = buffer.toString().getBytes();
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setConnectTimeout(3000);
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//
//            connection.setRequestMethod("POST");
//            connection.setUseCaches(false);
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//            connection.setRequestProperty("Content-Length", String.valueOf(mydata.length));
//            connection.connect();
//
//            OutputStream outputStream = connection.getOutputStream();
//            outputStream.write(mydata,0,mydata.length);
//
//            int responseCode = connection.getResponseCode();
//            if(responseCode == HttpURLConnection.HTTP_OK){
//                return changeInputeStream(connection.getInputStream(),encode);
//
//            }
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return "";
//    }
//    private static String changeInputeStream(InputStream inputStream, String encode) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] data = new byte[1024];
//        int len = 0;
//        String result = "";
//        if(inputStream != null){
//            try {
//                while((len = inputStream.read(data))!=-1){
//                    data.toString();
//
//                    outputStream.write(data, 0, len);
//                }
//                result = new String(outputStream.toByteArray(),encode);
//                outputStream.flush();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }
}
