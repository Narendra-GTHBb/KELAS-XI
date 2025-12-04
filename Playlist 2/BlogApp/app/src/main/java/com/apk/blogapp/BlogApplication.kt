package com.apk.blogapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class BlogApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Firebase App Check disabled for development
        // App Check causes attestation failures in debug builds
        // Enable only for production builds
        Log.d("BlogApp", "Firebase initialized without App Check for development")
    }
}