package com.example.grosure.ui.inside
// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.grosure.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class LocatorFragment : Fragment(), OnMapReadyCallback{
    lateinit var dataSet: MutableMap<String, LatLng>
    private lateinit var mMap: GoogleMap
    private lateinit var pins: MutableList<MarkerOptions>
    lateinit var userLocation: LatLng
    var locationPermissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        dataSet = mutableMapOf()
        pins = mutableListOf()
        val a  = AlertDialog.Builder(requireContext())
        a.setTitle("NOTE: THIS FEATURE IS JUST A PROOF OF CONCEPT")
        a.setMessage("There are numerous issues regarding the time required for loading of data from the API, hence only the first 50 stores in the API are loaded. \n\nPlease excuse the wait times.")
        a.show()

        // Construct a PlacesClient

//        // check if GPS enabled
//        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationClient.lastLocation.addOnSuccessListener {it->
//            userLocation = LatLng(it.latitude, it.longitude)
//
//        }



        mapFragment.getMapAsync(this)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })


        requireView().findViewById<EditText>(R.id.locatorSearch).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                mMap.clear()
                getLocationPermission()
                for (pins in pins) {
                    Log.i("count", "!")
                    if(pins.title.toLowerCase().contains(requireView().findViewById<EditText>(R.id.locatorSearch).text.toString().toLowerCase())) {
                        mMap.addMarker(pins.visible(true))
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                mMap.clear()
                getLocationPermission()
                for (pins in pins) {
                    Log.i("count", "!")
                    if(pins.title.toLowerCase().contains(requireView().findViewById<EditText>(R.id.locatorSearch).text.toString().toLowerCase())) {
                        mMap.addMarker(pins.visible(true))
                    }
                }
            }
        })


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


    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0!!

        // Add a marker in Sydney and move the camera
        userLocation = LatLng(1.287953, 103.851784)
        val url = "https://data.gov.sg/api/action/datastore_search?resource_id=df586152-d00f-4b15-b667-9e268f1b60df&limit=679"
        val sydney = LatLng(-34.0, 151.0)
        var cuf = CameraUpdateFactory.newLatLng(userLocation)
        mMap.setMinZoomPreference(11.1f)
        mMap.moveCamera(cuf)

        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String?> {
            override fun onResponse(response: String?) {
                try {

                    val jsonObject = JSONObject(response)
                    if (response != null) {
                        val storeListParent = jsonObject.getJSONObject("result")
                        val storeList = storeListParent.getJSONArray("records")

                        var geocode = Geocoder(requireContext())
                        for (t in 0 until 50-1){
                            Log.i("store", storeList.getJSONObject(t).toString())
                            lifecycleScope.launch{
                                var m = geocode.getFromLocationName(storeList.getJSONObject(t).getString("premise_address"), 1)
                                dataSet.put(storeList.getJSONObject(t).getString("business_name")+ "," +storeList.getJSONObject(t).getString("premise_address"), LatLng(m[0].latitude, m[0].longitude))
                            }

                        }
                    }


                    for (a in dataSet.keys){
                        var two = a.split(",")
                        var bruh = MarkerOptions().position(dataSet[a]!!).title(two[0]).snippet(two[1])
                        pins.add(bruh)
                        mMap.addMarker(bruh)
                    }


                }
                catch(e: Exception){
                    Log.i("WTF", "WTF12")
                }
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {

                Log.i("WTF", "WTF22")

            }
        })

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
//        stringRequest.retryPolicy = DefaultRetryPolicy(
//            45000,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(stringRequest)

        getLocationPermission()


    }


    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            mMap.isMyLocationEnabled = true
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
//            mMap.isMyLocationEnabled = true
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            mMap.isMyLocationEnabled = true
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))

            }


    }
}