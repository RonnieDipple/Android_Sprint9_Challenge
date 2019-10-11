package com.example.mapsprint2

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import androidx.annotation.NonNull
import com.mapbox.mapboxsdk.maps.Style.OnStyleLoaded
import com.mapbox.mapboxsdk.maps.Style.MAPBOX_STREETS
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.Style


/*You have *3 hours*, and you should work *independently* — looking things up (search, notes) is all fair game. And questions about *process* / *logistics* (i.e. if you have a hard time opening/saving to GitHub) are fair game too.

Good luck!

## Screen Recording

There is a screen recording in this repo which previews the look and feel of a completed project that meets the MVP requirements for this project.

This is an example showing how two views can look different when using different parameters.

## Requirements

Build a Map activity that has actions in the action bar to center the map on the user's current location and one to place a pin in the center of the screen. When a pin is placed, an audio effect will be played.

* Use toolbar actions to allow for adding pins and centering on the user
* Use a mediaplayer to play an audio track when pin is dropped (this could be from a resource directory, the file system, or from the internet)

## Stretch Goals
* Allow a user to set the audio effect and save that persistently
* Allow a user to remove each pin
* Allow a user to set a pin that's not in the center (long press for pin)
* Any other polish features that you'd like to add
*/
class MainActivity : AppCompatActivity(), PermissionsListener,
    LocationEngineCallback<LocationEngineResult> {

    companion object{
        const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
        const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    }


    private lateinit var mapView: MapView

    private lateinit var map: MapboxMap //The entry point for all methods related to the map view

    //THIS IS SUPER HELPFUL, it removes a lot of boilerplate code for permissions
    private lateinit var permissionManager: PermissionsManager

    //Where we are storing our current location
    private lateinit var originLocation: Location

    //Location engine is the component that gives the user location
    private lateinit var locationEngine: LocationEngine

    //works with the location engine to give a ui display of what the location engine is seeing
    // private var locationLayerPlugin: LocationLayerPlugin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //This almost ruined my day
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))
        setContentView(R.layout.activity_main)
        //gives mapbox the context and api key

        mapView = findViewById(R.id.mapView)

        //Initializes the map view
        mapView.onCreate(savedInstanceState)

        mapView!!.getMapAsync { mapboxMap ->
            map = mapboxMap

            //Here you can change the style of the map, I like dark
            mapboxMap.setStyle(Style.SATELLITE) {
                enableLocation(it)
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
            }
        }


    }

    @SuppressLint("MissingPermission")
    fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)

        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()


        locationEngine.requestLocationUpdates(request, this, mainLooper)

       val lastLocation = locationEngine.getLastLocation(this)


    }

    private fun setCameraPosition(location: Location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 13.0))

    }

    @SuppressLint("MissingPermission")
    fun enableLocation(loadedMapStyle: Style){

        // Check if permissions are enabled and if not request
        if(PermissionsManager.areLocationPermissionsGranted(this)){

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.greenApp))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // Get an instance of the LocationComponent and then adjust its settings
            map.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }


        } else {

            permissionManager = PermissionsManager(this)
           permissionManager.requestLocationPermissions(this)

        }
    }


    //Mapview contains it's on lifecycle methods for managing androids lifecycle so the life cycle methods must be overridden

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    //From permissionslistener
    //called when user denies permission the first time and the second time you could present a toast or dialog
    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocation(map.style!!)
        } else {
            Toast.makeText(this, "User Location Permission Not Granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

