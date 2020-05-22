package fr.insalyon.painttheworldapp.Navigation_drawer;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.nkzawa.socketio.client.Socket;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Locale;

import fr.insalyon.painttheworldapp.BuildConfig;
import fr.insalyon.painttheworldapp.R;

public class Fragment_first extends Fragment implements IMyLocationConsumer {
    /**Elements généraux */
    private Context context;
    private Fragment_first MA;
    private String playerName = "RootUser42";
    private int playerLevel= 420;
    private boolean premium=false;


    /**Elements d'interface */
    private Button bBlue, bRed, bPlus, bMinus, bGreen, bCyan, bMagenta, bYellow, bBlack, bWhite, bEraseAll, bSend;
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

    /** Elements du serveur */
    private Socket mSocket;
    public static final String SERVER_URL = "https://paint.antoine-rcbs.ovh:443";



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_first, container, false);
        context = root.getContext();
        locationConsumer = this;
        MA = this;
        //Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mMapView = root.findViewById(R.id.map);
        mMapView.setOnTouchListener((v, event) -> true);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        mMapView.setMultiTouchControls(false);
        final IMapController mapController = mMapView.getController();
        mapController.setZoom((double) zoom);
        GeoPoint startPoint = new GeoPoint(45.7837763, 4.872973);
        mapController.setCenter(startPoint);
        mLocationProvider = new GpsMyLocationProvider(context);
        mLocationProvider.startLocationProvider(locationConsumer);
        mLocationProvider.setLocationUpdateMinDistance(10);
        mLocationProvider.setLocationUpdateMinTime(5000);
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();
        mMapView.getOverlays().add(mLocationOverlay);
        mMapView.setMaxZoomLevel(20.0);
        mMapView.setMinZoomLevel(15.0);

        //Instanciation du service de localisation

        LinearLayout linear = root.findViewById(R.id.layoutCanvas);
        canvas = new MyCanvas(context, MA);

        linear.addView(canvas);
        iCircle = root.findViewById(R.id.imageCircle);
        updateRatio();
        System.out.println("height" + iCircle.getHeight());
        sPremium = root.findViewById(R.id.switchPremium);
        sPremium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    premium = true;
                    sPremium.setText(R.string.premium);
                } else {
                    premium = false;
                    sPremium.setText(R.string.regular);
                }
            }
        });
        sRed = root.findViewById(R.id.seekRed);
        sRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeColor(Color.rgb(sRed.getProgress(), sGreen.getProgress(), sBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sGreen = root.findViewById(R.id.seekGreen);
        sGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeColor(Color.rgb(sRed.getProgress(), sGreen.getProgress(), sBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sBlue = root.findViewById(R.id.seekBlue);
        sBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeColor(Color.rgb(sRed.getProgress(), sGreen.getProgress(), sBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sThickness = root.findViewById(R.id.seekThickness);
        sThickness.setMax(100 + playerLevel * 5);
        sThickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tThickness.setText(getString(R.string.width, String.format(Locale.getDefault(), "%d", sThickness.getProgress())));
                canvas.setThickness(sThickness.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sRed.getProgressDrawable().setColorFilter(new BlendModeColorFilter(Color.RED, BlendMode.SRC_ATOP));
            sRed.getThumb().setColorFilter(new BlendModeColorFilter(Color.RED, BlendMode.SRC_ATOP));
            sGreen.getProgressDrawable().setColorFilter(new BlendModeColorFilter(Color.GREEN, BlendMode.SRC_ATOP));
            sGreen.getThumb().setColorFilter(new BlendModeColorFilter(Color.GREEN, BlendMode.SRC_ATOP));
            sBlue.getProgressDrawable().setColorFilter(new BlendModeColorFilter(Color.BLUE, BlendMode.SRC_ATOP));
            sBlue.getThumb().setColorFilter(new BlendModeColorFilter(Color.BLUE, BlendMode.SRC_ATOP));
            sThickness.getProgressDrawable().setColorFilter(new BlendModeColorFilter(Color.BLACK, BlendMode.SRC_ATOP));
            sThickness.getThumb().setColorFilter(new BlendModeColorFilter(Color.BLACK, BlendMode.SRC_ATOP));
        } else {
            sRed.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            sRed.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            sGreen.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            sGreen.getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            sBlue.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
            sBlue.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
            sThickness.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
            sThickness.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        }
        tZoom = root.findViewById(R.id.textZoom);
        tColor = root.findViewById(R.id.textColor);
        tThickness = root.findViewById(R.id.textThickness);
        bPlus = root.findViewById(R.id.buttonPlus);
        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapView.canZoomIn()) {
                    zoom += 1;
                    mapController.setZoom((double) zoom);
                    updateRatio();
                    canvas.eraseAll();
                }
                tZoom.setText(String.format(Locale.getDefault(), "%d", zoom));
            }
        });
        bMinus = root.findViewById(R.id.buttonMinus);
        bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapView.canZoomOut()) {
                    zoom -= 1;
                    mapController.setZoom((double) zoom);
                    updateRatio();
                    canvas.eraseAll();
                }
                tZoom.setText(String.format(Locale.getDefault(), "%d", zoom));
            }
        });
        bSend = root.findViewById(R.id.buttonSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapDrawing drawing = new MapDrawing(canvas.getMapDrawingLines(), playerName, premium);
                mSocket.emit("new_drawing", drawing.toJSON());
                canvas.eraseAll();
            }
        });
        bUndo = root.findViewById(R.id.buttonUndo);
        bUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canvas.undo()) {
                    ActivateZoomButton(true);
                }
            }
        });

        bRedo = root.findViewById(R.id.buttonRedo);
        bRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canvas.redo()) {
                    ActivateZoomButton(false);
                }
            }
        });
        bEraseAll = root.findViewById(R.id.buttonEraseAll);
        bEraseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
                newDialog.setMessage("Effacer tout ?");
                newDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        canvas.eraseAll();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
            }
        });
        bBlue = root.findViewById(R.id.buttonBlue);
        bBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.BLUE);
            }
        });
        bRed = root.findViewById(R.id.buttonRed);
        bRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.RED);
            }
        });
        bGreen = root.findViewById(R.id.buttonGreen);
        bGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.GREEN);
            }
        });
        bCyan = root.findViewById(R.id.buttonCyan);
        bCyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.CYAN);
            }
        });
        bMagenta = root.findViewById(R.id.buttonMagenta);
        bMagenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.MAGENTA);
            }
        });
        bYellow = root.findViewById(R.id.buttonYellow);
        bYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.YELLOW);
            }
        });
        bBlack = root.findViewById(R.id.buttonBlack);
        bBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.BLACK);
            }
        });
        bWhite = root.findViewById(R.id.buttonWhite);
        bWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonColor(Color.WHITE);
            }
        });
        return root;
    }


    protected void changeColor(int col){
        tColor.setBackgroundColor(col);
        tColor.setText(Integer.toHexString(col));
        tColor.setTextColor(Color.rgb(255-Color.red(col),255-Color.green(col),255-Color.blue(col)));
        canvas.setColor(col);
    }
    protected void buttonColor(int col){
        sBlue.setProgress(Color.blue(col));
        sGreen.setProgress(Color.green(col));
        sRed.setProgress(Color.red(col));
        changeColor(col);
    }

//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (backAvailable){
//            AlertDialog.Builder alertReturn = new AlertDialog.Builder(context);
//            alertReturn.setMessage("Retourner au menu principal ?");
//            alertReturn.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int which){
//                    recreate();
//                }
//            });
//            alertReturn.setNegativeButton("Non", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            alertReturn.show();
//            return true;
//        }else{
//            AlertDialog.Builder alertExit = new AlertDialog.Builder(context);
//            alertExit.setMessage("Quitter l'application?");
//            alertExit.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int which){
//                    finish();
//                }
//            });
//            alertExit.setNegativeButton("Non", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            alertExit.show();
//        }
//        return false;
//    }



@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home){
//            AlertDialog.Builder newDialog = new AlertDialog.Builder(context);
//            newDialog.setMessage("Retourner au menu principal ?");
//            newDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
//                public void onClick(DialogInterface dialog, int which){
//                    root.recreate();
//                }
//            });
//            newDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            newDialog.show();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void onLocationChanged(Location location, IMyLocationProvider source) {
    }


    public int getPlayerLevel(){
        return playerLevel;
    }

    public void updateRatio(){
        int i=20-zoom;
        ratio=(int)Math.pow(2, i);
        iCircle.getLayoutParams().height = radius /ratio*2;
        iCircle.getLayoutParams().width = radius /ratio*2;
        canvas.ratioChange(ratio);
        System.out.println("ratio : " + ratio);
    }

    public void displayUndoRedo(boolean boolUndo, boolean boolRedo){
        if (boolUndo) bUndo.setAlpha((float)1);
        else bUndo.setAlpha((float)0.2);
        if (boolRedo) bRedo.setAlpha((float)1);
        else bRedo.setAlpha((float)0.2);
    }

    public int getRadius(){
        return radius;
    }

    public void ActivateZoomButton(boolean bool){
        bPlus.setEnabled(bool);
        bMinus.setEnabled(bool);
    }

    public void ActivateOtherButton(boolean bool){
        bBlue.setEnabled(bool);
        bRed.setEnabled(bool);
        bGreen.setEnabled(bool);
        bCyan.setEnabled(bool);
        bMagenta.setEnabled(bool);
        bYellow.setEnabled(bool);
        bBlack.setEnabled(bool);
        bWhite.setEnabled(bool);
        bEraseAll.setEnabled(bool);
        bSend.setEnabled(bool);
        bUndo.setEnabled(bool);
        bRedo.setEnabled(bool);
        sRed.setEnabled(bool);
        sGreen.setEnabled(bool);
        sBlue.setEnabled(bool);
        sThickness.setEnabled(bool);
    }

    public MapView getMap(){
        return mMapView;
    }
}
