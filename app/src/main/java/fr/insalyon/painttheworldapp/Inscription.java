package fr.insalyon.painttheworldapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.insalyon.painttheworldapp.util.PermissionUtils;

public class Inscription extends AppCompatActivity {
    private EditText Email,Password,Nom,Prenom;
    private Button Inscription;
    File ext = Environment.getExternalStorageDirectory();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Nom = findViewById(R.id.prenom);
        Prenom = findViewById(R.id.nom);
        Inscription = findViewById(R.id.inscription);

        Inscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                String nom = Nom.getText().toString().trim();
                String prenom = Prenom.getText().toString().trim();
                File file = new File(ext, "user.txt");
                try {
                    PermissionUtils.verify(Inscription.this);
                    OutputStream out = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                    BufferedWriter writer = new BufferedWriter(osw);
                    writer.write(email + "#" + password + "#" + nom + "#" + prenom);
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!isEmail(Email.getText().toString())) {
                    Toast.makeText(Inscription.this, "Votre email est non correct.", Toast.LENGTH_SHORT).show();
                } else if (!isPassword(Password.getText().toString())) {
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
}
