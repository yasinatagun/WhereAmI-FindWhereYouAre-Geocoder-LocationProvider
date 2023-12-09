package com.example.locationusage

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.locationusage.databinding.ActivityMainBinding
import com.google.android.gms.identity.intents.Address
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var geocoder: Geocoder;
    private lateinit var city: String
    private lateinit var state: String
    private lateinit var country: String
    private lateinit var addresses: List<android.location.Address>

    private lateinit var binding: ActivityMainBinding
    private var permissionControl = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationTask: Task<Location>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.txtCountry.visibility = View.INVISIBLE
        binding.txtState.visibility = View.INVISIBLE
        binding.txtLongitude.visibility = View.INVISIBLE
        binding.txtLatitude.visibility = View.INVISIBLE
        binding.txtCity.visibility = View.INVISIBLE
        geocoder = Geocoder(this, Locale.getDefault())
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnGetLocation.setOnClickListener {
            permissionControl =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

            if (permissionControl != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100
                )


            } else {
                // if location permission granted

                locationTask = fusedLocationProviderClient.lastLocation
                getLocationInfo()

            }
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {

            permissionControl =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                locationTask = fusedLocationProviderClient.lastLocation
                getLocationInfo()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getLocationInfo() {
        binding.txtCountry.visibility = View.VISIBLE
        binding.txtState.visibility = View.VISIBLE
        binding.txtLongitude.visibility = View.VISIBLE
        binding.txtLatitude.visibility = View.VISIBLE
        binding.txtCity.visibility = View.VISIBLE
        binding.imgWorldWideIcon.visibility = View.INVISIBLE

        locationTask.addOnSuccessListener { it ->
            if (it != null) {
                binding.txtLatitude.text = "Latidude: ${it.latitude}"
                binding.txtLongitude.text = "Longitude: ${it.longitude}"

                addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)!!
                city = addresses[0].getAddressLine(0)
                state = addresses[0].adminArea
                country = addresses[0].countryName
                binding.txtCity.text = "Adress: $city"
                binding.txtState.text = "State: $state"
                binding.txtCountry.text = "Country: $country"
            } else {
                binding.txtLatitude.text = "Latidude: Not Granted"
                binding.txtLongitude.text = "Longitude: Not Granted"
            }
        }.addOnFailureListener {

        }
    }
}