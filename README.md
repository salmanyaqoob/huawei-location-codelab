# Huawei Location Codelab

![](https://developer.huawei.com/consumer/en/codelab/HMSLocationKit-Kotlin/img/4c0410cf75576835.png)

## Overview

HUAWEI Location Kit combines GPS, Wi-Fi, and base station location data to allow you to quickly obtain precise user locations, giving you a global positioning capability and helping you deliver services to a global audience. In this codelab, you will create an Android app (codelab app) and integrate Location Kit into the app. The following figure shows the basic architecture of the app. The codelab app will use the integrated Location SDK to call the location service of HMS Core (APK).

## What You Will Create

In this codelab, you will create an app that obtains device location information.

## What You Will Learn

In this codelab, you will learn how to:

-   Configure the development environment.
-   Call the location service of HUAWEI Location Kit.

## Download the Location Codelab Project

Download Huawei Location Codelab [sample-application](https://github.com/salmanyaqoob/huawei-location-codelab/blob/main/huawei-location-codelab.zip?raw=true)

## Configuring the Development Environment
In this codelab, you need to create a project in Android Studio.

### Main Configurations
Open the **build.gradle** file in the root directory of your Android Studio project.
![project build.gradle](https://developer.huawei.com/consumer/en/codelab/HMSLocationKit-Kotlin/img/86e473a8e2f9ff82.png)

Go to **allprojects** > **repositories** and configure the Maven repository address for the HMS Core SDK.

    allprojects {
        repositories {
            maven { url 'https://developer.huawei.com/repo/' }
            google()
            jcenter()
        }
    }

Go to **buildscript** > **repositories** and configure the Maven repository address for the HMS Core SDK.

    buildscript {
	    repositories {
            maven { url 'https://developer.huawei.com/repo/' }
            google()
            jcenter()
	    }
        dependencies {
            classpath 'com.android.tools.build:gradle:3.3.2'
        }
    }

### **Adding Build Dependencies**
Open the **build.gradle** file in the **app** directory.
![app build,gradle](https://developer.huawei.com/consumer/en/codelab/HMSLocationKit-Kotlin/img/eb2692994e6db7d1.png)

**(Step 1.0)** Add app build dependencies. 

    // TODO: 1.0 Add app build dependencies.
    dependencies {
        implementation 'com.huawei.hms:location:5.0.0.302'
    }

### 1. Apply for location permissions. 
**(Step 1.1)** Add Location related permissions to the **AndroidManifest.xml** file of your project.

    <!-- TODO: Step 1.1, Add Location related permissions -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

In Android Q, if your app needs to track the device location even when it runs in the background, you need to declare the **ACCESS_BACKGROUND_LOCATION** permission in the **AndroidManifest.xml** file.

**(Step 1.2)** Add background location permission.

    <!-- TODO: Step 1.2, Add background location permission. -->
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>

**(Step 1.3)** Add dynamic application for related permissions to the **MainActivity.kt** file.

    // TODO: 1.3 check location permission
    // check location permission  
	if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {  
	    Log.i(TAG, "sdk < 28 Q")  
	    if (ActivityCompat.checkSelfPermission(  
	                    this,  
	  Manifest.permission.ACCESS_FINE_LOCATION  
	  ) != PackageManager.PERMISSION_GRANTED  
	  && ActivityCompat.checkSelfPermission(  
	                    this,  
	  Manifest.permission.ACCESS_COARSE_LOCATION  
	  ) != PackageManager.PERMISSION_GRANTED  
	  ) {  
	        val strings = arrayOf(  
	                Manifest.permission.ACCESS_FINE_LOCATION,  
	  Manifest.permission.ACCESS_COARSE_LOCATION  
	  )  
	        ActivityCompat.requestPermissions(this, strings, 1)  
	    }  
	} else {  
	    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(  
	                    this,  
	  Manifest.permission.ACCESS_COARSE_LOCATION  
	  ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(  
	                    this,  
	  "android.permission.ACCESS_BACKGROUND_LOCATION"  
	  ) != PackageManager.PERMISSION_GRANTED  
	  ) {  
	        val strings = arrayOf(  
	                Manifest.permission.ACCESS_FINE_LOCATION,  
	  Manifest.permission.ACCESS_COARSE_LOCATION,  
	  "android.permission.ACCESS_BACKGROUND_LOCATION"  
	  )  
	        ActivityCompat.requestPermissions(this, strings, 2)  
	    }  
	}

**(Step 1.4)** Add permission application result callback to the **MainActivity.kt** file.

    // TODO: 1.4 Permission result callback to application   
    override fun onRequestPermissionsResult(  
            requestCode: Int, permissions: Array<String>, grantResults: IntArray  
    ) {  
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)  
        if (requestCode == 1) {  
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED  
      ) {  
                Log.i(  
                        TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful"  
      )  
            } else {  
                Log.i(  
                        TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed"  
      )  
            }  
        }  
        if (requestCode == 2) {  
            if (grantResults.size > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED  
      ) {  
                Log.i(  
                        TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful"  
      )  
            } else {  
                Log.i(  
                        TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed"  
      )  
            }  
        }  
    }

### 2. Location Provider Client and Device Setting Client
**(Step 1.5)** Create a location provider client and device setting client.

    // TODO: 1.5 Create a location provider client and device setting client  
    // create fusedLocationProviderClient  
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)  
    // create settingsClient  
    settingsClient = LocationServices.getSettingsClient(this)

### 3. Location Request
**(Step 1.6)** Create a location request.

    // TODO: 1.6 Create a location request.  
    // Set the interval for location updates, in milliseconds.  
    // set the priority of the request
    mLocationRequest = LocationRequest().apply {  
      interval = TimeUnit.SECONDS.toMillis(1)  
      // Sets the fastest rate for active location updates. This interval is exact, and your  
      // application will never receive updates more frequently than this value.  
      fastestInterval = TimeUnit.SECONDS.toMillis(10)  
      needAddress = true  
      // Sets the maximum time when batched location updates are delivered. Updates may be  
      // delivered sooner than this interval.
      // maxWaitTime = TimeUnit.SECONDS.toMillis(1)  
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY  
    }

### 4. LocationCallback
**(Step 1.7)** Register LocationCallback to get Location Updates

    // TODO: 1.7  Register LocationCallback to get Location Updates  
      
    if (null == mLocationCallback) {  
        mLocationCallback = object : LocationCallback() {  
            override fun onLocationResult(locationResult: LocationResult?) {  
                if (locationResult != null) {  
                    val locations: List<Location> = locationResult.locations  
                    if (locations.isNotEmpty()) {  
                        for (location in locations) {  
                            Log.i(TAG, "onLocationResult location[Longitude,Latitude,Accuracy]:${location.longitude} , ${location.latitude} , ${location.accuracy}"  
      )  
                            makeLocationLog("onLocationResult location[Longitude,Latitude,Accuracy]:${location.longitude} , ${location.latitude} , ${location.accuracy}")  
                        }  
                    }  
                }  
            }  
      
            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {  
                locationAvailability?.let {  
                    val flag: Boolean = locationAvailability.isLocationAvailable  
                    Log.i(TAG, "onLocationAvailability isLocationAvailable:$flag")  
                    makeLocationLog("onLocationAvailability isLocationAvailable:$flag")  
                }  
            }  
        }  
    }

### 5. Requests location updates
**(Step 1.8)** Requests location updates with a callback on the specified Looper thread.

    // TODO: 1.8  Requests location updates with a callback on the specified Looper thread.  
    private fun requestLocationUpdatesWithCallback() {  
        // check location permission  
        if (!hasLocationPermission()) requestLocationPermission()  
      
        try {  
            val builder = LocationSettingsRequest.Builder()  
            builder.addLocationRequest(mLocationRequest)  
            val locationSettingsRequest = builder.build()  
            // check devices settings before request location updates.  
            //Before requesting location update, invoke checkLocationSettings to check device settings.
            val locationSettingsResponseTask: Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(locationSettingsRequest)  
      
            locationSettingsResponseTask  
                    .addOnSuccessListener { locationSettingsResponse: LocationSettingsResponse? ->  
                        Log.i(TAG, "check location settings success  {$locationSettingsResponse}")  
                        makeLocationLog("check location settings success  {$locationSettingsResponse}")  
                        // request location updates
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())  
                                .addOnSuccessListener {  
                                    Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess")  
                                    makeLocationLog("requestLocationUpdatesWithCallback onSuccess")  
                                }  
      
                               .addOnFailureListener { e ->  
                                Log.e(TAG, "requestLocationUpdatesWithCallback onFailure:${e.message}")  
                                makeLocationLog("requestLocationUpdatesWithCallback onFailure:${e.message}")  
                                }  
     }  .addOnFailureListener { e: Exception ->  
      Log.e(TAG, "checkLocationSetting onFailure:${e.message}")  
                        makeLocationLog("checkLocationSetting onFailure:${e.message}")  
                        when ((e as ApiException).statusCode) {  
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {  
                                val rae = e as ResolvableApiException  
                                rae.startResolutionForResult(  
                                        this, 0  
      )  
                            } catch (sie: IntentSender.SendIntentException) {  
                                Log.e(TAG, "PendingIntent unable to execute request.")  
                                makeLocationLog("PendingIntent unable to execute request.")  
                            }  
                        }  
                    }  
      } catch (e: Exception) {  
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:${e.message}")  
            makeLocationLog("requestLocationUpdatesWithCallback exception:${e.message}")  
        }  
    }

### 6. Remove location updates
**(Step 1.9)** Remove the Location request with callback

    // TODO: 1.9  Remove the Location request with callback  
    private fun removeLocationUpdatesWithCallback() {  
        try {  
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)  
                    .addOnSuccessListener {  
                        Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess")  
                        makeLocationLog("removeLocationUpdatesWithCallback onSuccess")  
                    }  
      .addOnFailureListener { e ->  
                        Log.e(TAG, "removeLocationUpdatesWithCallback onFailure:${e.message}")  
                        makeLocationLog("removeLocationUpdatesWithCallback onFailure:${e.message}")  
                    }  
      } catch (e: Exception) {  
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:${e.message}")  
            makeLocationLog("removeLocationUpdatesWithCallback exception:${e.message}")  
        }  
    }

### 7. Run Application
Run your application and check the location updates.

![Huawei location codelab application](https://raw.githubusercontent.com/salmanyaqoob/huawei-location-codelab/main/images/codelab.png)

