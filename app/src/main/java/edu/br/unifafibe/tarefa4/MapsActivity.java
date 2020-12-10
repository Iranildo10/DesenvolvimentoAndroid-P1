package edu.br.unifafibe.tarefa4;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;

    String nome;

    Double lat;

    Double lgt;

    private String CHANNEL_ID = "meuCanal1";

    private SensorManager sensorManager;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        Bundle b = getIntent().getExtras();
        if(b != null){

             nome = (String) b.get("Website");

             lat = (Double) b.get("Lat");

             lgt = (Double) b.get("Long");
        }

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lat, lgt);

        mMap.addMarker(new MarkerOptions().position(sydney).title(nome));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //***** ABRIR A CAMERA
                dispatchTakePictureIntent();

                //**** LANÇAR A NOTIFICAÇÃO
                lancarNotificacao(nome);

                return false;
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {

            float[] values = event.values;

            float x = values[0];

            float y = values[1];

            float z = values[2];

            double acel = Math.sqrt(x*x+y*y+z*z);

        if(acel > 50){

            Intent it = new Intent(MapsActivity.this, MainActivity.class);

            it.putExtra("Website", nome);

            startActivity(it);

        }

    }

    public void lancarNotificacao(String atributo){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MapsActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("P1_Android")
                .setContentText("Atributo: " + atributo);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MapsActivity.this);


        notificationManager.notify(25, builder.build());

    }


}
