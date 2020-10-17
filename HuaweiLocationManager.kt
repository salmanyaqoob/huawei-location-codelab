package com.android.maplocationkotlin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import com.huawei.hms.maps.model.LatLng

/**
 * Wrapper class for handling Location related methods
 */
class HuaweiLocationManager constructor(private val context : Context) {
    val TAG = "Huawei_Location"
    //region vars
    private val mFusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val mSettingsClient: SettingsClient by lazy {
        LocationServices.getSettingsClient(context)
    }

    private var mLocationCallback: LocationCallback? = null
    //endregion

    companion object {
        const val REQUEST_CHECK_SETTINGS = 123
        const val REQUEST_ENABLE_GPS = 456
    }


    /**
     * Create location setting task with Success Callback
     */
    private fun getLocationSettingsTask(
        onSuccess: ((locationSettingsResponse: LocationSettingsResponse?) -> Unit)? = null
    ): Task<LocationSettingsResponse>? {

        val builder = LocationSettingsRequest.Builder()
        val locationRequest = LocationRequest()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        // Check the device location settings.
        return mSettingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                // Initiate location requests when the location settings meet the requirements.
                Log.d(TAG, "LocationManager -> Device location is open")
                // Notify
                onSuccess?.invoke(it)
            }
    }


    /**
     * Check Location settings is enable or not
     * Manually handle success and fail situations
     */
    fun checkLocationSettings(
        onSuccess: ((locationSettingsResponse: LocationSettingsResponse?) -> Unit)? = null,
        onFail: ((exception: Exception) -> Unit)? = null
    ) {

        // Check the device location settings.
        getLocationSettingsTask(onSuccess)?.addOnFailureListener { e ->
            // Device location settings do not meet the requirements.
            val statusCode = (e as ApiException).statusCode
            Log.d(TAG, "LocationManager -> Device location is close with status code $statusCode exception: ${e.localizedMessage}")
            // Notify
            onFail?.invoke(e)
        }
    }

    /**
     * Check Location settings is enable or not
     * If location disable, automatically show system dialog for enable location
     *
     * Handle on Activity Result
     *
     * if (requestCode == REQUEST_CHECK_SETTINGS) {
     *   when (resultCode) {
     *       Activity.RESULT_OK -> {
     *           Log.d(TAG, "User confirm to access location")
     *           }
     *       Activity.RESULT_CANCELED -> {
     *           Log.d(TAG, "User denied to access location")
     *           }
     *       }
     *  }
     */
    fun checkLocationSettingsAndShowPopup(
        activity: Activity,
        onSuccess: ((locationSettingsResponse: LocationSettingsResponse?) -> Unit)? = null
    ) {

        getLocationSettingsTask(onSuccess)?.addOnFailureListener { e ->
            // Device location settings do not meet the requirements.
            val statusCode = (e as ApiException).statusCode
            Log.d(TAG, "LocationManager -> Device location is close with status code $statusCode, User will get system dialog for enable location")
            when (statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    val rae = e as ResolvableApiException
                    // Call startResolutionForResult to display a pop-up asking the user to enable related permission.
                    // rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS) // -> Only call Activity's onActivityResult
                    activity.startIntentSenderForResult(
                        rae.resolution.intentSender,
                        REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null
                    ) // -> Call also Fragment's onActivityResult
                } catch (sie: IntentSender.SendIntentException) {
                    Log.d(TAG, "LocationKit -> Error while showing system pop-up with error message:" + sie.message)
                }
            }
        }
    }

    /**
     * Check Location settings is enable or not
     * If location disable, automatically show system dialog for enable location
     *
     * Handle on Activity Result
     *
     * if (requestCode == REQUEST_CHECK_SETTINGS) {
     *   when (resultCode) {
     *       Activity.RESULT_OK -> {
     *           Log.d(TAG, "User confirm to access location")
     *           }
     *       Activity.RESULT_CANCELED -> {
     *           Log.d(TAG, "User denied to access location")
     *           }
     *       }
     *  }
     */
    fun checkLocationSettingsAndShowPopup(
        fragment: Fragment,
        onSuccess: ((locationSettingsResponse: LocationSettingsResponse?) -> Unit)? = null
    ) {
        getLocationSettingsTask(onSuccess)?.addOnFailureListener { e ->
            // Device location settings do not meet the requirements.
            val statusCode = (e as ApiException).statusCode
            Log.d(TAG, "LocationManager -> Device location is close with status code $statusCode, User will get system dialog for enable location")
            when (statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    val rae = e as ResolvableApiException
                    // Call startResolutionForResult to display a pop-up asking the user to enable related permission.
                    // rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS) // -> Only call Activity's onActivityResult
                    fragment.startIntentSenderForResult(
                        rae.resolution.intentSender,
                        REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null
                    ) // -> Call also Fragment's onActivityResult
                } catch (sie: IntentSender.SendIntentException) {
                    Log.d(TAG, "LocationKit -> Error while showing system pop-up with error message:" + sie.message)
                }
            }
        }
    }

    /**
     * Open Location settings page
     *
     * User manually handle result in onActivityResult
     *
     * (requestCode == REQUEST_ENABLE_GPS) {
     *      val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
     *      val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
     *       if (!isGpsEnabled) {
     *           Log.d(TAG, "User closed to settings page")
     *       } else {
     *           Log.d(TAG, "User open location from settings page")
     *       }
     *  }
     */
    fun openGpsEnableSetting(activity: Activity) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }

    /**
     * Open Location settings page
     *
     * User manually handle result in onActivityResult
     *
     * (requestCode == REQUEST_ENABLE_GPS) {
     *      val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
     *      val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
     *       if (!isGpsEnabled) {
     *           Log.d(TAG, "User closed to settings page")
     *       } else {
     *           Log.d(TAG, "User open location from settings page")
     *       }
     *  }
     */
    fun openGpsEnableSetting(fragment: Fragment) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        fragment.startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }

    /**
     * Obtaining Location Availability from Huawei Location Services
     */
    fun getLocationAvailability(
        onSuccess: ((locationAvailability: LocationAvailability?) -> Unit)? = null,
        onFail: ((exception: Exception) -> Unit)? = null
    ) {

        val locationAvailabilityTask: Task<LocationAvailability> =  mFusedLocationProviderClient.locationAvailability
        locationAvailabilityTask.addOnSuccessListener { locationAvailability ->
            if (locationAvailability != null) {
                Log.d(TAG, "Location Kit -> getLocationAvailability onSuccess:${locationAvailability.isLocationAvailable}")
            }
            // Notify
            onSuccess?.invoke(locationAvailability)
        }
            .addOnFailureListener { e ->
                Log.d(TAG, "getLocationAvailability onFailure:" + e.message)
                // Notify
                onFail?.invoke(e)
            }
    }


    /**
     * Check location service hard wares are available
     */
    fun checkLocationServiceAvailability(): Boolean? {
        val lm = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        var isGpsEnable = false
        var isNetworkEnable = false
        try {
            isGpsEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            isNetworkEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!isGpsEnable && !isNetworkEnable) {
            Log.d(TAG, "Location Kit -> checkLocationServiceAvailability: False")
            return false
        }
        Log.d(TAG, "Location Kit -> checkLocationServiceAvailability: True")
        return true
    }

    /**
     * Get Last Know Location
     */
    fun getLastKnownLocation(
        onSuccess: ((lastKnownLocation: Location?) -> Unit)? = null,
        onFail: ((exception: Exception) -> Unit)? = null
    ) {
        val task: Task<Location> = mFusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { lastKnowLocation ->

            if (lastKnowLocation == null) {
                Log.d(TAG, "LocationKit -> Last Known Location is empty")
            } else {
                val currentLatLng = LatLng(
                    lastKnowLocation.latitude,
                    lastKnowLocation.longitude
                )
                Log.d(TAG, "LocationKit -> Last Known Location: $currentLatLng")
            }
            // Notify
            onSuccess?.invoke(lastKnowLocation)

        }.addOnFailureListener { exception ->
            Log.d(TAG, "LocationKit -> Failed to get Last Known Location with exception: ${exception.localizedMessage}")
            // Notify
            onFail?.invoke(exception)
        }
    }


    /**
     * Start listening location updates
     */
    fun registerLocationUpdates(
        interval: Long = 10000,
        onSuccess: ((location: Location?) -> Unit)? = null,
        onFail: ((locationAvailability: LocationAvailability?) -> Unit)? = null
    ) {
        val mLocationRequest = LocationRequest()
        // Set the location update interval (in milliseconds).
        mLocationRequest.interval = interval
        // Set the weight.
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        // Get location update only once
        // mLocationRequest.numUpdates = 1

        // Create location callback
        if (mLocationCallback == null)
            mLocationCallback = createLocationCallback(onSuccess, onFail)

        // Request location update
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.getMainLooper()
        )
            .addOnSuccessListener {
                // Requesting location updates is started successfully.
                Log.d(TAG, "LocationKit -> Start Location Updates Successfully")
            }
            .addOnFailureListener { exception ->
                // Failed to start location updates.
                Log.d(TAG, "LocationKit -> Start Location Updates Failed with exception: ${exception.localizedMessage}")
            }
    }

    /**
     * Stop listening location updates
     */
    fun unregisterLocationUpdates() {

        mLocationCallback?.let { locationCallback ->
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener {
                    // Requesting location updates is stopped successfully.
                    Log.d(TAG, "LocationKit -> Stop Listening Location Successfully")
                }
                .addOnFailureListener {
                    // Failed to stop requesting location updates.
                    Log.d(TAG, "LocationKit -> Stop Listening Location Failed with exception: ${it.localizedMessage}")
                }
        }
    }

    /**
     * Create Location Callback
     */
    private fun createLocationCallback(
        onSuccess: ((location: Location?) -> Unit)? = null,
        onFail: ((locationAvailability: LocationAvailability?) -> Unit)? = null
    ): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val currentLatLng = LatLng(
                    locationResult.lastLocation.latitude,
                    locationResult.lastLocation.longitude
                )
                Log.d(TAG, "LocationKit -> currentLatLng: $currentLatLng")
                // Notify
                onSuccess?.invoke(locationResult.lastLocation)
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {

                val flag = locationAvailability.isLocationAvailable
                Log.i(TAG, "LocationKit isLocationAvailable:$flag")
                // Notify
                onFail?.invoke(locationAvailability)
            }
        }
    }
}