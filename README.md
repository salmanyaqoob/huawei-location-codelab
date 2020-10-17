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

Download Location Codelab

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

**(TODO 1.0)** Add build dependencies. 

    dependencies {
        implementation 'com.huawei.hms:location:5.0.0.302'
    }

### 1. Apply for location permissions. 
**(TODO 1.1)** Add related permissions to the **AndroidManifest.xml** file of your project.

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

In Android Q, if your app needs to track the device location even when it runs in the background, you need to declare the **ACCESS_BACKGROUND_LOCATION** permission in the **AndroidManifest.xml** file.

**(TODO 1.2)** add background location permission.

    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>
















