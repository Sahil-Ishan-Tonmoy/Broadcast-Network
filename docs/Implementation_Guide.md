# Assignment 02: Viva Preparation Guide

This document outlines the code changes, implementation details, and explanations needed to be "viva ready" for the `Broadcast-Network` project (Assignment 02).

---

## 1. How the Assignment Task was Implemented

The assignment was implemented as a **Single-Activity, Multi-Fragment Android application** using Kotlin. 
Instead of launching separate Activities for every view, it relies on Jetpack's **Navigation component**:
- **`MainActivity.kt`**: Serves as the host container. It initializes the `NavHostFragment` and sets up the Drawer Layout (`navigation view`) using `AppBarConfiguration`.
- **Navigation Graph (`mobile_navigation.xml`)**: Manages the routing between different destinations (Audio, Video, Image Scaling, Broadcast options).
- **ViewBinding**: Adopted across all fragments and activities to replace `findViewById`, making view references safer and null-aware.

---

## 2. Integration of Local Resources

A key part of the assignment involved moving away from internet URLs and embedding median files directly into the APK. Here is how local resources are handled:

### A. Image Integration (`res/drawable/`)
- **File Location:** `res/drawable/sample_image`
- **Implementation (`ImageScaleFragment.kt`):** 
  - We use the third-party **Glide** library to load the image into the `ImageView`.
  - Glide automatically manages background thread decoding and memory caching.
  - Code reference: `Glide.with(this).load(R.drawable.sample_image).into(binding.ivScalableImage)`

### B. Video Integration (`res/raw/`)
- **File Location:** `res/raw/gangnam_style`
- **Implementation (`VideoFragment.kt`):** 
  - We construct a specific Android Resource URI: `"android.resource://${requireContext().packageName}/${R.raw.gangnam_style}"`.
  - This URI acts like a file path that the app's internal `VideoView` can parse and play directly.

### C. Audio Integration (`res/raw/`)
- **File Location:** `res/raw/meow_audio`
- **Implementation (`AudioFragment.kt`):** 
  - The raw resource file descriptor (`AssetFileDescriptor`) is opened using `resources.openRawResourceFd(R.raw.meow_audio)`.
  - This descriptor is passed directly to the `MediaPlayer` as its data source before preparing and playing.

---

## 3. How the Core Functions Work

You must be able to explain the logic of the main features in the app:

### Feature 1: Image Scaling (`ImageScaleFragment`)
- **How it works:** 
  - It utilizes Android's `ScaleGestureDetector` to track user 'pinch' and 'zoom' gestures.
  - The `onTouchListener` captures the events and forwards them to the detector.
  - Inside `onScale()`, a `scaleFactor` is calculated based on the distance between the user's fingers.
  - A graphics `Matrix` applies this dynamic scale factor (clamped between 0.1x and 10.0x size) to the `ImageView`.

### Feature 2: Audio Playback (`AudioFragment`)
- **How it works:** 
  - Managed by an Android `MediaPlayer` instance.
  - **Play:** Initializes the player, sets audio attributes (music playback), loads the local raw file descriptor, and uses `prepareAsync()` (so as not to freeze the UI thread). Once prepared, `mp.start()` begins playback.
  - **Pause/Stop:** Simple API calls (`pause()` and `stop()`).
  - **Memory Management:** The memory is properly freed by calling `mediaPlayer!!.release()` inside the fragment's `onDestroyView()` lifecycle callback, preventing memory leaks if the user leaves the fragment while audio is playing.

### Feature 3: Video Playback (`VideoFragment`)
- **How it works:** 
  - Handled by a built-in `VideoView`.
  - A `MediaController` object is attached and anchored to the `VideoView`, which automatically provides the Play/Pause tracking bar UI overlay.

### Feature 4: Broadcast Receivers
The app features two types of Broadcasts accessible via `BroadcastOptionFragment`:

**1. System Broadcast (Battery - `BroadcastBatteryFragment`)**
- **How it works:** 
  - Dynamically registers a `BroadcastReceiver` during `onResume()` using an `IntentFilter` for `Intent.ACTION_BATTERY_CHANGED`.
  - Unregisters during `onPause()` to prevent severe battery drain when the app is in the background.
  - It extracts the `BatteryManager.EXTRA_LEVEL` (current charge) and `EXTRA_SCALE` (max capacity) from the intent bundle to calculate the precise percentage.

**2. Custom Broadcast (`BroadcastInputFragment` & `BroadcastCustomReceiverFragment`)**
- **How it works:** 
  - A text message is captured in an `EditText` and passed between fragments using a Navigation `Bundle`.
  - An intent is created using a unique, app-specific string (`com.example.assignment02.ACTION_CUSTOM_BROADCAST`) and the package name is set to ensure the broadcast only stays inside the app.
  - The `sendBroadcast(intent)` fires the event containing the extra message data.
  - The custom receiver class sitting in `BroadcastCustomReceiverFragment` intercepts the intent, decodes the extra message string, and updates the `TextView` on screen to prove the data was routed correctly.
