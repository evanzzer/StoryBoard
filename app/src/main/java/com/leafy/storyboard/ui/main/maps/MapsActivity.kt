package com.leafy.storyboard.ui.main.maps

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.leafy.storyboard.R
import com.leafy.storyboard.core.domain.model.Story
import com.leafy.storyboard.databinding.ActivityMapsBinding
import com.leafy.storyboard.utils.ObserverGenerator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsActivity : AppCompatActivity() {
    private val viewModel by viewModel<MapsViewModel>()
    private val sp by lazy {
        getSharedPreferences(resources.getString(R.string.appDirectory), Context.MODE_PRIVATE)
    }
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMapsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val token = sp.getString(resources.getString(R.string.tokenKey), "") ?: ""
        invokeSearch(token)

        binding.layoutError.btnRefresh.setOnClickListener { invokeSearch(token) }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun invokeSearch(token: String) {
        viewModel.getStoryWithLocation(token).observe(this, object : ObserverGenerator<List<Story>>() {
            override fun getSuccessUI(data: List<Story>?) {
                binding.apply {
                    progressBar.visibility = View.GONE
                    map.visibility = View.VISIBLE
                }

                val mapFragment = supportFragmentManager
                    .findFragmentById(binding.map.id) as SupportMapFragment
                mapFragment.getMapAsync { googleMap ->
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity, R.raw.map_style))

                    googleMap.uiSettings.apply {
                        isZoomControlsEnabled = true
                        isIndoorLevelPickerEnabled = true
                        isCompassEnabled = true
                        isMapToolbarEnabled = true
                    }

                    val boundsBuilder = LatLngBounds.Builder()
                    data?.forEach { data ->
                        if (data.lat != null && data.lon != null) {
                            val latLng = LatLng(data.lat, data.lon)
                            googleMap.addMarker(MarkerOptions().position(latLng).title(data.name))
                            boundsBuilder.include(latLng)
                        }
                    }
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            boundsBuilder.build(),
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
            }

            override fun getEmptyUI() {
                binding.apply {
                    progressBar.visibility = View.GONE
                    layoutEmpty.root.visibility = View.VISIBLE
                }
            }

            override fun getErrorUI(message: String?) {
                binding.apply {
                    progressBar.visibility = View.GONE
                    layoutEmpty.root.visibility = View.VISIBLE
                    layoutError.tvError.text = message
                }
            }

            override fun getLoadingUI() {
                binding.apply {
                    progressBar.visibility = View.VISIBLE
                    map.visibility = View.GONE
                    layoutEmpty.root.visibility = View.GONE
                    layoutError.root.visibility = View.GONE
                }
            }
        }.asObserver())
    }
}