package com.comp3617.finalProject.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.view.View
import androidx.core.app.ActivityCompat
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.MPlace
import com.comp3617.finalProject.ui.fragments.MapsFragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar

const val PERMISSION_REQUEST_LOCATION = 0

/**
 * Permission class. Handles Permission for MapFragment. Used only by MapFragment. Function to
 * start the Map is called here. Permission is needed to read the user's current location. And this
 * is only needed when user has no Blocks(no points of interest) to show on the Map. Only in this
 * case, will the Map get user's current location and show that as a pin on the Map.
 */
class Permission(val activity: Activity, val layout: View) {

    /**
     * The function to start the Map.
     */
    fun getCurrentLocation(googleMap: GoogleMap, mutableList: MutableList<LatLng?>, blocks: MutableList<Block>, places: MutableList<MPlace>) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            val client = LocationServices.getFusedLocationProviderClient(activity)
            client.lastLocation.addOnSuccessListener { it : Location? ->
                if (mutableList.isEmpty()) {
                    if (it != null) {
                        val l = LatLng(it.latitude, it.longitude)
                        mutableList.add(l)
                    }
                }
                MapsFragment().startMap(activity, googleMap, mutableList, blocks)
            }
        }
    }

    /**
     * Requests the [android.Manifest.permission.ACCESS_COARSE_LOCATION] permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private fun requestLocationPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            layout.showSnackbar(
                R.string.location_permission_required,
                Snackbar.LENGTH_INDEFINITE, R.string.ok) {

                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_LOCATION)
            }

        } else {
            layout.showSnackbar(R.string.location_permission_not_available, Snackbar.LENGTH_SHORT)
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_LOCATION)
        }
    }
}