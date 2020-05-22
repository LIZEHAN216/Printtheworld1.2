package fr.insalyon.painttheworldapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.insalyon.painttheworldapp.util.PermissionUtils;

public class Login extends AppCompatActivity {

    private Button Button_confirmer;
    private Button Button_inscription;
    private EditText Email;
    private EditText Password;
    private CheckBox Rembme;
    File ext = Environment.getExternalStorageDirectory();
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button_confirmer = (Button) findViewById(R.id.confirmer);
        Button_inscription = (Button) findViewById(R.id.inscription);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Rembme = findViewById(R.id.rembme);

        loadinfo();



        Button_confirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Mot de passe ou email est vide", Toast.LENGTH_SHORT).show();
                    //return;
                } else if (!Rembme.isChecked()) {
                    file = new File(ext, "user.txt");
                }
//                if(!isEmail(Email.getText().toString())) {
//                    Toast.makeText(Login.this, "Votre email est non correct.", Toast.LENGTH_SHORT).show();
                 if(!isPassword(Password.getText().toString())){
                    Toast.makeText(Login.this, "Votre password < 5 bytes",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent();
                    intent.setClass(Login.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });


        Button_inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(Login.this, Inscription.class);

                startActivity(intent);
            }
        });

    }

    private void loadinfo(){
        File file = new File(ext,"user.txt");
        if(!file.exists()){
            return;
        }
        try{
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String text = br.readLine();
            String[] arr = text.split("#");
            Email.setText(arr[4]);
            Password.setText(arr[1]);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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


}
