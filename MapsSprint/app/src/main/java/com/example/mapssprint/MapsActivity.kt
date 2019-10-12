package com.example.mapssprint

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var mediaPlayer: MediaPlayer? = null
    companion object{
        private const val FINE_LOCATION_REQUEST_CODE = 5
        private const val MEDIA_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Annoying sound effect for when the marker gets dropped
        mediaPlayer = MediaPlayer.create(this, R.raw.nawnawnawnaw )


        //Request permission
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            FINE_LOCATION_REQUEST_CODE
        )
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Long press for Marker
        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(MarkerOptions().position(latLng))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mediaPlayer?.start() //Start when really it should be play
        }

        //Removes marker
        mMap.setOnMarkerClickListener { marker ->
            marker.remove() //Took me ages to work out I needed to return true
            true
        }


        //LONDON BABY
        val london = LatLng(51.509865, -0.118092)
        //sets marker position
        mMap.addMarker(MarkerOptions().position((london)))
        //moves camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(london))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //gets location
    fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            locationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    val location = LatLng(it.latitude, it.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

      /*  //User Location
       if(id == R.id.menu_location && item.isChecked ){

           getLocation()
           item.isChecked = true


       }*/

        //audio
        if (id == R.id.menu_audio){
            val audioIntent = Intent(Intent.ACTION_GET_CONTENT)
           audioIntent.type = "audio/*"
            startActivityForResult(audioIntent, MEDIA_REQUEST_CODE)
        }

        //add marker
        if(id == R.id.marker){
            mMap.addMarker(MarkerOptions().position(mMap.cameraPosition.target))
            //Plays the amazing sound
            mediaPlayer?.start()
        }

        return super.onOptionsItemSelected(item)

    }
}
