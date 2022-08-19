package com.leafy.storyboard.ui.main.insert

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.leafy.storyboard.R
import com.leafy.storyboard.databinding.ActivityNewStoryBinding
import com.leafy.storyboard.utils.CameraUtils
import com.leafy.storyboard.utils.ObserverGenerator
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class NewStoryActivity : AppCompatActivity() {
    companion object {
        const val RESULT_CODE = 200

        private const val cameraPermission = Manifest.permission.CAMERA
        private const val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        private const val approxLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        private val REQUIRED_PERMISSIONS = arrayOf(fineLocationPermission, approxLocationPermission)
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private var location: Location? = null

    private val viewModel by viewModel<NewStoryViewModel>()
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityNewStoryBinding.inflate(layoutInflater)
    }
    private val sp by lazy {
        getSharedPreferences(resources.getString(R.string.appDirectory), Context.MODE_PRIVATE)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // file picture
            val file = File(viewModel.currentPhotoPath)

            // Set temporary variable for uploading
            viewModel.file = file
            Glide.with(this)
                .load(file)
                .centerCrop()
                .into(binding.imgStory)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            viewModel.file = CameraUtils.uriToFile(this, selectedImg)
            Glide.with(this)
                .load(selectedImg)
                .centerCrop()
                .into(binding.imgStory)
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permission ->
        if (!permission) {
            AlertDialog.Builder(this)
                .setTitle(R.string.alertTitle)
                .setMessage(R.string.cameraPermissionMsg)
                .setPositiveButton("OK") { _, _ -> }
                .show()
        } else {
            openIntentCamera()
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission[fineLocationPermission] ?: false -> {
                // Precise location access granted.
                getMyLastLocation()
            }
            permission[approxLocationPermission] ?: false -> {
                // Only approximate location access granted.
                getMyLastLocation()
            }
            else -> {
                // No location access granted.
                AlertDialog.Builder(this)
                    .setTitle(R.string.alertTitle)
                    .setMessage(R.string.locationPermissionMsg)
                    .setPositiveButton("OK") { _, _ -> finish() }
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getMyLastLocation()

        // Image Load when orientation change
        if (viewModel.file != null) {
            Glide.with(this)
                .load(viewModel.file as File)
                .centerCrop()
                .into(binding.imgStory)
        }

        binding.btnCamera.setOnClickListener {
            if (checkPermission(cameraPermission)) {
                openIntentCamera()
            } else {
                requestCameraPermissionLauncher.launch(cameraPermission)
            }
        }

        binding.btnGallery.setOnClickListener {
            launcherIntentGallery.launch(Intent.createChooser(Intent().also { intent ->
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
            }, resources.getString(R.string.chooseImageMsg)))
        }

        binding.btnPost.setOnClickListener {
            val description = binding.etDescription.text.toString()

            if (viewModel.file == null) {
                Snackbar.make(binding.root, R.string.imageEmptyMsg, Toast.LENGTH_SHORT)
                    .setAction("OK") {}
                    .show()
            } else if (description.isBlank()) {
                binding.layoutDescription.error = resources.getString(R.string.emptyMsg)
            } else {
                val token = sp.getString(resources.getString(R.string.tokenKey), "") ?: ""
                val reducedFile = CameraUtils.reduceFileImage(viewModel.file as File)

                val lat = location?.latitude
                val lon = location?.longitude
                if (lat != null && lon != null) {
                    viewModel.postNewStory(token, reducedFile, description, lat, lon).observe(
                        this, object : ObserverGenerator<String>() {
                            override fun getSuccessUI(data: String?) {
                                setLoadingState(false)
                                Toast.makeText(
                                    this@NewStoryActivity,
                                    R.string.successPostMsg,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                setResult(RESULT_CODE)
                                finish()
                            }

                            override fun getEmptyUI() {}

                            override fun getErrorUI(message: String?) {
                                setLoadingState(false)
                                AlertDialog.Builder(this@NewStoryActivity)
                                    .setTitle(R.string.errorTitle)
                                    .setMessage(message)
                                    .setPositiveButton("OK") { _, _ -> }
                                    .show()
                            }

                            override fun getLoadingUI() {
                                binding.layoutDescription.error = ""
                                setLoadingState(true)
                            }
                        }.asObserver()
                    )
                } else {
                    Toast.makeText(
                        this,
                        R.string.locationNotFoundMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                    getMyLastLocation()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun openIntentCamera() {
        launcherIntentCamera.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)

            CameraUtils.createTempFiles(application).also { file ->
                val photoUri: Uri = FileProvider.getUriForFile(
                    this,
                    resources.getString(R.string.appDirectory),
                    file
                )
                viewModel.currentPhotoPath = file.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
        })
    }

    private fun getMyLastLocation() {
        if (checkPermission(fineLocationPermission) && checkPermission(approxLocationPermission)
            && checkPermission(cameraPermission)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                } else {
                    Toast.makeText(
                        this,
                        R.string.locationNotFoundMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setLoadingState(state: Boolean) {
        binding.apply {
            etDescription.isEnabled = !state
            btnGallery.isEnabled = !state
            btnCamera.isEnabled = !state
            btnPost.isEnabled = !state
            progressBar.visibility = if (state) View.VISIBLE else View.GONE
        }
    }
}
