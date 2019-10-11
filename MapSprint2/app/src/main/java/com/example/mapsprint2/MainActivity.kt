package com.example.mapsprint2

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
class MainActivity : AppCompatActivity() {


    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.access_token))
        setContentView(R.layout.activity_main)
        //gives mapbox the context and api key



        mapView = findViewById(R.id.mapView)

        //Initializes the map view
        mapView.onCreate(savedInstanceState)

        mapView!!.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
            }
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
        if (outState!=null){
            mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}

