package fr.insalyon.painttheworldapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import fr.insalyon.painttheworldapp.Navigation_drawer.Fragment_first;
import fr.insalyon.painttheworldapp.Navigation_drawer.MyCanvas;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements IMyLocationConsumer {

    /**Elements généraux */
    private Context context;
    private MainActivity MA;
    private String playerName = "RootUser42";
    private int playerLevel= 420;
    private boolean premium=false;


    /**Elements d'interface */
    private Button bPlay, bActivate, bBlue, bRed, bPlus, bMinus, bGreen, bCyan, bMagenta, bYellow, bBlack, bWhite, bEraseAll, bSend;
    private ImageButton bUndo, bRedo;
    private SeekBar sRed, sGreen, sBlue, sThickness;
    private Switch sPremium;
    private TextView tColor, tThickness, tZoom;

    /**Elements géographiques*/
    private MapView mMapView;
    private ImageView iCircle;
    private MyLocationNewOverlay mLocationOverlay;
    private GpsMyLocationProvider mLocationProvider;
    private IMyLocationConsumer locationConsumer;
    private int zoom=20;

    private int ratio=1;
    private int radius = (500+playerLevel*10)/ratio;

    private boolean backAvailable =false;
    private MyCanvas canvas;

    /** Elements du dessin */
    private ArrayList<ArrayList<IGeoPoint>> geoDrawingLines = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> linesInfos =new ArrayList<>();

    /** Elements du serveur */
    private Socket mSocket;
    public static final String SERVER_URL = "https://paint.antoine-rcbs.ovh:443";



    @Override
        protected void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            context = this;
            locationConsumer = this;
            MA = this;
            //Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
            Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
            setContentView(R.layout.activity_main);

            //Instanciation du socket avec le serveur node.js
            try {
                mSocket = IO.socket(SERVER_URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            mSocket.connect();
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(backAvailable);
            bPlay = findViewById(R.id.buttonPlay);
            bActivate = findViewById(R.id.buttonActivate);
            bPlay.setEnabled(false);
            context =this;
            bActivate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bPlay.setEnabled(true);
                    System.out.println("bactivate");
                }
            });

            bPlay.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onClick(View v) {
                    System.out.println("bPlay");
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, map.class);
                    startActivity(intent);
                }
            });

    }


    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {

    }
}
