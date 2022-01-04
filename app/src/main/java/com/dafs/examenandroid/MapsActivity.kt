package com.dafs.examenandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.dafs.examenandroid.adapters.LocationsAdapter

import com.dafs.examenandroid.databinding.ActivityMapsBinding
import com.dafs.examenandroid.models.ELocations
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        val db = Firebase.firestore
        val builder = LatLngBounds.Builder()
        val myMarkers = java.util.ArrayList<Marker>()


        db.collection("location")
            .get()
            .addOnSuccessListener { result ->

                if(result.size()>0){



                for (document in result) {

                    val date:String=document.get("fecha").toString()
                    val latitud:String=document.get("latitud").toString()
                    val longitud:String=document.get("longitud").toString()

                    val locations =
                        LatLng(latitud.toDouble(), longitud.toDouble())
                    val marker = googleMap.addMarker(
                        MarkerOptions().position(locations)
                            .title(date)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )

                    if (marker != null) {
                        myMarkers.add(marker)
                    }

                    Log.d("LocationsSearch", "${document.id} => ${document.data}")
                    Log.d("LocationsSearch", "$date $latitud $longitud")

                }

                for (marker in myMarkers) {
                    builder.include(marker.position)
                }

                val bounds = builder.build()
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 50)
                mMap.animateCamera(cameraUpdate)
                }
                else{
                    CustomDialogFragment("No existe ninguna locación en la base de Datos de Firebase").show(supportFragmentManager,"customDialog");

                }

            }
            .addOnFailureListener { exception ->
                CustomDialogFragment(exception.toString()).show(supportFragmentManager,"customDialog");
                Log.w("LocationsSearch", "Error getting documents.", exception)
            }


    }





}