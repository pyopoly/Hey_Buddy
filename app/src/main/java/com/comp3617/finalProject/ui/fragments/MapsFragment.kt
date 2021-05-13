package com.comp3617.finalProject.ui.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.comp3617.finalProject.R
import com.comp3617.finalProject.models.Block
import com.comp3617.finalProject.models.MPlace
import com.comp3617.finalProject.repositories.Repository
import com.comp3617.finalProject.ui.MainActivity
import com.comp3617.finalProject.util.Permission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator

/**
 * MapFragment. Shows info of the Blocks on the page the user was at. If there are no Blocks to show
 * on the Map, the current location of the user is shown. Information of the Blocks and Points of
 * Interest are shown on in the infoWindow when markers are clicked.
 */
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val places = mutableListOf<MPlace>()
        val vm = (activity as MainActivity).mMainActivityViewModel
        val blocks = vm.mBlocks.value!! //list of all current blocks showing on the page
        val latLngList = vm.latLngList

        // Empty Blocks means there are no points of interest to show on the Map.
        // The Map will instead show the currentLocation of the user
        if (blocks.isEmpty()) {
            Permission(
                activity as MainActivity,
                (activity as MainActivity).findViewById(R.id.id_layout_main)
            )
                .getCurrentLocation(googleMap, latLngList, blocks, places)
        } else {
            places.clear()
            latLngList.clear()
            for (block in blocks) {
                val latitude = block.lat
                val longitude = block.lng
                if (latitude != null && longitude != null) {
                    val latLng = LatLng(latitude, longitude)
                    latLngList.add(latLng)
                } else {
                    latLngList.add(null)
                }
            }
            Permission(
                activity as MainActivity,
                (activity as MainActivity).findViewById(R.id.id_layout_main)
            )
                .getCurrentLocation(googleMap, latLngList, blocks, places)
        }

        googleMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(p0: Marker?): View? {
                return null
            }

            /**
             * sets up the Custom InfoWindow in order to show multiple lines of info
             */
            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(activity)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(activity)
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(activity)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        (activity as MainActivity).showBlocksInUpperPanel()
    }

    /**
     * Starts the Map. Drawing Icons, finding Places info.
     */
    fun startMap(
        activity: Context,
        googleMap: GoogleMap,
        latLngList: MutableList<LatLng?>,
        blocks: MutableList<Block>
    ) {
        val iconGen = IconGenerator(activity)
        iconGen.setBackground(ContextCompat.getDrawable(activity, R.drawable.map_icon))
        iconGen.setTextAppearance(R.style.mapIconTextStyle)

        Thread {
            //Starts building the GoogleMap
            val builder = LatLngBounds.Builder()
            for (i in 0 until latLngList.size) {
                val latLng: LatLng? = latLngList[i]
                if (latLng != null) {
                    // If there are Blocks, then we show points of interest on the map
                    if (blocks.isNotEmpty()) {
                        val title = blocks[i].title
                        val place = Repository.findPlaceByParentId(blocks[i].blockId)
                        val string =
                            place?.OPENING_HOURS?.substring(0, place.OPENING_HOURS.length - 1)
                                ?.replace(",", "\n")
                        val hours = if (string != null) "\n\n$string" else ""
                        val phone =
                            if (place?.PHONE_NUMBER != null) "\n${place.PHONE_NUMBER}" else ""
                        val ratings = if (place?.RATING != null) "\nRating: ${place.RATING}" else ""
                        val markerOptions = MarkerOptions().icon(
                            BitmapDescriptorFactory
                                .fromBitmap(iconGen.makeIcon(title))
                        )
                            .position(latLng).anchor(iconGen.anchorU, iconGen.anchorV)
                            .title(place?.NAME)
                            .snippet((place?.ADDRESS ?: "") + phone + ratings + hours)
                        (activity as MainActivity).runOnUiThread {
                            googleMap.addMarker(markerOptions).tag = i
                        }
                        builder.include(latLng)

                        // If there are no Blocks, we show the current location of the user. This
                        // is where Location Permission is needed.
                    } else {
                        (activity as MainActivity).runOnUiThread {
                        googleMap.addMarker(MarkerOptions().position(latLng).title("You are here"))
                            .showInfoWindow()
                        }
                        builder.include(latLng)
                    }
                }
            }
            val bounds = builder.build()

            //Zooms in on map differently depending on the marker number.
            // 1 marker vs. multiple markers
            val cu = if (latLngList.size < 2)
                CameraUpdateFactory.newLatLngZoom(latLngList[0], 15F)
            else
                CameraUpdateFactory.newLatLngBounds(bounds, 200)
            (activity as MainActivity).runOnUiThread {
                googleMap.setOnMarkerClickListener(this)
                googleMap.animateCamera(cu, 2000, null)
            }
        }.start()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        p0?.showInfoWindow()
        return true
    }
}