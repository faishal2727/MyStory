package com.example.ceritaku.ui.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.ceritaku.R
import com.example.ceritaku.custom_view.MyAlertDialog
import com.example.ceritaku.databinding.ActivityUploadBinding
import com.example.ceritaku.factory.ViewModelFactory
import com.example.ceritaku.ui.home.MainActivity
import com.example.ceritaku.util.createCustomTempFile
import com.example.ceritaku.util.reduceFileImage
import com.example.ceritaku.util.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var viewModelFactory: ViewModelFactory
    private val uplpadVieModel: UploadViewModel by viewModels { viewModelFactory }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_response),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setViewModel()
        setPermission()
        btnUpload()
        btnCamera()
        btnGalery()
        addLoc()
        setActionBar()

    }

    private fun setViewModel() {
        viewModelFactory = ViewModelFactory.getInstnce(binding.root.context)
    }

    private fun setActionBar() {
        supportActionBar?.title = "Upload Story"
    }

    private fun setPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun getLoc() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                10
            )
            return
        }

        val loc = fusedLocationProviderClient.lastLocation
        loc.addOnSuccessListener {
            if (it != null) {
                val lat = it.latitude.toString()
                val lot = it.longitude.toString()
                binding?.tvLocation?.text = lat + "," + lot
                Toast.makeText(this, "Berhasil Mendapatkan Lokasi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal Mendapatkan Lokasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun btnCamera() {
        binding.btnCameraX.setOnClickListener {
            startCamera()
        }
    }

    private fun btnGalery() {
        binding.btnGalery.setOnClickListener {
            startGalery()
        }
    }

    private fun btnUpload() {
        binding.btnUpload.setOnClickListener {
            uploadStory()
        }
    }

    private fun addLoc() {
        binding.addLoaction.setOnClickListener {
            getLoc()
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.example.ceritaku",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGalery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.pilih_gambar))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding?.previewImageView?.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding?.previewImageView?.setImageURI(selectedImg)
        }
    }

    private fun uploadStory() {
        if (getFile != null) {
            val task = fusedLocationProviderClient.lastLocation
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    10
                )
                return
            }

            var lat: Double
            var lon: Double

            task.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    val file = reduceFileImage(getFile as File)
                    val description = binding?.edtDesc?.text.toString()
                        .toRequestBody("text/plain".toMediaTypeOrNull())
                    val currentImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        currentImageFile
                    )
                    val desc = binding?.edtDesc?.text.toString()
                    if (desc.isEmpty()) {
                        binding?.edtDesc?.error = getString(R.string.wajib_isi_desc)
                    } else {
                        uplpadVieModel.uploadStory(imageMultipart,description,lat, lon).observe(this){
                            if (it != null) {
                                showLoading(false)
                                successAlert()
                            } else {
                                Toast.makeText(this, "Gagal Upload Story", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    if (getFile != null) {
                        val file = reduceFileImage(getFile as File)
                        val description = binding?.edtDesc?.text.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                        val currentImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            currentImageFile
                        )
                        val desc = binding?.edtDesc?.text.toString()
                        if (desc.isEmpty()) {
                            binding?.edtDesc?.error = getString(R.string.wajib_isi_desc)
                        } else {
                            uplpadVieModel.uploadStoryNotLoc(imageMultipart,description).observe(this){
                                if (it != null) {
                                    showLoading(false)
                                    successAlert()
                                    Toast.makeText(this, "Sukses Upload Story", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(this, "Gagal Upload Story", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.masukan_gambar), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.masukan_gambar), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarUpload.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun successAlert() {
        MyAlertDialog(
            this,
            R.string.sukses_upload,
            R.drawable.success,
            fun() {
                val moveActivity = Intent(this, MainActivity::class.java)
                moveActivity.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(moveActivity)
                finish()
            }
        ).show()
        binding.previewImageView.setImageResource(R.drawable.ic_person)
        binding.edtDesc.text?.clear()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}