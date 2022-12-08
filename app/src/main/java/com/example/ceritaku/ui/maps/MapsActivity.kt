package com.example.ceritaku.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.ceritaku.R
import com.example.ceritaku.data.Result
import com.example.ceritaku.databinding.ActivityMapsBinding
import com.example.ceritaku.factory.ViewModelFactory
import com.example.ceritaku.model.Story
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var viewModelFactory: ViewModelFactory
    private val mapsViewModel: MapsViewModel by viewModels { viewModelFactory }
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMapFragment()
        setViewModel()
        setActionBar()
    }

    private fun setViewModel(){
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun setMapFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setActionBar(){
        supportActionBar?.title = "Maps"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        getStoryLocation(googleMap)
    }

    private fun getStoryLocation(googleMap: GoogleMap){
        mapsViewModel.getStoriesMap().observe(this){
            if (it != null){
                when(it){
                    is Result.Error -> {
                        Toast.makeText(this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success ->{
                        showMarkerStory(it.data.listStory, googleMap)
                    }
                }
            }
        }
    }

    private fun showMarkerStory(listStory: List<Story>, googleMap: GoogleMap){
        listStory.forEach {
            val latLng = LatLng(it.lat, it.lon)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(it.name)
            )
            boundsBuilder.include(latLng)
        }
    }
}