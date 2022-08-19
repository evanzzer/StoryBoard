package com.leafy.storyboard.ui.main.details

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.leafy.storyboard.R
import com.leafy.storyboard.core.domain.model.Story
import com.leafy.storyboard.databinding.ActivityDetailBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DATA = "extra_story"
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val data = intent.getParcelableExtra<Story>(EXTRA_DATA)

        if (data != null) {
            val dateStr = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
                .format(
                    ZonedDateTime.parse(data.createdAt).withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime()
                )
            binding.apply {
                Glide.with(this@DetailActivity)
                    .load(data.photoUrl)
                    .centerCrop()
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .into(binding.imgStory)

                tvName.text = data.name

                // Android SDK 26+ TODO: Parse support for 25 and lower
                tvDate.text = resources.getString(R.string.createdAt, dateStr)
                tvDescription.text = data.description
            }

            if (data.lat != null && data.lon != null) {
                val mapFragment = supportFragmentManager
                    .findFragmentById(binding.mapLocation.id) as SupportMapFragment
                mapFragment.getMapAsync { googleMap ->
                    val storyLocation = LatLng(data.lat, data.lon)
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
                    googleMap.addMarker(MarkerOptions().position(storyLocation).title(data.name))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(storyLocation))

                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(storyLocation, 16f)
                    )
                }
            } else {
                binding.mapLocation.visibility = View.GONE
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.errorTitle)
                .setMessage(R.string.errorText)
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}