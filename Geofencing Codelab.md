# Geofencing Codelab

![enter image description here](https://developer.android.com/images/training/geofence.png)

## Overview

Geofencing combines awareness of the user's current location with awareness of the user's proximity to locations that may be of interest. To mark a location of interest, you specify its latitude and longitude. To adjust the proximity for the location, you add a radius. The latitude, longitude, and radius define a geofence, creating a circular area, or fence, around the location of interest. 

## What You Will Create

In this codelab, you will create an app that obtains device location information and create home geofence. Show notification to the user when he enters, stay or exit the geofence. 

## What You Will Learn

How to use location Api's to create geofencing into your Android Application.

## Download the Location Codelab Project

Download Sample Location GeoFencing Codelab [sample-application](https://github.com/salmanyaqoob/huawei-location-codelab/blob/main/huawei-location-codelab.zip?raw=true)

### Main Configurations

### **Adding Build Dependencies**
Open the **build.gradle** file in the **app** directory.
![app build,gradle](https://developer.huawei.com/consumer/en/codelab/HMSLocationKit-Kotlin/img/eb2692994e6db7d1.png)

**(Step 1.0)** Add app build dependencies. 

    // TODO: 1.0 Add app build dependencies.
    dependencies {
        implementation 'com.google.android.gms:play-services-location:17.0.0'
    }

### 1. Apply for location permissions. 
**(Step 1.1)** Add Location related permissions to the **AndroidManifest.xml** file of your project.

    <!-- TODO: Step 1.1, Add Location related permissions -->  
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />  
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />  
    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />

### 2. Add GoogleLocationManager class 
**(Step 1.2)** Add GoogleLocationManager class into your project.
Download Google Location Manager class.

### 3. Create GeofenceIntentService class  
**(Step 1.3)** Create **GeofenceIntentService** class extend with **JobIntentService**.

    class GeofenceIntentService : JobIntentService() {}

#### onHandleWork method

    override fun onHandleWork(intent: Intent) {  
        val geofenceData = GeofencingEvent.fromIntent(intent)  
        if (geofenceData != null) {  
            val conversion: Int = geofenceData.geofenceTransition  
      val geofenceTransition = geofenceData.triggeringGeofences as ArrayList<Geofence>  
            val geofenceTransitionDetails = getGeofenceTransitionDetails(  
                conversion,  
      geofenceTransition  
            )  
            sendNotification(geofenceTransitionDetails)  
            Log.i("GeofenceTest", geofenceTransitionDetails)  
        }  
    }

#### getGeofenceTransitionDetails method

    private fun getGeofenceTransitionDetails(  
        conversion: Int,  
      triggeringGeofences: ArrayList<Geofence>  
    ): String {  
        val geofenceConversion = getConversionString(conversion)  
        val triggeringGeofencesIdsList: ArrayList<String?> = ArrayList()  
        for (geofence in triggeringGeofences) {  
            triggeringGeofencesIdsList.add(geofence.requestId)  
        }  
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)  
        return "$geofenceConversion: $triggeringGeofencesIdsString"  
    }

#### sendNotification method

    private fun sendNotification(notificationDetails: String) {  
        // Get an instance of the Notification manager  
      val mNotificationManager =  
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager  
        // Android O requires a Notification Channel.  
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
            val name: CharSequence = getString(R.string.app_name)  
            // Create the channel for the notification  
      val mChannel =  
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)  
      
            // Set the Notification Channel for the Notification Manager.  
      mNotificationManager.createNotificationChannel(mChannel)  
        }  
      
        // Create an explicit content Intent that starts the main Activity.  
      val notificationIntent = Intent(applicationContext, MainActivity::class.java)  
      
        // Construct a task stack.  
      val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)  
      
        // Add the main Activity to the task stack as the parent.  
      stackBuilder.addParentStack(MainActivity::class.java)  
      
        // Push the content Intent onto the stack.  
      stackBuilder.addNextIntent(notificationIntent)  
      
        // Get a PendingIntent containing the entire back stack.  
      val notificationPendingIntent: PendingIntent? =  
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)  
      
        // Get a notification builder that's compatible with platform versions >= 4  
      val builder = NotificationCompat.Builder(this)  
      
        // Define the notification settings.  
      builder.setSmallIcon(R.drawable.ic_launcher_foreground) // In a real app, you may want to use a library like Volley  
     // to decode the Bitmap.  .setLargeIcon(  
                BitmapFactory.decodeResource(  
                    resources,  
      R.drawable.ic_marker  
      )  
            )  
            .setColor(Color.RED)  
            .setContentTitle(notificationDetails)  
            .setContentText("Hey! There you are arrived to your geofence.")  
            .setContentIntent(notificationPendingIntent)  
      
        // Set the Channel ID for Android O.  
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  
            builder.setChannelId(CHANNEL_ID) // Channel ID  
      }  
      
        // Dismiss notification once the user touches it.  
      builder.setAutoCancel(true)  
        assert(mNotificationManager != null)  
        mNotificationManager.notify(0, builder.build())  
    }

#### getConversionString method
    private fun getConversionString(conversionType: Int): String {  
        return when (conversionType) {  
            Geofence.GEOFENCE_TRANSITION_ENTER -> "You are entered to your home location"  
      Geofence.GEOFENCE_TRANSITION_EXIT-> "You are exited to your home location"  
      Geofence.GEOFENCE_TRANSITION_DWELL -> "You are inside your home location"  
      else -> "No Conversion found"  
      }  
    }

#### companion object method

    companion object {  
        private const val JOB_ID = 573  
      private const val CHANNEL_ID = "channel_01"  
      fun enqueueWork(context: Context, intent: Intent) {  
            enqueueWork(  
                context,  
      GeofenceIntentService::class.java, JOB_ID, intent  
            )  
        }  
    }

### 4. Create GeoFenceBroadcastReceiver Broadcast Receiver  
**(Step 1.4)** Create **GeoFenceBroadcastReceiver ** class extend with **BroadcastReceiver()**.

    // TODO: 1.4 Create GeoFenceBroadcastReceiver Broadcast Receiver  
    class GeoFenceBroadcastReceiver : BroadcastReceiver() {  
        private val TAG = "geod Geofence"  
      companion object{  
            const val ACTION_PROCESS_LOCATION = "com.android.geofencedemo.receivers.GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION"  
      }  
      
        override fun onReceive(context: Context, intent: Intent) {  
            Log.i(TAG, "GeoFenceBroadcastReceiver ()")  
            GeofenceIntentService.enqueueWork(context, intent)  
        }  
    }

### 4. Manage MainActivity Class
**(Step 1.5)** Attach **locationManager**, **geofencingClient**, **pendingIntent**.

    // TODO: 1.5 Attach locationManager, geofencingClient, pendingIntent  
    locationManagerGoogle = GoogleLocationManager(applicationContext)  
      
    geofencingClient = LocationServices.getGeofencingClient(this)  
    pendingIntent = getPendingIntent()

**(Step 1.6)** Get **Device Location Update**.

    // TODO: 1.6 Get device location update  
    fun handleGoogleLocation() {  
          
        locationManagerGoogle.checkLocationSettingsAndShowPopup(  
            this,  
      onSuccess =  
      {  
      locationManagerGoogle.registerLocationUpdates(onSuccess = {  
      val location: Location? = it  
      myLocation = location  
                    printLocation(location)  
                })  
            }  
      )  
    }

**(Step 1.7)** Get **PendingIntent** and attached **Broadcast Receiver**

    // TODO: 1.7 get PendingIntent and attached Broadcast Receiver  
    private fun getPendingIntent(): PendingIntent {  
        // The GeoFenceBroadcastReceiver class is a customized static broadcast class.  
      val intent = Intent(this, GeoFenceBroadcastReceiver::class.java)  
        intent.action = GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION  
      return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)  
    }

**(Step 1.8)** Get **GeofencingRequest** and define initial triggers

    // TODO: 1.8 get GeofencingRequest, define initial triggers  
    private fun getGeofencingRequest(): GeofencingRequest? {  
        return GeofencingRequest.Builder().apply {  
      setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_DWELL)  
            addGeofences(geofenceList)  
        }.build()  
    }

**(Step 1.9)** Create **GeoFence** and make list of geofence

    // TODO: 1.9 Create GeoFence, make list of geofence and geofences from geofencingClient  
    private fun createGeoFenceForHome(lat: Double, lon: Double, radius: Float) {  
        geofenceList.add(  
            Geofence.Builder()  
                .setRequestId(HOME_GEOFENCE_ID)  
                .setCircularRegion(lat, lon, radius)  
                .setExpirationDuration(Geofence.NEVER_EXPIRE)  
                .setTransitionTypes(  
                    Geofence.GEOFENCE_TRANSITION_ENTER or  
                    Geofence.GEOFENCE_TRANSITION_DWELL or  
                    Geofence.GEOFENCE_TRANSITION_EXIT  
      )  
                .setLoiteringDelay(10000)  
                .build()  
        )  
      
        if (ActivityCompat.checkSelfPermission(  
                this,  
      Manifest.permission.ACCESS_FINE_LOCATION  
      ) != PackageManager.PERMISSION_GRANTED  
      ) {  
            return  
      }  
        geofencingClient.addGeofences(getGeofencingRequest(), pendingIntent)?.run {  
      addOnSuccessListener {  
      Log.i(TAG, "geod Geofence Created: \nLat: $lat\nLon: $lon")  
            }  
      addOnFailureListener {  
      Log.i(TAG, "failure $it")  
            }  
        }
     }

### 5. Run Application
Run your application and check the location updates and geofencing notification on different events.


## Congratulations

Well done. You have successfully completed this codelab and learned how to:
-   Integrate Location Kit.
-   Call the location service to make geofencing.
