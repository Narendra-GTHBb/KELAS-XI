package com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapppackage com.apkfood.weatherapp



import android.graphics.Color

import android.os.Bundle

import android.view.animation.*import android.graphics.Color

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivityimport android.os.Bundle

import com.apkfood.weatherapp.databinding.ActivityMainBinding

import com.google.gson.Gsonimport android.os.Handlerimport android.graphics.Color

import okhttp3.*

import java.io.IOExceptionimport android.os.Looper

import java.text.SimpleDateFormat

import java.util.*import android.view.animation.*import android.os.Bundle



class MainActivity : AppCompatActivity() {import android.widget.Toast

    private lateinit var binding: ActivityMainBinding

    private lateinit var okHttpClient: OkHttpClientimport androidx.appcompat.app.AppCompatActivityimport android.os.Handlerimport android.Manifest



    override fun onCreate(savedInstanceState: Bundle?) {import androidx.core.content.ContextCompat

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)import com.apkfood.weatherapp.databinding.ActivityMainBindingimport android.os.Looper

        setContentView(binding.root)

import com.google.gson.Gson

        okHttpClient = OkHttpClient()

        fetchWeatherData()import okhttp3.*import android.view.animation.*import android.content.pm.PackageManager

    }

import java.io.IOException

    private fun fetchWeatherData() {

        val url = "https://api.openweathermap.org/data/2.5/weather?lat=-6.2088&lon=106.8456&appid=${Constants.API_KEY}&units=metric"import java.text.SimpleDateFormatimport android.widget.Toast

        val request = Request.Builder().url(url).build()

import java.util.*

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {import androidx.appcompat.app.AppCompatActivityimport android.location.Locationimport android.Manifest

                runOnUiThread {

                    Toast.makeText(this@MainActivity, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()class MainActivity : AppCompatActivity() {

                }

            }    private lateinit var binding: ActivityMainBindingimport androidx.core.content.ContextCompat



            override fun onResponse(call: Call, response: Response) {    private lateinit var okHttpClient: OkHttpClient

                if (response.isSuccessful) {

                    val responseData = response.body?.string()    private var timeHandler: Handler? = nullimport com.apkfood.weatherapp.databinding.ActivityMainBindingimport android.os.Bundle

                    if (responseData != null) {

                        val weatherData = Gson().fromJson(responseData, WeatherData::class.java)    private var timeRunnable: Runnable? = null

                        runOnUiThread {

                            updateUI(weatherData)import com.google.gson.Gson

                        }

                    }    override fun onCreate(savedInstanceState: Bundle?) {

                }

            }        super.onCreate(savedInstanceState)import okhttp3.*import android.os.Handlerimport android.content.pm.PackageManager

        })

    }        binding = ActivityMainBinding.inflate(layoutInflater)



    private fun updateUI(weatherData: WeatherData) {        setContentView(binding.root)import java.io.IOException

        val weatherMain = weatherData.weather[0].main

        val temp = "${weatherData.main.temp.toInt()}°C"

        val description = weatherData.weather[0].description.replaceFirstChar { 

            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()         okHttpClient = OkHttpClient()import java.text.SimpleDateFormatimport android.os.Looper

        }

        val humidity = "${weatherData.main.humidity}%"        fetchWeatherData()

        val windSpeed = "${weatherData.wind.speed} m/s"

        val pressure = "${weatherData.main.pressure} hPa"        startClock()import java.util.*

        val location = weatherData.name

    }

        // Update text

        binding.tvTemperature.text = tempimport android.view.animation.*import android.location.Locationimport android.Manifest

        binding.tvDescription.text = description

        binding.tvHumidity.text = "Humidity: $humidity"    private fun fetchWeatherData() {

        binding.tvWindSpeed.text = "Wind: $windSpeed"

        binding.tvPressure.text = "Pressure: $pressure"        val url = "https://api.openweathermap.org/data/2.5/weather?lat=-6.2088&lon=106.8456&appid=${Constants.API_KEY}&units=metric"class MainActivity : AppCompatActivity() {

        binding.tvLocation.text = location

        val request = Request.Builder().url(url).build()

        // Clear previous animations

        binding.ivWeatherIcon.clearAnimation()    private lateinit var binding: ActivityMainBindingimport android.widget.Toast



        // Set weather icon and animation        okHttpClient.newCall(request).enqueue(object : Callback {

        when (weatherMain.lowercase()) {

            "clear" -> {            override fun onFailure(call: Call, e: IOException) {    private lateinit var okHttpClient: OkHttpClient

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

                startSimpleAnimation()                runOnUiThread {

            }

            "clouds" -> {                    Toast.makeText(this@MainActivity, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()    private var timeHandler: Handler? = nullimport androidx.appcompat.app.AppCompatActivityimport android.os.Bundle

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

                startFloatAnimation()                }

            }

            "rain", "drizzle" -> {            }    private var timeRunnable: Runnable? = null

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

                startRainAnimation()

            }

            "thunderstorm" -> {            override fun onResponse(call: Call, response: Response) {import androidx.core.app.ActivityCompat

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_stormy)

                startThunderAnimation()                if (response.isSuccessful) {

            }

            "snow" -> {                    val responseData = response.body?.string()    override fun onCreate(savedInstanceState: Bundle?) {

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_snowy)

                startSnowAnimation()                    if (responseData != null) {

            }

            "mist", "smoke", "haze", "dust", "fog" -> {                        val weatherData = Gson().fromJson(responseData, WeatherData::class.java)        super.onCreate(savedInstanceState)import com.apkfood.weatherapp.databinding.ActivityMainBindingimport android.os.Handlerimport android.content.pm.PackageManager

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

                startMistAnimation()                        runOnUiThread {

            }

            else -> {                            updateUI(weatherData)        binding = ActivityMainBinding.inflate(layoutInflater)

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

                startSimpleAnimation()                        }

            }

        }                    }        setContentView(binding.root)import com.google.android.gms.location.FusedLocationProviderClient



        // Update background color                }

        updateBackground(weatherMain)

    }            }



    private fun startSimpleAnimation() {        })

        val rotation = RotateAnimation(0f, 360f, 

            Animation.RELATIVE_TO_SELF, 0.5f,     }        okHttpClient = OkHttpClient()import com.google.android.gms.location.LocationServicesimport android.os.Looper

            Animation.RELATIVE_TO_SELF, 0.5f)

        rotation.duration = 10000

        rotation.repeatCount = Animation.INFINITE

        rotation.interpolator = LinearInterpolator()    private fun updateUI(weatherData: WeatherData) {        fetchWeatherData()

        binding.ivWeatherIcon.startAnimation(rotation)

    }        val weatherMain = weatherData.weather[0].main



    private fun startFloatAnimation() {        val temp = "${weatherData.main.temp.toInt()}°C"        startClock()import com.google.gson.Gson

        val floating = TranslateAnimation(0f, 0f, -30f, 30f)

        floating.duration = 3000        val description = weatherData.weather[0].description.replaceFirstChar { 

        floating.repeatCount = Animation.INFINITE

        floating.repeatMode = Animation.REVERSE            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()     }

        floating.interpolator = AccelerateDecelerateInterpolator()

        binding.ivWeatherIcon.startAnimation(floating)        }

    }

        val humidity = "${weatherData.main.humidity}%"import okhttp3.*import android.view.animation.*import android.location.Locationimport android.annotation.SuppressLint

    private fun startRainAnimation() {

        val drop = TranslateAnimation(0f, 0f, -20f, 20f)        val windSpeed = "${weatherData.wind.speed} m/s"

        drop.duration = 1000

        drop.repeatCount = Animation.INFINITE        val pressure = "${weatherData.main.pressure} hPa"    private fun fetchWeatherData() {

        drop.repeatMode = Animation.RESTART

        drop.interpolator = AccelerateInterpolator()        val location = weatherData.name

        binding.ivWeatherIcon.startAnimation(drop)

    }        val url = "https://api.openweathermap.org/data/2.5/weather?lat=-6.2088&lon=106.8456&appid=${Constants.API_KEY}&units=metric"import java.io.IOException



    private fun startThunderAnimation() {        // Update text

        val shake = TranslateAnimation(-5f, 5f, -5f, 5f)

        shake.duration = 100        binding.tvTemperature.text = temp        val request = Request.Builder().url(url).build()

        shake.repeatCount = Animation.INFINITE

        shake.interpolator = CycleInterpolator(2f)        binding.tvDescription.text = description

        binding.ivWeatherIcon.startAnimation(shake)

    }        binding.tvHumidity.text = "Humidity: $humidity"import java.text.SimpleDateFormatimport android.widget.Toast



    private fun startSnowAnimation() {        binding.tvWindSpeed.text = "Wind: $windSpeed"

        val sway = TranslateAnimation(-15f, 15f, -15f, 15f)

        sway.duration = 2000        binding.tvPressure.text = "Pressure: $pressure"        okHttpClient.newCall(request).enqueue(object : Callback {

        sway.repeatCount = Animation.INFINITE

        sway.repeatMode = Animation.REVERSE        binding.tvLocation.text = location

        sway.interpolator = AccelerateDecelerateInterpolator()

        binding.ivWeatherIcon.startAnimation(sway)            override fun onFailure(call: Call, e: IOException) {import java.util.*

    }

        // Clear previous animations

    private fun startMistAnimation() {

        val fade = AlphaAnimation(0.3f, 1.0f)        binding.ivWeatherIcon.clearAnimation()                runOnUiThread {

        fade.duration = 1500

        fade.repeatCount = Animation.INFINITE

        fade.repeatMode = Animation.REVERSE

        fade.interpolator = AccelerateDecelerateInterpolator()        // Set weather icon and animation based on condition                    Toast.makeText(this@MainActivity, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()import kotlin.math.roundToIntimport androidx.appcompat.app.AppCompatActivityimport android.location.LocationManager

        binding.ivWeatherIcon.startAnimation(fade)

    }        when (weatherMain.lowercase()) {



    private fun updateBackground(weatherMain: String) {            "clear" -> {                }

        when (weatherMain.lowercase()) {

            "clear" -> binding.root.setBackgroundColor(Color.parseColor("#87CEEB"))                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

            "clouds" -> binding.root.setBackgroundColor(Color.parseColor("#708090"))

            "rain", "drizzle" -> binding.root.setBackgroundColor(Color.parseColor("#4682B4"))                startSunAnimation()            }

            "thunderstorm" -> binding.root.setBackgroundColor(Color.parseColor("#2F4F4F"))

            "snow" -> binding.root.setBackgroundColor(Color.parseColor("#F0F8FF"))            }

            "mist", "fog", "haze" -> binding.root.setBackgroundColor(Color.parseColor("#D3D3D3"))

            else -> binding.root.setBackgroundColor(Color.parseColor("#87CEEB"))            "clouds" -> {

        }

    }                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

}
                startCloudAnimation()            override fun onResponse(call: Call, response: Response) {class MainActivity : AppCompatActivity() {import androidx.core.app.ActivityCompat

            }

            "rain" -> {                if (response.isSuccessful) {

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

                startRainAnimation()                    val responseData = response.body?.string()    

            }

            "drizzle" -> {                    if (responseData != null) {

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

                startDrizzleAnimation()                        val weatherData = Gson().fromJson(responseData, WeatherData::class.java)    private lateinit var binding: ActivityMainBindingimport com.apkfood.weatherapp.databinding.ActivityMainBindingimport android.os.Bundleimport android.content.pm.PackageManager

            }

            "thunderstorm" -> {                        runOnUiThread {

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_stormy)

                startThunderstormAnimation()                            updateUI(weatherData)    private lateinit var fusedLocationClient: FusedLocationProviderClient

            }

            "snow" -> {                        }

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_snowy)

                startSnowAnimation()                    }    private val client = OkHttpClient()import com.google.android.gms.location.FusedLocationProviderClient

            }

            "mist", "smoke", "haze", "dust", "fog", "sand", "ash", "squall", "tornado" -> {                }

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

                startMistAnimation()            }    

            }

            else -> {        })

                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

                startSunAnimation()    }    private val timeHandler = Handler(Looper.getMainLooper())import com.google.android.gms.location.LocationServicesimport android.os.Handler

            }

        }



        // Update background based on weather    private fun updateUI(weatherData: WeatherData) {    private val timeRunnable = object : Runnable {

        updateBackground(weatherMain)

    }        val weatherMain = weatherData.weather[0].main



    private fun startSunAnimation() {        val temp = "${weatherData.main.temp.toInt()}°C"        override fun run() {import com.google.gson.Gson

        val animationSet = AnimationSet(true)

        val description = weatherData.weather[0].description.replaceFirstChar { 

        // Rotation animation

        val rotation = RotateAnimation(0f, 360f,             if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()             updateTime()

            Animation.RELATIVE_TO_SELF, 0.5f, 

            Animation.RELATIVE_TO_SELF, 0.5f)        }

        rotation.duration = 8000

        rotation.repeatCount = Animation.INFINITE        val humidity = "${weatherData.main.humidity}%"            timeHandler.postDelayed(this, 1000)import okhttp3.*import android.os.Looperimport android.location.Locationimport android.annotation.SuppressLint

        rotation.interpolator = LinearInterpolator()

        val windSpeed = "${weatherData.wind.speed} m/s"

        // Breathing scale animation

        val scale = ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f,        val pressure = "${weatherData.main.pressure} hPa"        }

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)        val location = weatherData.name

        scale.duration = 3000

        scale.repeatCount = Animation.INFINITE    }import java.io.IOException

        scale.repeatMode = Animation.REVERSE

        scale.interpolator = AccelerateDecelerateInterpolator()        // Update text



        // Glow effect (alpha)        binding.tvTemperature.text = temp    

        val glow = AlphaAnimation(0.8f, 1.0f)

        glow.duration = 2000        binding.tvDescription.text = description

        glow.repeatCount = Animation.INFINITE

        glow.repeatMode = Animation.REVERSE        binding.tvHumidity.text = "Humidity: $humidity"    override fun onCreate(savedInstanceState: Bundle?) {import java.text.SimpleDateFormatimport android.view.animation.*

        glow.interpolator = AccelerateDecelerateInterpolator()

        binding.tvWindSpeed.text = "Wind: $windSpeed"

        animationSet.addAnimation(rotation)

        animationSet.addAnimation(scale)        binding.tvPressure.text = "Pressure: $pressure"        super.onCreate(savedInstanceState)

        animationSet.addAnimation(glow)

        binding.tvLocation.text = location

        binding.ivWeatherIcon.startAnimation(animationSet)

    }        import java.util.*



    private fun startCloudAnimation() {        // Clear previous animations

        val animationSet = AnimationSet(true)

        binding.ivWeatherIcon.clearAnimation()        binding = ActivityMainBinding.inflate(layoutInflater)

        // Floating animation (Y translate)

        val floating = TranslateAnimation(0f, 0f, -20f, 20f)

        floating.duration = 4000

        floating.repeatCount = Animation.INFINITE        // Set weather icon and animation based on condition        setContentView(binding.root)import kotlin.math.roundToIntimport android.widget.Toastimport android.os.Bundle

        floating.repeatMode = Animation.REVERSE

        floating.interpolator = AccelerateDecelerateInterpolator()        when (weatherMain.lowercase()) {



        // Drifting animation (X translate)            "clear" -> {        

        val drifting = TranslateAnimation(-15f, 15f, 0f, 0f)

        drifting.duration = 6000                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

        drifting.repeatCount = Animation.INFINITE

        drifting.repeatMode = Animation.REVERSE                startSunAnimation()        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        drifting.interpolator = AccelerateDecelerateInterpolator()

            }

        // Morphing scale

        val morphing = ScaleAnimation(0.95f, 1.05f, 0.95f, 1.05f,            "clouds" -> {        

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

        morphing.duration = 5000

        morphing.repeatCount = Animation.INFINITE                startCloudAnimation()        requestLocationPermission()class MainActivity : AppCompatActivity() {import androidx.appcompat.app.AppCompatActivity

        morphing.repeatMode = Animation.REVERSE

        morphing.interpolator = AccelerateDecelerateInterpolator()            }



        animationSet.addAnimation(floating)            "rain" -> {        startTimeUpdates()

        animationSet.addAnimation(drifting)

        animationSet.addAnimation(morphing)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)



        binding.ivWeatherIcon.startAnimation(animationSet)                startRainAnimation()    }    

    }

            }

    private fun startRainAnimation() {

        val animationSet = AnimationSet(true)            "drizzle" -> {    



        // Rain drop animation (Y translate)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

        val drop = TranslateAnimation(0f, 0f, -30f, 30f)

        drop.duration = 1500                startDrizzleAnimation()    private fun requestLocationPermission() {    private lateinit var binding: ActivityMainBindingimport androidx.core.app.ActivityCompatimport android.os.Handlerimport android.content.pm.PackageManager

        drop.repeatCount = Animation.INFINITE

        drop.repeatMode = Animation.RESTART            }

        drop.interpolator = AccelerateInterpolator()

            "thunderstorm" -> {        if (ActivityCompat.checkSelfPermission(

        // Wind shake (X translate)

        val shake = TranslateAnimation(-3f, 3f, 0f, 0f)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_stormy)

        shake.duration = 300

        shake.repeatCount = Animation.INFINITE                startThunderstormAnimation()                this,    private lateinit var fusedLocationClient: FusedLocationProviderClient

        shake.interpolator = CycleInterpolator(8f)

            }

        // Impact splash (scale)

        val splash = ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f,            "snow" -> {                Manifest.permission.ACCESS_FINE_LOCATION

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_snowy)

        splash.duration = 800

        splash.repeatCount = Animation.INFINITE                startSnowAnimation()            ) != PackageManager.PERMISSION_GRANTED    private val client = OkHttpClient()import com.apkfood.weatherapp.databinding.ActivityMainBinding

        splash.repeatMode = Animation.REVERSE

        splash.interpolator = BounceInterpolator()            }



        // Intensity alpha            "mist", "smoke", "haze", "dust", "fog", "sand", "ash", "squall", "tornado" -> {        ) {

        val intensity = AlphaAnimation(0.7f, 1.0f)

        intensity.duration = 1000                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

        intensity.repeatCount = Animation.INFINITE

        intensity.repeatMode = Animation.REVERSE                startMistAnimation()            ActivityCompat.requestPermissions(    

        intensity.interpolator = AccelerateDecelerateInterpolator()

            }

        animationSet.addAnimation(drop)

        animationSet.addAnimation(shake)            else -> {                this,

        animationSet.addAnimation(splash)

        animationSet.addAnimation(intensity)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)



        binding.ivWeatherIcon.startAnimation(animationSet)                startSunAnimation()                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),    private val timeHandler = Handler(Looper.getMainLooper())import com.google.android.gms.location.FusedLocationProviderClientimport android.os.Looper

    }

            }

    private fun startDrizzleAnimation() {

        val animationSet = AnimationSet(true)        }                100



        // Gentle fall (Y translate)

        val fall = TranslateAnimation(0f, 0f, -15f, 15f)

        fall.duration = 2500        // Update background based on weather            )    private val timeRunnable = object : Runnable {

        fall.repeatCount = Animation.INFINITE

        fall.repeatMode = Animation.RESTART        updateBackground(weatherMain)

        fall.interpolator = AccelerateDecelerateInterpolator()

    }        } else {

        // Soft sway (X translate)

        val sway = TranslateAnimation(-2f, 2f, 0f, 0f)

        sway.duration = 2000

        sway.repeatCount = Animation.INFINITE    private fun startSunAnimation() {            getCurrentLocation()        override fun run() {import com.google.android.gms.location.LocationServices

        sway.repeatMode = Animation.REVERSE

        sway.interpolator = AccelerateDecelerateInterpolator()        val animationSet = AnimationSet(true)



        // Subtle scale        }

        val scale = ScaleAnimation(0.95f, 1.02f, 0.95f, 1.02f,

            Animation.RELATIVE_TO_SELF, 0.5f,        // Rotation animation

            Animation.RELATIVE_TO_SELF, 0.5f)

        scale.duration = 3000        val rotation = RotateAnimation(0f, 360f,     }            updateTime()

        scale.repeatCount = Animation.INFINITE

        scale.repeatMode = Animation.REVERSE            Animation.RELATIVE_TO_SELF, 0.5f, 

        scale.interpolator = AccelerateDecelerateInterpolator()

            Animation.RELATIVE_TO_SELF, 0.5f)    

        // Soft alpha

        val softAlpha = AlphaAnimation(0.8f, 1.0f)        rotation.duration = 8000

        softAlpha.duration = 2000

        softAlpha.repeatCount = Animation.INFINITE        rotation.repeatCount = Animation.INFINITE    override fun onRequestPermissionsResult(            timeHandler.postDelayed(this, 1000)import com.google.gson.Gsonimport android.util.Logimport android.location.Locationimport android.annotation.SuppressLintimport android.annotation.SuppressLint

        softAlpha.repeatMode = Animation.REVERSE

        softAlpha.interpolator = AccelerateDecelerateInterpolator()        rotation.interpolator = LinearInterpolator()



        animationSet.addAnimation(fall)        requestCode: Int,

        animationSet.addAnimation(sway)

        animationSet.addAnimation(scale)        // Breathing scale animation

        animationSet.addAnimation(softAlpha)

        val scale = ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f,        permissions: Array<out String>,        }

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            Animation.RELATIVE_TO_SELF, 0.5f,



    private fun startThunderstormAnimation() {            Animation.RELATIVE_TO_SELF, 0.5f)        grantResults: IntArray

        val animationSet = AnimationSet(true)

        scale.duration = 3000

        // Lightning flash (alpha)

        val flash = AlphaAnimation(0.3f, 1.0f)        scale.repeatCount = Animation.INFINITE    ) {    }import okhttp3.*

        flash.duration = 150

        flash.repeatCount = Animation.INFINITE        scale.repeatMode = Animation.REVERSE

        flash.interpolator = AccelerateInterpolator()

        scale.interpolator = AccelerateDecelerateInterpolator()        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Thunder shake (both X and Y)

        val shakeX = TranslateAnimation(-5f, 5f, 0f, 0f)

        shakeX.duration = 100

        shakeX.repeatCount = Animation.INFINITE        // Glow effect (alpha)        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {    

        shakeX.interpolator = CycleInterpolator(20f)

        val glow = AlphaAnimation(0.8f, 1.0f)

        val shakeY = TranslateAnimation(0f, 0f, -5f, 5f)

        shakeY.duration = 120        glow.duration = 2000            getCurrentLocation()

        shakeY.repeatCount = Animation.INFINITE

        shakeY.interpolator = CycleInterpolator(15f)        glow.repeatCount = Animation.INFINITE



        // Electric rotation        glow.repeatMode = Animation.REVERSE        } else {    override fun onCreate(savedInstanceState: Bundle?) {import java.io.IOExceptionimport android.widget.Toast

        val rotation = RotateAnimation(-5f, 5f,

            Animation.RELATIVE_TO_SELF, 0.5f,        glow.interpolator = AccelerateDecelerateInterpolator()

            Animation.RELATIVE_TO_SELF, 0.5f)

        rotation.duration = 200            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()

        rotation.repeatCount = Animation.INFINITE

        rotation.repeatMode = Animation.REVERSE        animationSet.addAnimation(rotation)

        rotation.interpolator = CycleInterpolator(10f)

        animationSet.addAnimation(scale)        }        super.onCreate(savedInstanceState)

        // Dramatic scale

        val dramatic = ScaleAnimation(0.8f, 1.2f, 0.8f, 1.2f,        animationSet.addAnimation(glow)

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)    }

        dramatic.duration = 300

        dramatic.repeatCount = Animation.INFINITE        binding.ivWeatherIcon.startAnimation(animationSet)

        dramatic.repeatMode = Animation.REVERSE

        dramatic.interpolator = BounceInterpolator()    }            import java.text.SimpleDateFormat



        animationSet.addAnimation(flash)

        animationSet.addAnimation(shakeX)

        animationSet.addAnimation(shakeY)    private fun startCloudAnimation() {    private fun getCurrentLocation() {

        animationSet.addAnimation(rotation)

        animationSet.addAnimation(dramatic)        val animationSet = AnimationSet(true)



        binding.ivWeatherIcon.startAnimation(animationSet)        if (ActivityCompat.checkSelfPermission(        binding = ActivityMainBinding.inflate(layoutInflater)

    }

        // Floating animation (Y translate)

    private fun startSnowAnimation() {

        val animationSet = AnimationSet(true)        val floating = TranslateAnimation(0f, 0f, -20f, 20f)                this,



        // Swaying fall (Y translate with curve)        floating.duration = 4000

        val fall = TranslateAnimation(0f, 0f, -25f, 25f)

        fall.duration = 4000        floating.repeatCount = Animation.INFINITE                Manifest.permission.ACCESS_FINE_LOCATION        setContentView(binding.root)import java.util.*import androidx.appcompat.app.AppCompatActivityimport android.location.LocationManager

        fall.repeatCount = Animation.INFINITE

        fall.repeatMode = Animation.RESTART        floating.repeatMode = Animation.REVERSE

        fall.interpolator = AccelerateDecelerateInterpolator()

        floating.interpolator = AccelerateDecelerateInterpolator()            ) != PackageManager.PERMISSION_GRANTED

        // Sway motion (X translate)

        val sway = TranslateAnimation(-20f, 20f, 0f, 0f)

        sway.duration = 3000

        sway.repeatCount = Animation.INFINITE        // Drifting animation (X translate)        ) {        

        sway.repeatMode = Animation.REVERSE

        sway.interpolator = AccelerateDecelerateInterpolator()        val drifting = TranslateAnimation(-15f, 15f, 0f, 0f)



        // Crystalline rotation        drifting.duration = 6000            return

        val rotate = RotateAnimation(0f, 180f,

            Animation.RELATIVE_TO_SELF, 0.5f,        drifting.repeatCount = Animation.INFINITE

            Animation.RELATIVE_TO_SELF, 0.5f)

        rotate.duration = 6000        drifting.repeatMode = Animation.REVERSE        }        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)import kotlin.math.roundToInt

        rotate.repeatCount = Animation.INFINITE

        rotate.interpolator = LinearInterpolator()        drifting.interpolator = AccelerateDecelerateInterpolator()



        // Graceful scale        

        val scale = ScaleAnimation(0.9f, 1.05f, 0.9f, 1.05f,

            Animation.RELATIVE_TO_SELF, 0.5f,        // Morphing scale

            Animation.RELATIVE_TO_SELF, 0.5f)

        scale.duration = 4000        val morphing = ScaleAnimation(0.95f, 1.05f, 0.95f, 1.05f,        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->        

        scale.repeatCount = Animation.INFINITE

        scale.repeatMode = Animation.REVERSE            Animation.RELATIVE_TO_SELF, 0.5f,

        scale.interpolator = AccelerateDecelerateInterpolator()

            Animation.RELATIVE_TO_SELF, 0.5f)            if (location != null) {

        // Fade effect

        val fade = AlphaAnimation(0.7f, 1.0f)        morphing.duration = 5000

        fade.duration = 3000

        fade.repeatCount = Animation.INFINITE        morphing.repeatCount = Animation.INFINITE                fetchWeather(location.latitude, location.longitude)        requestLocationPermission()import androidx.core.app.ActivityCompat

        fade.repeatMode = Animation.REVERSE

        fade.interpolator = AccelerateDecelerateInterpolator()        morphing.repeatMode = Animation.REVERSE



        animationSet.addAnimation(fall)        morphing.interpolator = AccelerateDecelerateInterpolator()            } else {

        animationSet.addAnimation(sway)

        animationSet.addAnimation(rotate)

        animationSet.addAnimation(scale)

        animationSet.addAnimation(fade)        animationSet.addAnimation(floating)                fetchWeather(-6.2088, 106.8456)        startTimeUpdates()



        binding.ivWeatherIcon.startAnimation(animationSet)        animationSet.addAnimation(drifting)

    }

        animationSet.addAnimation(morphing)            }

    private fun startMistAnimation() {

        val animationSet = AnimationSet(true)



        // Mysterious drift (X translate)        binding.ivWeatherIcon.startAnimation(animationSet)        }    }class MainActivity : AppCompatActivity() {

        val driftX = TranslateAnimation(-30f, 30f, 0f, 0f)

        driftX.duration = 6000    }

        driftX.repeatCount = Animation.INFINITE

        driftX.repeatMode = Animation.REVERSE    }

        driftX.interpolator = AccelerateDecelerateInterpolator()

    private fun startRainAnimation() {

        // Vertical drift (Y translate)

        val driftY = TranslateAnimation(0f, 0f, -15f, 15f)        val animationSet = AnimationSet(true)        

        driftY.duration = 5000

        driftY.repeatCount = Animation.INFINITE

        driftY.repeatMode = Animation.REVERSE

        driftY.interpolator = AccelerateDecelerateInterpolator()        // Rain drop animation (Y translate)    private fun fetchWeather(lat: Double, lon: Double) {



        // Ethereal breathing (scale)        val drop = TranslateAnimation(0f, 0f, -30f, 30f)

        val breathing = ScaleAnimation(0.85f, 1.15f, 0.85f, 1.15f,

            Animation.RELATIVE_TO_SELF, 0.5f,        drop.duration = 1500        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=${Constants.API_KEY}&units=metric"    private fun requestLocationPermission() {    import androidx.core.content.ContextCompatimport android.os.Bundleimport android.os.Bundleimport android.os.Bundle

            Animation.RELATIVE_TO_SELF, 0.5f)

        breathing.duration = 4000        drop.repeatCount = Animation.INFINITE

        breathing.repeatCount = Animation.INFINITE

        breathing.repeatMode = Animation.REVERSE        drop.repeatMode = Animation.RESTART        

        breathing.interpolator = AccelerateDecelerateInterpolator()

        drop.interpolator = AccelerateInterpolator()

        // Mystical fade (alpha)

        val mystical = AlphaAnimation(0.4f, 0.9f)        val request = Request.Builder()        if (ActivityCompat.checkSelfPermission(

        mystical.duration = 3000

        mystical.repeatCount = Animation.INFINITE        // Wind shake (X translate)

        mystical.repeatMode = Animation.REVERSE

        mystical.interpolator = AccelerateDecelerateInterpolator()        val shake = TranslateAnimation(-3f, 3f, 0f, 0f)            .url(url)



        animationSet.addAnimation(driftX)        shake.duration = 300

        animationSet.addAnimation(driftY)

        animationSet.addAnimation(breathing)        shake.repeatCount = Animation.INFINITE            .build()                this,    private lateinit var binding: ActivityMainBinding

        animationSet.addAnimation(mystical)

        shake.interpolator = CycleInterpolator(8f)

        binding.ivWeatherIcon.startAnimation(animationSet)

    }        



    private fun startClock() {        // Impact splash (scale)

        timeHandler = Handler(Looper.getMainLooper())

        timeRunnable = object : Runnable {        val splash = ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f,        client.newCall(request).enqueue(object : Callback {                Manifest.permission.ACCESS_FINE_LOCATION

            override fun run() {

                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())            Animation.RELATIVE_TO_SELF, 0.5f,

                val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

                val currentTime = Date()            Animation.RELATIVE_TO_SELF, 0.5f)            override fun onFailure(call: Call, e: IOException) {

                

                binding.tvCurrentTime.text = timeFormat.format(currentTime)        splash.duration = 800

                binding.tvDate.text = dateFormat.format(currentTime)

                        splash.repeatCount = Animation.INFINITE                runOnUiThread {            ) != PackageManager.PERMISSION_GRANTED    private lateinit var fusedLocationClient: FusedLocationProviderClientimport androidx.databinding.DataBindingUtil

                timeHandler?.postDelayed(this, 1000)

            }        splash.repeatMode = Animation.REVERSE

        }

        timeRunnable?.let { timeHandler?.post(it) }        splash.interpolator = BounceInterpolator()                    Toast.makeText(this@MainActivity, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()

    }



    private fun updateBackground(weatherMain: String) {

        when (weatherMain.lowercase()) {        // Intensity alpha                }        ) {

            "clear" -> binding.root.setBackgroundColor(Color.parseColor("#87CEEB"))

            "clouds" -> binding.root.setBackgroundColor(Color.parseColor("#708090"))        val intensity = AlphaAnimation(0.7f, 1.0f)

            "rain", "drizzle" -> binding.root.setBackgroundColor(Color.parseColor("#4682B4"))

            "thunderstorm" -> binding.root.setBackgroundColor(Color.parseColor("#2F4F4F"))        intensity.duration = 1000            }

            "snow" -> binding.root.setBackgroundColor(Color.parseColor("#F0F8FF"))

            "mist", "fog", "haze" -> binding.root.setBackgroundColor(Color.parseColor("#D3D3D3"))        intensity.repeatCount = Animation.INFINITE

            else -> binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))

        }        intensity.repeatMode = Animation.REVERSE                        ActivityCompat.requestPermissions(    private val client = OkHttpClient()

    }

        intensity.interpolator = AccelerateDecelerateInterpolator()

    override fun onDestroy() {

        super.onDestroy()            override fun onResponse(call: Call, response: Response) {

        timeRunnable?.let { timeHandler?.removeCallbacks(it) }

        timeHandler = null        animationSet.addAnimation(drop)

        binding.ivWeatherIcon.clearAnimation()

    }        animationSet.addAnimation(shake)                response.body?.let { responseBody ->                this,

}
        animationSet.addAnimation(splash)

        animationSet.addAnimation(intensity)                    val weatherData = Gson().fromJson(responseBody.string(), WeatherData::class.java)



        binding.ivWeatherIcon.startAnimation(animationSet)                    runOnUiThread {                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),    import com.google.android.gms.location.FusedLocationProviderClientimport android.os.Handler

    }

                        updateUI(weatherData)

    private fun startDrizzleAnimation() {

        val animationSet = AnimationSet(true)                    }                100



        // Gentle fall (Y translate)                }

        val fall = TranslateAnimation(0f, 0f, -15f, 15f)

        fall.duration = 2500            }            )    private val timeHandler = Handler(Looper.getMainLooper())

        fall.repeatCount = Animation.INFINITE

        fall.repeatMode = Animation.RESTART        })

        fall.interpolator = AccelerateDecelerateInterpolator()

    }        } else {

        // Soft sway (X translate)

        val sway = TranslateAnimation(-2f, 2f, 0f, 0f)    

        sway.duration = 2000

        sway.repeatCount = Animation.INFINITE    private fun updateUI(weatherData: WeatherData) {            getCurrentLocation()    private val timeRunnable = object : Runnable {import com.google.android.gms.location.LocationServices

        sway.repeatMode = Animation.REVERSE

        sway.interpolator = AccelerateDecelerateInterpolator()        binding.apply {



        // Subtle scale            tvTemperature.text = "${weatherData.main.temp.roundToInt()}°"        }

        val scale = ScaleAnimation(0.95f, 1.02f, 0.95f, 1.02f,

            Animation.RELATIVE_TO_SELF, 0.5f,            tvCityName.text = weatherData.name

            Animation.RELATIVE_TO_SELF, 0.5f)

        scale.duration = 3000            tvDescription.text = weatherData.weather[0].description.replaceFirstChar {     }        override fun run() {

        scale.repeatCount = Animation.INFINITE

        scale.repeatMode = Animation.REVERSE                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 

        scale.interpolator = AccelerateDecelerateInterpolator()

            }    

        // Soft alpha

        val softAlpha = AlphaAnimation(0.8f, 1.0f)            tvHumidity.text = "${weatherData.main.humidity}%"

        softAlpha.duration = 2000

        softAlpha.repeatCount = Animation.INFINITE            tvWindSpeed.text = "${weatherData.wind.speed} m/s"    override fun onRequestPermissionsResult(            updateTime()import com.google.gson.Gsonimport android.os.Looperimport android.os.Handlerimport android.os.Handler

        softAlpha.repeatMode = Animation.REVERSE

        softAlpha.interpolator = AccelerateDecelerateInterpolator()            tvFeelsLike.text = "${weatherData.main.feels_like.roundToInt()}°"



        animationSet.addAnimation(fall)            tvPressure.text = "${weatherData.main.pressure} hPa"        requestCode: Int,

        animationSet.addAnimation(sway)

        animationSet.addAnimation(scale)            tvVisibility.text = "${(weatherData.visibility / 1000.0).roundToInt()} km"

        animationSet.addAnimation(softAlpha)

                    permissions: Array<out String>,            timeHandler.postDelayed(this, 1000)

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            val iconCode = weatherData.weather[0].icon



    private fun startThunderstormAnimation() {            setWeatherIcon(iconCode)        grantResults: IntArray

        val animationSet = AnimationSet(true)

            setBackgroundAndAnimation(weatherData.weather[0].main, iconCode)

        // Lightning flash (alpha)

        val flash = AlphaAnimation(0.3f, 1.0f)        }    ) {        }import okhttp3.*

        flash.duration = 150

        flash.repeatCount = Animation.INFINITE    }

        flash.interpolator = AccelerateInterpolator()

            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Thunder shake (both X and Y)

        val shakeX = TranslateAnimation(-5f, 5f, 0f, 0f)    private fun setWeatherIcon(iconCode: String) {

        shakeX.duration = 100

        shakeX.repeatCount = Animation.INFINITE        val iconResource = when (iconCode) {        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {    }

        shakeX.interpolator = CycleInterpolator(20f)

            "01d" -> R.drawable.ic_sun

        val shakeY = TranslateAnimation(0f, 0f, -5f, 5f)

        shakeY.duration = 120            "01n" -> R.drawable.ic_moon            getCurrentLocation()

        shakeY.repeatCount = Animation.INFINITE

        shakeY.interpolator = CycleInterpolator(15f)            "02d", "02n" -> R.drawable.ic_cloud_sun



        // Electric rotation            "03d", "03n", "04d", "04n" -> R.drawable.ic_clouds        } else {    import java.io.IOExceptionimport android.util.Log

        val rotation = RotateAnimation(-5f, 5f,

            Animation.RELATIVE_TO_SELF, 0.5f,            "09d", "09n", "10d", "10n" -> R.drawable.ic_rain

            Animation.RELATIVE_TO_SELF, 0.5f)

        rotation.duration = 200            "11d", "11n" -> R.drawable.ic_thunder            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()

        rotation.repeatCount = Animation.INFINITE

        rotation.repeatMode = Animation.REVERSE            "13d", "13n" -> R.drawable.ic_snow

        rotation.interpolator = CycleInterpolator(10f)

            "50d", "50n" -> R.drawable.ic_mist        }    override fun onCreate(savedInstanceState: Bundle?) {

        // Dramatic scale

        val dramatic = ScaleAnimation(0.8f, 1.2f, 0.8f, 1.2f,            else -> R.drawable.ic_sun

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)        }    }

        dramatic.duration = 300

        dramatic.repeatCount = Animation.INFINITE        binding.ivWeatherIcon.setImageResource(iconResource)

        dramatic.repeatMode = Animation.REVERSE

        dramatic.interpolator = BounceInterpolator()    }            super.onCreate(savedInstanceState)import java.text.SimpleDateFormat



        animationSet.addAnimation(flash)    

        animationSet.addAnimation(shakeX)

        animationSet.addAnimation(shakeY)    private fun setBackgroundAndAnimation(weatherMain: String, iconCode: String) {    private fun getCurrentLocation() {

        animationSet.addAnimation(rotation)

        animationSet.addAnimation(dramatic)        val backgroundResource = when {



        binding.ivWeatherIcon.startAnimation(animationSet)            iconCode.contains("d") -> when (weatherMain.lowercase()) {        if (ActivityCompat.checkSelfPermission(        

    }

                "clear" -> R.drawable.bg_sunny

    private fun startSnowAnimation() {

        val animationSet = AnimationSet(true)                "clouds" -> R.drawable.bg_cloudy                this,



        // Swaying fall (Y translate with curve)                "rain", "drizzle" -> R.drawable.bg_rainy

        val fall = TranslateAnimation(0f, 0f, -25f, 25f)

        fall.duration = 4000                "thunderstorm" -> R.drawable.bg_storm                Manifest.permission.ACCESS_FINE_LOCATION        binding = ActivityMainBinding.inflate(layoutInflater)import java.util.*import android.widget.Toastimport android.os.Looperimport android.os.Looper

        fall.repeatCount = Animation.INFINITE

        fall.repeatMode = Animation.RESTART                "snow" -> R.drawable.bg_snow

        fall.interpolator = AccelerateDecelerateInterpolator()

                "mist", "fog", "haze" -> R.drawable.bg_mist            ) != PackageManager.PERMISSION_GRANTED

        // Sway motion (X translate)

        val sway = TranslateAnimation(-20f, 20f, 0f, 0f)                else -> R.drawable.bg_sunny

        sway.duration = 3000

        sway.repeatCount = Animation.INFINITE            }        ) {        setContentView(binding.root)

        sway.repeatMode = Animation.REVERSE

        sway.interpolator = AccelerateDecelerateInterpolator()            else -> R.drawable.bg_night



        // Crystalline rotation        }            return

        val rotate = RotateAnimation(0f, 180f,

            Animation.RELATIVE_TO_SELF, 0.5f,        

            Animation.RELATIVE_TO_SELF, 0.5f)

        rotate.duration = 6000        binding.root.background = resources.getDrawable(backgroundResource, theme)        }        import java.util.concurrent.TimeUnit

        rotate.repeatCount = Animation.INFINITE

        rotate.interpolator = LinearInterpolator()        startAdvancedAnimation(weatherMain.lowercase(), iconCode.contains("d"))



        // Graceful scale    }        

        val scale = ScaleAnimation(0.9f, 1.05f, 0.9f, 1.05f,

            Animation.RELATIVE_TO_SELF, 0.5f,    

            Animation.RELATIVE_TO_SELF, 0.5f)

        scale.duration = 4000    private fun startAdvancedAnimation(weatherType: String, isDayTime: Boolean) {        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        scale.repeatCount = Animation.INFINITE

        scale.repeatMode = Animation.REVERSE        when (weatherType) {

        scale.interpolator = AccelerateDecelerateInterpolator()

            "clear" -> if (isDayTime) startSunAnimation() else startMoonAnimation()            if (location != null) {

        // Fade effect

        val fade = AlphaAnimation(0.7f, 1.0f)            "clouds" -> startCloudAnimation()

        fade.duration = 3000

        fade.repeatCount = Animation.INFINITE            "rain" -> startRainAnimation()                fetchWeather(location.latitude, location.longitude)        import com.apkfood.weatherapp.databinding.ActivityMainBindingimport androidx.appcompat.app.AppCompatActivity

        fade.repeatMode = Animation.REVERSE

        fade.interpolator = AccelerateDecelerateInterpolator()            "drizzle" -> startDrizzleAnimation()



        animationSet.addAnimation(fall)            "thunderstorm" -> startThunderstormAnimation()            } else {

        animationSet.addAnimation(sway)

        animationSet.addAnimation(rotate)            "snow" -> startSnowAnimation()

        animationSet.addAnimation(scale)

        animationSet.addAnimation(fade)            "mist", "fog", "haze" -> startMistAnimation()                fetchWeather(-6.2088, 106.8456)        requestLocationPermission()



        binding.ivWeatherIcon.startAnimation(animationSet)            else -> if (isDayTime) startSunAnimation() else startMoonAnimation()

    }

        }            }

    private fun startMistAnimation() {

        val animationSet = AnimationSet(true)    }



        // Mysterious drift (X translate)            }        startTimeUpdates()import android.view.animation.*

        val driftX = TranslateAnimation(-30f, 30f, 0f, 0f)

        driftX.duration = 6000    // SOPHISTICATED ANIMATIONS START HERE

        driftX.repeatCount = Animation.INFINITE

        driftX.repeatMode = Animation.REVERSE        }

        driftX.interpolator = AccelerateDecelerateInterpolator()

    private fun startSunAnimation() {

        // Vertical drift (Y translate)

        val driftY = TranslateAnimation(0f, 0f, -15f, 15f)        val rotateAnimation = RotateAnimation(        }

        driftY.duration = 5000

        driftY.repeatCount = Animation.INFINITE            0f, 360f,

        driftY.repeatMode = Animation.REVERSE

        driftY.interpolator = AccelerateDecelerateInterpolator()            Animation.RELATIVE_TO_SELF, 0.5f,    private fun fetchWeather(lat: Double, lon: Double) {



        // Ethereal breathing (scale)            Animation.RELATIVE_TO_SELF, 0.5f

        val breathing = ScaleAnimation(0.85f, 1.15f, 0.85f, 1.15f,

            Animation.RELATIVE_TO_SELF, 0.5f,        ).apply {        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=${Constants.API_KEY}&units=metric"    import androidx.core.app.ActivityCompatimport android.util.Logimport android.util.Log

            Animation.RELATIVE_TO_SELF, 0.5f)

        breathing.duration = 4000            duration = 20000

        breathing.repeatCount = Animation.INFINITE

        breathing.repeatMode = Animation.REVERSE            repeatCount = Animation.INFINITE        

        breathing.interpolator = AccelerateDecelerateInterpolator()

            interpolator = LinearInterpolator()

        // Mystical fade (alpha)

        val mystical = AlphaAnimation(0.4f, 0.9f)        }        val request = Request.Builder()    private fun requestLocationPermission() {

        mystical.duration = 3000

        mystical.repeatCount = Animation.INFINITE        

        mystical.repeatMode = Animation.REVERSE

        mystical.interpolator = AccelerateDecelerateInterpolator()        val breathingScale = ScaleAnimation(            .url(url)



        animationSet.addAnimation(driftX)            1.0f, 1.15f, 1.0f, 1.15f,

        animationSet.addAnimation(driftY)

        animationSet.addAnimation(breathing)            Animation.RELATIVE_TO_SELF, 0.5f,            .build()        if (ActivityCompat.checkSelfPermission(class MainActivity : AppCompatActivity() {

        animationSet.addAnimation(mystical)

            Animation.RELATIVE_TO_SELF, 0.5f

        binding.ivWeatherIcon.startAnimation(animationSet)

    }        ).apply {        



    private fun startClock() {            duration = 3000

        timeHandler = Handler(Looper.getMainLooper())

        timeRunnable = object : Runnable {            repeatCount = Animation.INFINITE        client.newCall(request).enqueue(object : Callback {                this,

            override fun run() {

                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())            repeatMode = Animation.REVERSE

                val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

                val currentTime = Date()            interpolator = AccelerateDecelerateInterpolator()            override fun onFailure(call: Call, e: IOException) {

                

                binding.tvCurrentTime.text = timeFormat.format(currentTime)        }

                binding.tvDate.text = dateFormat.format(currentTime)

                                        runOnUiThread {                Manifest.permission.ACCESS_FINE_LOCATION    private lateinit var binding: ActivityMainBindingimport androidx.core.content.ContextCompat

                timeHandler?.postDelayed(this, 1000)

            }        val glowEffect = AlphaAnimation(0.8f, 1.0f).apply {

        }

        timeRunnable?.let { timeHandler?.post(it) }            duration = 2000                    Toast.makeText(this@MainActivity, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()

    }

            repeatCount = Animation.INFINITE

    private fun updateBackground(weatherMain: String) {

        when (weatherMain.lowercase()) {            repeatMode = Animation.REVERSE                }            ) != PackageManager.PERMISSION_GRANTED

            "clear" -> binding.root.setBackgroundColor(Color.parseColor("#87CEEB"))

            "clouds" -> binding.root.setBackgroundColor(Color.parseColor("#708090"))        }

            "rain", "drizzle" -> binding.root.setBackgroundColor(Color.parseColor("#4682B4"))

            "thunderstorm" -> binding.root.setBackgroundColor(Color.parseColor("#2F4F4F"))                    }

            "snow" -> binding.root.setBackgroundColor(Color.parseColor("#F0F8FF"))

            "mist", "fog", "haze" -> binding.root.setBackgroundColor(Color.parseColor("#D3D3D3"))        val animationSet = AnimationSet(false).apply {

            else -> binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))

        }            addAnimation(rotateAnimation)                    ) {    private lateinit var fusedLocationClient: FusedLocationProviderClient

    }

            addAnimation(breathingScale)

    override fun onDestroy() {

        super.onDestroy()            addAnimation(glowEffect)            override fun onResponse(call: Call, response: Response) {

        timeRunnable?.let { timeHandler?.removeCallbacks(it) }

        timeHandler = null        }

        binding.ivWeatherIcon.clearAnimation()

    }                        response.body?.let { responseBody ->            ActivityCompat.requestPermissions(

}
        binding.ivWeatherIcon.startAnimation(animationSet)

    }                    val weatherData = Gson().fromJson(responseBody.string(), WeatherData::class.java)

    

    private fun startMoonAnimation() {                    runOnUiThread {                this,    private lateinit var timeHandler: Handlerimport androidx.databinding.DataBindingUtilimport android.view.Viewimport android.view.View

        val floatY = TranslateAnimation(

            0f, 0f, 0f, -40f                        updateUI(weatherData)

        ).apply {

            duration = 5000                    }                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE                }

            interpolator = AccelerateDecelerateInterpolator()

        }            }                100    private lateinit var timeRunnable: Runnable

        

        val floatX = TranslateAnimation(        })

            -15f, 15f, 0f, 0f

        ).apply {    }            )

            duration = 7000

            repeatCount = Animation.INFINITE    

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()    private fun updateUI(weatherData: WeatherData) {        } else {    import com.google.android.gms.location.FusedLocationProviderClient

        }

                binding.apply {

        val mysticalGlow = AlphaAnimation(0.6f, 1.0f).apply {

            duration = 3500            tvTemperature.text = "${weatherData.main.temp.roundToInt()}°"            getCurrentLocation()

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            tvCityName.text = weatherData.name

        }

                    tvDescription.text = weatherData.weather[0].description.replaceFirstChar {         }    companion object {

        val animationSet = AnimationSet(false).apply {

            addAnimation(floatY)                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 

            addAnimation(floatX)

            addAnimation(mysticalGlow)            }    }

        }

                    tvHumidity.text = "${weatherData.main.humidity}%"

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            tvWindSpeed.text = "${weatherData.wind.speed} m/s"            private const val LOCATION_PERMISSION_REQUEST_CODE = 1import com.google.android.gms.location.LocationServicesimport android.view.animation.*import android.view.animation.*

    

    private fun startCloudAnimation() {            tvFeelsLike.text = "${weatherData.main.feels_like.roundToInt()}°"

        val driftAnimation = TranslateAnimation(

            -60f, 60f, 0f, 0f            tvPressure.text = "${weatherData.main.pressure} hPa"    override fun onRequestPermissionsResult(

        ).apply {

            duration = 12000            tvVisibility.text = "${(weatherData.visibility / 1000.0).roundToInt()} km"

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE                    requestCode: Int,    }

            interpolator = AccelerateDecelerateInterpolator()

        }            val iconCode = weatherData.weather[0].icon

        

        val morphScale = ScaleAnimation(            setWeatherIcon(iconCode)        permissions: Array<out String>,

            0.85f, 1.15f, 0.85f, 1.15f,

            Animation.RELATIVE_TO_SELF, 0.5f,            setBackgroundAndAnimation(weatherData.weather[0].main, iconCode)

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {        }        grantResults: IntArray    import com.google.gson.Gson

            duration = 6000

            repeatCount = Animation.INFINITE    }

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()        ) {

        }

            private fun setWeatherIcon(iconCode: String) {

        val subtleFloat = TranslateAnimation(

            0f, 0f, -20f, 20f        val iconResource = when (iconCode) {        super.onRequestPermissionsResult(requestCode, permissions, grantResults)    override fun onCreate(savedInstanceState: Bundle?) {

        ).apply {

            duration = 8000            "01d" -> R.drawable.ic_sun

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            "01n" -> R.drawable.ic_moon        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            interpolator = AccelerateDecelerateInterpolator()

        }            "02d", "02n" -> R.drawable.ic_cloud_sun

        

        val animationSet = AnimationSet(false).apply {            "03d", "03n", "04d", "04n" -> R.drawable.ic_clouds            getCurrentLocation()        super.onCreate(savedInstanceState)import okhttp3.*import android.widget.Toastimport android.widget.Toast

            addAnimation(driftAnimation)

            addAnimation(morphScale)            "09d", "09n", "10d", "10n" -> R.drawable.ic_rain

            addAnimation(subtleFloat)

        }            "11d", "11n" -> R.drawable.ic_thunder        } else {

        

        binding.ivWeatherIcon.startAnimation(animationSet)            "13d", "13n" -> R.drawable.ic_snow

    }

                "50d", "50n" -> R.drawable.ic_mist            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    private fun startRainAnimation() {

        val intenseDrop = TranslateAnimation(            else -> R.drawable.ic_sun

            0f, 0f, -30f, 30f

        ).apply {        }        }

            duration = 400

            repeatCount = Animation.INFINITE        binding.ivWeatherIcon.setImageResource(iconResource)

            repeatMode = Animation.REVERSE

            interpolator = BounceInterpolator()    }    }        import java.io.IOException

        }

            

        val windShake = TranslateAnimation(

            -15f, 15f, -8f, 8f    private fun setBackgroundAndAnimation(weatherMain: String, iconCode: String) {    

        ).apply {

            duration = 150        val backgroundResource = when {

            repeatCount = Animation.INFINITE

            interpolator = CycleInterpolator(1f)            iconCode.contains("d") -> when (weatherMain.lowercase()) {    private fun getCurrentLocation() {        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        }

                        "clear" -> R.drawable.bg_sunny

        val rainIntensity = AlphaAnimation(0.5f, 1.0f).apply {

            duration = 600                "clouds" -> R.drawable.bg_cloudy        if (ActivityCompat.checkSelfPermission(

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE                "rain", "drizzle" -> R.drawable.bg_rainy

        }

                        "thunderstorm" -> R.drawable.bg_storm                this,        import java.text.SimpleDateFormatimport androidx.appcompat.app.AppCompatActivityimport androidx.appcompat.app.AppCompatActivity

        val impactScale = ScaleAnimation(

            0.9f, 1.1f, 0.9f, 1.1f,                "snow" -> R.drawable.bg_snow

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f                "mist", "fog", "haze" -> R.drawable.bg_mist                Manifest.permission.ACCESS_FINE_LOCATION

        ).apply {

            duration = 300                else -> R.drawable.bg_sunny

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            }            ) != PackageManager.PERMISSION_GRANTED        startLiveTime()

            interpolator = BounceInterpolator()

        }            else -> R.drawable.bg_night

        

        val animationSet = AnimationSet(false).apply {        }        ) {

            addAnimation(intenseDrop)

            addAnimation(windShake)        

            addAnimation(rainIntensity)

            addAnimation(impactScale)        binding.root.background = resources.getDrawable(backgroundResource, theme)            return        requestLocationPermission()import java.util.*

        }

                startWeatherAnimation(weatherMain.lowercase(), iconCode.contains("d"))

        binding.ivWeatherIcon.startAnimation(animationSet)

    }    }        }

    

    private fun startDrizzleAnimation() {    

        val gentleDrop = TranslateAnimation(

            -8f, 8f, -20f, 20f    private fun startWeatherAnimation(weatherType: String, isDayTime: Boolean) {                

        ).apply {

            duration = 2500        when (weatherType) {

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            "clear" -> if (isDayTime) startSunAnimation() else startMoonAnimation()        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

            interpolator = AccelerateDecelerateInterpolator()

        }            "clouds" -> startCloudAnimation()

        

        val softAlpha = AlphaAnimation(0.7f, 1.0f).apply {            "rain" -> startRainAnimation()            if (location != null) {        binding.btnSearch.setOnClickListener {import java.util.concurrent.TimeUnitimport androidx.core.content.ContextCompatimport androidx.core.content.ContextCompat

            duration = 2000

            repeatCount = Animation.INFINITE            "drizzle" -> startDrizzleAnimation()

            repeatMode = Animation.REVERSE

        }            "thunderstorm" -> startThunderstormAnimation()                fetchWeather(location.latitude, location.longitude)

        

        val subtleScale = ScaleAnimation(            "snow" -> startSnowAnimation()

            0.95f, 1.05f, 0.95f, 1.05f,

            Animation.RELATIVE_TO_SELF, 0.5f,            "mist", "fog", "haze" -> startMistAnimation()            } else {            val city = binding.etCity.text.toString().trim()

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {            else -> if (isDayTime) startSunAnimation() else startMoonAnimation()

            duration = 3000

            repeatCount = Animation.INFINITE        }                // Fallback to default location (Jakarta)

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()    }

        }

                            fetchWeather(-6.2088, 106.8456)            if (city.isNotEmpty()) {import com.apkfood.weatherapp.databinding.ActivityMainBinding

        val animationSet = AnimationSet(false).apply {

            addAnimation(gentleDrop)    // YouTube-quality sophisticated sun animation

            addAnimation(softAlpha)

            addAnimation(subtleScale)    private fun startSunAnimation() {            }

        }

                val rotateAnimation = RotateAnimation(

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            0f, 360f,        }                fetchWeatherByCity(city)

    

    private fun startThunderstormAnimation() {            Animation.RELATIVE_TO_SELF, 0.5f,

        val lightningFlash = AlphaAnimation(1.0f, 0.2f).apply {

            duration = 100            Animation.RELATIVE_TO_SELF, 0.5f    }

            repeatCount = 5

            repeatMode = Animation.REVERSE        ).apply {

        }

                    duration = 20000                } else {import android.view.animation.*import com.apkfood.weatherapp.databinding.ActivityMainBindingimport com.apkfood.weatherapp.databinding.ActivityMainBinding

        val thunderShake = TranslateAnimation(

            -20f, 20f, -20f, 20f            repeatCount = Animation.INFINITE

        ).apply {

            duration = 80            interpolator = LinearInterpolator()    private fun fetchWeather(lat: Double, lon: Double) {

            repeatCount = 15

            interpolator = CycleInterpolator(1f)        }

        }

                        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=${Constants.API_KEY}&units=metric"                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()

        val dramaticScale = ScaleAnimation(

            1.0f, 1.4f, 1.0f, 1.4f,        val breathingScale = ScaleAnimation(

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f            1.0f, 1.15f, 1.0f, 1.15f,        

        ).apply {

            duration = 250            Animation.RELATIVE_TO_SELF, 0.5f,

            repeatCount = 3

            repeatMode = Animation.REVERSE            Animation.RELATIVE_TO_SELF, 0.5f        val request = Request.Builder()            }import java.util.Date

            interpolator = AccelerateDecelerateInterpolator()

        }        ).apply {

        

        val electricRotate = RotateAnimation(            duration = 3000            .url(url)

            -5f, 5f,

            Animation.RELATIVE_TO_SELF, 0.5f,            repeatCount = Animation.INFINITE

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {            repeatMode = Animation.REVERSE            .build()        }

            duration = 100

            repeatCount = 8            interpolator = AccelerateDecelerateInterpolator()

            repeatMode = Animation.REVERSE

            interpolator = CycleInterpolator(1f)        }        

        }

                

        val animationSet = AnimationSet(false).apply {

            addAnimation(lightningFlash)        val glowEffect = AlphaAnimation(0.8f, 1.0f).apply {        client.newCall(request).enqueue(object : Callback {    }import com.apkfood.weatherapp.utils.Constantsimport com.apkfood.weatherapp.utils.Constants

            addAnimation(thunderShake)

            addAnimation(dramaticScale)            duration = 2000

            addAnimation(electricRotate)

        }            repeatCount = Animation.INFINITE            override fun onFailure(call: Call, e: IOException) {

        

        binding.ivWeatherIcon.startAnimation(animationSet)            repeatMode = Animation.REVERSE

        

        Handler(Looper.getMainLooper()).postDelayed({        }                runOnUiThread {    

            if (!isDestroyed) {

                startThunderstormAnimation()        

            }

        }, 4000)        val animationSet = AnimationSet(false).apply {                    Toast.makeText(this@MainActivity, "Failed to fetch weather data", Toast.LENGTH_SHORT).show()

    }

                addAnimation(rotateAnimation)

    private fun startSnowAnimation() {

        val snowFall = TranslateAnimation(            addAnimation(breathingScale)                }    private fun requestLocationPermission() {class MainActivity : AppCompatActivity() {

            0f, 0f, -40f, 40f

        ).apply {            addAnimation(glowEffect)

            duration = 5000

            repeatCount = Animation.INFINITE        }            }

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()        

        }

                binding.ivWeatherIcon.startAnimation(animationSet)                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) 

        val snowSway = TranslateAnimation(

            -25f, 25f, 0f, 0f    }

        ).apply {

            duration = 7000                override fun onResponse(call: Call, response: Response) {

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE    // Elegant moon animation with mystical floating

            interpolator = AccelerateDecelerateInterpolator()

        }    private fun startMoonAnimation() {                response.body?.let { responseBody ->            != PackageManager.PERMISSION_GRANTED) {    private lateinit var binding: ActivityMainBindingimport com.google.gson.Gsonimport com.google.gson.Gson

        

        val snowRotate = RotateAnimation(        val floatY = TranslateAnimation(

            -15f, 15f,

            Animation.RELATIVE_TO_SELF, 0.5f,            0f, 0f, 0f, -40f                    val weatherData = Gson().fromJson(responseBody.string(), WeatherData::class.java)

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {        ).apply {

            duration = 4000

            repeatCount = Animation.INFINITE            duration = 5000                    runOnUiThread {            ActivityCompat.requestPermissions(this, 

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()            repeatCount = Animation.INFINITE

        }

                    repeatMode = Animation.REVERSE                        updateUI(weatherData)

        val crystalline = AlphaAnimation(0.8f, 1.0f).apply {

            duration = 3000            interpolator = AccelerateDecelerateInterpolator()

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE        }                    }                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),     private lateinit var fusedLocationClient: FusedLocationProviderClient

        }

                

        val animationSet = AnimationSet(false).apply {

            addAnimation(snowFall)        val floatX = TranslateAnimation(                }

            addAnimation(snowSway)

            addAnimation(snowRotate)            -15f, 15f, 0f, 0f

            addAnimation(crystalline)

        }        ).apply {            }                LOCATION_PERMISSION_REQUEST_CODE)

        

        binding.ivWeatherIcon.startAnimation(animationSet)            duration = 7000

    }

                repeatCount = Animation.INFINITE        })

    private fun startMistAnimation() {

        val mistDrift = TranslateAnimation(            repeatMode = Animation.REVERSE

            -50f, 50f, 0f, 0f

        ).apply {            interpolator = AccelerateDecelerateInterpolator()    }        } else {    private lateinit var timeHandler: Handlerimport okhttp3.*import okhttp3.*

            duration = 10000

            repeatCount = Animation.INFINITE        }

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()            

        }

                val mysticalGlow = AlphaAnimation(0.6f, 1.0f).apply {

        val etherealFade = AlphaAnimation(0.3f, 1.0f).apply {

            duration = 4000            duration = 3500    private fun updateUI(weatherData: WeatherData) {            getCurrentLocation()

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            repeatCount = Animation.INFINITE

        }

                    repeatMode = Animation.REVERSE        binding.apply {

        val mistExpansion = ScaleAnimation(

            0.7f, 1.3f, 0.7f, 1.3f,        }

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f                    tvTemperature.text = "${weatherData.main.temp.roundToInt()}°"        }    private lateinit var timeRunnable: Runnable

        ).apply {

            duration = 6000        val animationSet = AnimationSet(false).apply {

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            addAnimation(floatY)            tvCityName.text = weatherData.name

            interpolator = AccelerateDecelerateInterpolator()

        }            addAnimation(floatX)

        

        val verticalFloat = TranslateAnimation(            addAnimation(mysticalGlow)            tvDescription.text = weatherData.weather[0].description.replaceFirstChar {     }

            0f, 0f, -15f, 15f

        ).apply {        }

            duration = 8000

            repeatCount = Animation.INFINITE                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()        binding.ivWeatherIcon.startAnimation(animationSet)

        }

            }            }        import java.io.IOExceptionimport java.io.IOException

        val animationSet = AnimationSet(false).apply {

            addAnimation(mistDrift)    

            addAnimation(etherealFade)

            addAnimation(mistExpansion)    // Professional cloud drifting animation            tvHumidity.text = "${weatherData.main.humidity}%"

            addAnimation(verticalFloat)

        }    private fun startCloudAnimation() {

        

        binding.ivWeatherIcon.startAnimation(animationSet)        val driftAnimation = TranslateAnimation(            tvWindSpeed.text = "${weatherData.wind.speed} m/s"    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

    }

                -60f, 60f, 0f, 0f

    private fun updateTime() {

        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())        ).apply {            tvFeelsLike.text = "${weatherData.main.feels_like.roundToInt()}°"

        binding.tvCurrentTime.text = currentTime

    }            duration = 12000

    

    private fun startTimeUpdates() {            repeatCount = Animation.INFINITE            tvPressure.text = "${weatherData.main.pressure} hPa"        super.onRequestPermissionsResult(requestCode, permissions, grantResults)    companion object {

        timeHandler.post(timeRunnable)

    }            repeatMode = Animation.REVERSE

    

    override fun onDestroy() {            interpolator = AccelerateDecelerateInterpolator()            tvVisibility.text = "${(weatherData.visibility / 1000.0).roundToInt()} km"

        super.onDestroy()

        timeHandler.removeCallbacks(timeRunnable)        }

        binding.ivWeatherIcon.clearAnimation()

    }                            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

}
        val morphScale = ScaleAnimation(

            0.85f, 1.15f, 0.85f, 1.15f,            val iconCode = weatherData.weather[0].icon

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f            setWeatherIcon(iconCode)            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {        private const val LOCATION_PERMISSION_REQUEST_CODE = 1import java.text.SimpleDateFormatimport java.text.SimpleDateFormat

        ).apply {

            duration = 6000            setBackgroundAndAnimation(weatherData.weather[0].main, iconCode)

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE        }                getCurrentLocation()

            interpolator = AccelerateDecelerateInterpolator()

        }    }

        

        val subtleFloat = TranslateAnimation(                } else {    }

            0f, 0f, -20f, 20f

        ).apply {    private fun setWeatherIcon(iconCode: String) {

            duration = 8000

            repeatCount = Animation.INFINITE        val iconResource = when (iconCode) {                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()            "01d" -> R.drawable.ic_sun

        }

                    "01n" -> R.drawable.ic_moon            }    import java.util.*import java.util.*

        val animationSet = AnimationSet(false).apply {

            addAnimation(driftAnimation)            "02d", "02n" -> R.drawable.ic_cloud_sun

            addAnimation(morphScale)

            addAnimation(subtleFloat)            "03d", "03n", "04d", "04n" -> R.drawable.ic_clouds        }

        }

                    "09d", "09n", "10d", "10n" -> R.drawable.ic_rain

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            "11d", "11n" -> R.drawable.ic_thunder    }    override fun onCreate(savedInstanceState: Bundle?) {

    

    // Dynamic rain animation with intensity            "13d", "13n" -> R.drawable.ic_snow

    private fun startRainAnimation() {

        val intenseDrop = TranslateAnimation(            "50d", "50n" -> R.drawable.ic_mist    

            0f, 0f, -30f, 30f

        ).apply {            else -> R.drawable.ic_sun

            duration = 400

            repeatCount = Animation.INFINITE        }    @SuppressLint("MissingPermission")        super.onCreate(savedInstanceState)

            repeatMode = Animation.REVERSE

            interpolator = BounceInterpolator()        binding.ivWeatherIcon.setImageResource(iconResource)

        }

            }    private fun getCurrentLocation() {

        val windShake = TranslateAnimation(

            -15f, 15f, -8f, 8f    

        ).apply {

            duration = 150    private fun setBackgroundAndAnimation(weatherMain: String, iconCode: String) {        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

            repeatCount = Animation.INFINITE

            interpolator = CycleInterpolator(1f)        val backgroundResource = when {

        }

                    iconCode.contains("d") -> when (weatherMain.lowercase()) {            location?.let {

        val rainIntensity = AlphaAnimation(0.5f, 1.0f).apply {

            duration = 600                "clear" -> R.drawable.bg_sunny

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE                "clouds" -> R.drawable.bg_cloudy                fetchWeatherByLocation(it.latitude, it.longitude)        class MainActivity : AppCompatActivity() {class MainActivity : AppCompatActivity() {

        }

                        "rain", "drizzle" -> R.drawable.bg_rainy

        val impactScale = ScaleAnimation(

            0.9f, 1.1f, 0.9f, 1.1f,                "thunderstorm" -> R.drawable.bg_storm            } ?: run {

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f                "snow" -> R.drawable.bg_snow

        ).apply {

            duration = 300                "mist", "fog", "haze" -> R.drawable.bg_mist                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE                else -> R.drawable.bg_sunny

            interpolator = BounceInterpolator()

        }            }            }

        

        val animationSet = AnimationSet(false).apply {            else -> R.drawable.bg_night

            addAnimation(intenseDrop)

            addAnimation(windShake)        }        }        

            addAnimation(rainIntensity)

            addAnimation(impactScale)        

        }

                binding.root.background = resources.getDrawable(backgroundResource, theme)    }

        binding.ivWeatherIcon.startAnimation(animationSet)

    }        startAdvancedWeatherAnimation(weatherMain.lowercase(), iconCode.contains("d"))

    

    // Gentle drizzle with soft movements    }            startLiveTime()

    private fun startDrizzleAnimation() {

        val gentleDrop = TranslateAnimation(    

            -8f, 8f, -20f, 20f

        ).apply {    private fun startAdvancedWeatherAnimation(weatherType: String, isDayTime: Boolean) {    private fun fetchWeatherByCity(city: String) {

            duration = 2500

            repeatCount = Animation.INFINITE        when (weatherType) {

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()            "clear" -> if (isDayTime) startSunAnimation() else startMoonAnimation()        val client = OkHttpClient.Builder()        requestLocationPermission()    private lateinit var binding: ActivityMainBinding    private lateinit var binding: ActivityMainBinding

        }

                    "clouds" -> startCloudAnimation()

        val softAlpha = AlphaAnimation(0.7f, 1.0f).apply {

            duration = 2000            "rain" -> startRainAnimation()            .connectTimeout(10, TimeUnit.SECONDS)

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            "drizzle" -> startDrizzleAnimation()

        }

                    "thunderstorm" -> startThunderstormAnimation()            .writeTimeout(10, TimeUnit.SECONDS)        

        val subtleScale = ScaleAnimation(

            0.95f, 1.05f, 0.95f, 1.05f,            "snow" -> startSnowAnimation()

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f            "mist", "fog", "haze" -> startMistAnimation()            .readTimeout(30, TimeUnit.SECONDS)

        ).apply {

            duration = 3000            else -> if (isDayTime) startSunAnimation() else startMoonAnimation()

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE        }            .build()        binding.btnSearch.setOnClickListener {    private val client = OkHttpClient()    private val client = OkHttpClient()

            interpolator = AccelerateDecelerateInterpolator()

        }    }

        

        val animationSet = AnimationSet(false).apply {                

            addAnimation(gentleDrop)

            addAnimation(softAlpha)    private fun startSunAnimation() {

            addAnimation(subtleScale)

        }        // Rotating sun with pulsing glow effect        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=${Constants.API_KEY}&units=metric"            val city = binding.etCity.text.toString().trim()

        

        binding.ivWeatherIcon.startAnimation(animationSet)        val rotateAnimation = RotateAnimation(

    }

                0f, 360f,        val request = Request.Builder().url(url).build()

    // Dramatic thunderstorm with lightning effects

    private fun startThunderstormAnimation() {            Animation.RELATIVE_TO_SELF, 0.5f,

        val lightningFlash = AlphaAnimation(1.0f, 0.2f).apply {

            duration = 100            Animation.RELATIVE_TO_SELF, 0.5f                    if (city.isNotEmpty()) {    private val timeHandler = Handler(Looper.getMainLooper())    private val timeHandler = Handler(Looper.getMainLooper())

            repeatCount = 5

            repeatMode = Animation.REVERSE        ).apply {

        }

                    duration = 20000        client.newCall(request).enqueue(object : Callback {

        val thunderShake = TranslateAnimation(

            -20f, 20f, -20f, 20f            repeatCount = Animation.INFINITE

        ).apply {

            duration = 80            interpolator = LinearInterpolator()            override fun onFailure(call: Call, e: IOException) {                fetchWeatherByCity(city)

            repeatCount = 15

            interpolator = CycleInterpolator(1f)        }

        }

                                runOnUiThread {

        val dramaticScale = ScaleAnimation(

            1.0f, 1.4f, 1.0f, 1.4f,        val scaleAnimation = ScaleAnimation(

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f            1.0f, 1.2f, 1.0f, 1.2f,                    Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()            } else {    private var timeRunnable: Runnable? = null    private var timeRunnable: Runnable? = null

        ).apply {

            duration = 250            Animation.RELATIVE_TO_SELF, 0.5f,

            repeatCount = 3

            repeatMode = Animation.REVERSE            Animation.RELATIVE_TO_SELF, 0.5f                }

            interpolator = AccelerateDecelerateInterpolator()

        }        ).apply {

        

        val electricRotate = RotateAnimation(            duration = 3000            }                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()

            -5f, 5f,

            Animation.RELATIVE_TO_SELF, 0.5f,            repeatCount = Animation.INFINITE

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {            repeatMode = Animation.REVERSE            

            duration = 100

            repeatCount = 8            interpolator = AccelerateDecelerateInterpolator()

            repeatMode = Animation.REVERSE

            interpolator = CycleInterpolator(1f)        }            override fun onResponse(call: Call, response: Response) {            }

        }

                

        val animationSet = AnimationSet(false).apply {

            addAnimation(lightningFlash)        val animationSet = AnimationSet(false).apply {                response.use {

            addAnimation(thunderShake)

            addAnimation(dramaticScale)            addAnimation(rotateAnimation)

            addAnimation(electricRotate)

        }            addAnimation(scaleAnimation)                    if (response.isSuccessful) {        }

        

        binding.ivWeatherIcon.startAnimation(animationSet)        }

        

        // Repeat dramatic sequence every 4 seconds                                val responseBody = response.body?.string()

        Handler(Looper.getMainLooper()).postDelayed({

            if (!isDestroyed) {        binding.ivWeatherIcon.startAnimation(animationSet)

                startThunderstormAnimation()

            }    }                        responseBody?.let { json ->    }    override fun onCreate(savedInstanceState: Bundle?) {    override fun onCreate(savedInstanceState: Bundle?) {

        }, 4000)

    }    

    

    // Graceful snow animation with swaying fall    private fun startMoonAnimation() {                            runOnUiThread {

    private fun startSnowAnimation() {

        val snowFall = TranslateAnimation(        // Gentle floating moon with subtle glow

            0f, 0f, -40f, 40f

        ).apply {        val translateAnimation = TranslateAnimation(                                parseWeatherData(json)    

            duration = 5000

            repeatCount = Animation.INFINITE            0f, 0f, 0f, -30f

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()        ).apply {                            }

        }

                    duration = 4000

        val snowSway = TranslateAnimation(

            -25f, 25f, 0f, 0f            repeatCount = Animation.INFINITE                        }    private fun requestLocationPermission() {        super.onCreate(savedInstanceState)        super.onCreate(savedInstanceState)

        ).apply {

            duration = 7000            repeatMode = Animation.REVERSE

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            interpolator = AccelerateDecelerateInterpolator()                    } else {

            interpolator = AccelerateDecelerateInterpolator()

        }        }

        

        val snowRotate = RotateAnimation(                                runOnUiThread {        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) 

            -15f, 15f,

            Animation.RELATIVE_TO_SELF, 0.5f,        val alphaAnimation = AlphaAnimation(0.7f, 1.0f).apply {

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {            duration = 3000                            Toast.makeText(this@MainActivity, "City not found", Toast.LENGTH_SHORT).show()

            duration = 4000

            repeatCount = Animation.INFINITE            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()            repeatMode = Animation.REVERSE                        }            != PackageManager.PERMISSION_GRANTED) {        binding = ActivityMainBinding.inflate(layoutInflater)        binding = ActivityMainBinding.inflate(layoutInflater)

        }

                }

        val crystalline = AlphaAnimation(0.8f, 1.0f).apply {

            duration = 3000                            }

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE        val animationSet = AnimationSet(false).apply {

        }

                    addAnimation(translateAnimation)                }            ActivityCompat.requestPermissions(this, 

        val animationSet = AnimationSet(false).apply {

            addAnimation(snowFall)            addAnimation(alphaAnimation)

            addAnimation(snowSway)

            addAnimation(snowRotate)        }            }

            addAnimation(crystalline)

        }        

        

        binding.ivWeatherIcon.startAnimation(animationSet)        binding.ivWeatherIcon.startAnimation(animationSet)        })                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),         setContentView(binding.root)        setContentView(binding.root)

    }

        }

    // Mysterious mist with ethereal movement

    private fun startMistAnimation() {        }

        val mistDrift = TranslateAnimation(

            -50f, 50f, 0f, 0f    private fun startCloudAnimation() {

        ).apply {

            duration = 10000        // Drifting clouds with subtle scale changes                    LOCATION_PERMISSION_REQUEST_CODE)

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE        val translateAnimation = TranslateAnimation(

            interpolator = AccelerateDecelerateInterpolator()

        }            -50f, 50f, 0f, 0f    private fun fetchWeatherByLocation(lat: Double, lon: Double) {

        

        val etherealFade = AlphaAnimation(0.3f, 1.0f).apply {        ).apply {

            duration = 4000

            repeatCount = Animation.INFINITE            duration = 8000        val client = OkHttpClient.Builder()        } else {

            repeatMode = Animation.REVERSE

        }            repeatCount = Animation.INFINITE

        

        val mistExpansion = ScaleAnimation(            repeatMode = Animation.REVERSE            .connectTimeout(10, TimeUnit.SECONDS)

            0.7f, 1.3f, 0.7f, 1.3f,

            Animation.RELATIVE_TO_SELF, 0.5f,            interpolator = AccelerateDecelerateInterpolator()

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {        }            .writeTimeout(10, TimeUnit.SECONDS)            getCurrentLocation()

            duration = 6000

            repeatCount = Animation.INFINITE        

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()        val scaleAnimation = ScaleAnimation(            .readTimeout(30, TimeUnit.SECONDS)

        }

                    0.9f, 1.1f, 0.9f, 1.1f,

        val verticalFloat = TranslateAnimation(

            0f, 0f, -15f, 15f            Animation.RELATIVE_TO_SELF, 0.5f,            .build()        }        setupViews()        setupViews()

        ).apply {

            duration = 8000            Animation.RELATIVE_TO_SELF, 0.5f

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE        ).apply {            

            interpolator = AccelerateDecelerateInterpolator()

        }            duration = 5000

        

        val animationSet = AnimationSet(false).apply {            repeatCount = Animation.INFINITE        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=${Constants.API_KEY}&units=metric"    }

            addAnimation(mistDrift)

            addAnimation(etherealFade)            repeatMode = Animation.REVERSE

            addAnimation(mistExpansion)

            addAnimation(verticalFloat)            interpolator = AccelerateDecelerateInterpolator()        val request = Request.Builder().url(url).build()

        }

                }

        binding.ivWeatherIcon.startAnimation(animationSet)

    }                            startTimeUpdates()        startTimeUpdates()

    

    private fun updateTime() {        val animationSet = AnimationSet(false).apply {

        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        binding.tvCurrentTime.text = currentTime            addAnimation(translateAnimation)        client.newCall(request).enqueue(object : Callback {

    }

                addAnimation(scaleAnimation)

    private fun startTimeUpdates() {

        timeHandler.post(timeRunnable)        }            override fun onFailure(call: Call, e: IOException) {    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

    }

            

    override fun onDestroy() {

        super.onDestroy()        binding.ivWeatherIcon.startAnimation(animationSet)                runOnUiThread {

        timeHandler.removeCallbacks(timeRunnable)

        binding.ivWeatherIcon.clearAnimation()    }

    }

}                        Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()        super.onRequestPermissionsResult(requestCode, permissions, grantResults)                

    private fun startRainAnimation() {

        // Heavy rain with shaking and bouncing effects                }

        val shakeAnimation = TranslateAnimation(

            -10f, 10f, -10f, 10f            }        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

        ).apply {

            duration = 100            

            repeatCount = Animation.INFINITE

            interpolator = CycleInterpolator(1f)            override fun onResponse(call: Call, response: Response) {            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {        // Load cuaca default untuk Jakarta        // Load cuaca default untuk Jakarta

        }

                        response.use {

        val bounceAnimation = TranslateAnimation(

            0f, 0f, -20f, 20f                    if (response.isSuccessful) {                getCurrentLocation()

        ).apply {

            duration = 800                        val responseBody = response.body?.string()

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE                        responseBody?.let { json ->            } else {        getWeatherData("Jakarta")        getWeatherData("Jakarta")

            interpolator = BounceInterpolator()

        }                            runOnUiThread {

        

        val alphaAnimation = AlphaAnimation(0.6f, 1.0f).apply {                                parseWeatherData(json)                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()

            duration = 500

            repeatCount = Animation.INFINITE                            }

            repeatMode = Animation.REVERSE

        }                        }            }    }    }

        

        val animationSet = AnimationSet(false).apply {                    } else {

            addAnimation(shakeAnimation)

            addAnimation(bounceAnimation)                        runOnUiThread {        }

            addAnimation(alphaAnimation)

        }                            Toast.makeText(this@MainActivity, "Unable to fetch weather data", Toast.LENGTH_SHORT).show()

        

        binding.ivWeatherIcon.startAnimation(animationSet)                        }    }

    }

                        }

    private fun startDrizzleAnimation() {

        // Gentle drizzle with soft movements                }    

        val translateAnimation = TranslateAnimation(

            -5f, 5f, -15f, 15f            }

        ).apply {

            duration = 2000        })    @SuppressLint("MissingPermission")    private fun setupViews() {    private fun setupViews() {

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE    }

            interpolator = AccelerateDecelerateInterpolator()

        }        private fun getCurrentLocation() {

        

        val alphaAnimation = AlphaAnimation(0.8f, 1.0f).apply {    private fun parseWeatherData(json: String) {

            duration = 1500

            repeatCount = Animation.INFINITE        try {        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->        binding.btnSearch.setOnClickListener {        binding.btnSearch.setOnClickListener {

            repeatMode = Animation.REVERSE

        }            val gson = Gson()

        

        val animationSet = AnimationSet(false).apply {            val weather = gson.fromJson(json, WeatherData::class.java)            location?.let {

            addAnimation(translateAnimation)

            addAnimation(alphaAnimation)            

        }

                    val weatherMain = weather.weather[0].main                fetchWeatherByLocation(it.latitude, it.longitude)            val cityName = binding.etCityName.text.toString().trim()            val cityName = binding.etCityName.text.toString().trim()

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            val weatherDescription = weather.weather[0].description

    

    private fun startThunderstormAnimation() {                        } ?: run {

        // Dramatic thunderstorm with flash and shake effects

        val flashAnimation = AlphaAnimation(1.0f, 0.3f).apply {            binding.tvCityName.text = weather.name

            duration = 150

            repeatCount = 3            binding.tvTemperature.text = "${weather.main.temp.toInt()}°C"                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()            if (cityName.isNotEmpty()) {            if (cityName.isNotEmpty()) {

            repeatMode = Animation.REVERSE

        }            binding.tvFeelsLike.text = "Feels like ${weather.main.feels_like.toInt()}°C"

        

        val shakeAnimation = TranslateAnimation(            binding.tvHumidity.text = "${weather.main.humidity}%"            }

            -15f, 15f, -15f, 15f

        ).apply {            binding.tvPressure.text = "${weather.main.pressure} hPa"

            duration = 80

            repeatCount = 10            binding.tvWindSpeed.text = "${weather.wind.speed} m/s"        }                getWeatherData(cityName)                getWeatherData(cityName)

            interpolator = CycleInterpolator(1f)

        }            binding.tvVisibility.text = "${weather.visibility / 1000} km"

        

        val scaleAnimation = ScaleAnimation(            binding.tvWeatherDescription.text = weatherDescription.replaceFirstChar { it.uppercase() }    }

            1.0f, 1.3f, 1.0f, 1.3f,

            Animation.RELATIVE_TO_SELF, 0.5f,            binding.tvMaxTemp.text = "${weather.main.temp_max.toInt()}°C"

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {            binding.tvMinTemp.text = "${weather.main.temp_min.toInt()}°C"                    binding.etCityName.text.clear()                binding.etCityName.text.clear()

            duration = 200

            repeatCount = 2            

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()            val sunriseTime = Date(weather.sys.sunrise * 1000L)    private fun fetchWeatherByCity(city: String) {

        }

                    val sunsetTime = Date(weather.sys.sunset * 1000L)

        val animationSet = AnimationSet(false).apply {

            addAnimation(flashAnimation)            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())        val client = OkHttpClient.Builder()            } else {            } else {

            addAnimation(shakeAnimation)

            addAnimation(scaleAnimation)            

        }

                    binding.tvSunrise.text = timeFormat.format(sunriseTime)            .connectTimeout(10, TimeUnit.SECONDS)

        binding.ivWeatherIcon.startAnimation(animationSet)

                    binding.tvSunset.text = timeFormat.format(sunsetTime)

        // Repeat the dramatic effect every 3 seconds

        Handler(Looper.getMainLooper()).postDelayed({                        .writeTimeout(10, TimeUnit.SECONDS)                Toast.makeText(this, "Masukkan nama kota", Toast.LENGTH_SHORT).show()                Toast.makeText(this, "Masukkan nama kota", Toast.LENGTH_SHORT).show()

            if (!isDestroyed) {

                startThunderstormAnimation()            setWeatherIcon(weatherMain)

            }

        }, 3000)            startWeatherAnimation(weatherMain)            .readTimeout(30, TimeUnit.SECONDS)

    }

                

    private fun startSnowAnimation() {

        // Gentle snow with falling and swaying motion        } catch (e: Exception) {            .build()            }            }

        val translateYAnimation = TranslateAnimation(

            0f, 0f, -30f, 30f            Log.e("WeatherApp", "Error parsing weather data: ${e.message}")

        ).apply {

            duration = 4000            Toast.makeText(this, "Error displaying weather data", Toast.LENGTH_SHORT).show()            

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE        }

            interpolator = AccelerateDecelerateInterpolator()

        }    }        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=${Constants.API_KEY}&units=metric"        }        }

        

        val translateXAnimation = TranslateAnimation(    

            -20f, 20f, 0f, 0f

        ).apply {    private fun setWeatherIcon(weatherMain: String) {        val request = Request.Builder().url(url).build()

            duration = 6000

            repeatCount = Animation.INFINITE        when (weatherMain.lowercase()) {

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()            "clear" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)            }

        }

                    "clouds" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

        val rotateAnimation = RotateAnimation(

            -10f, 10f,            "rain" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)        client.newCall(request).enqueue(object : Callback {

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f            "drizzle" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

        ).apply {

            duration = 3000            "thunderstorm" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_thunderstorm)            override fun onFailure(call: Call, e: IOException) {        // Search saat enter di keyboard

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            "snow" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_snowy)

            interpolator = AccelerateDecelerateInterpolator()

        }            "mist", "fog", "haze" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_misty)                runOnUiThread {

        

        val animationSet = AnimationSet(false).apply {            else -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

            addAnimation(translateYAnimation)

            addAnimation(translateXAnimation)        }                    Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()        binding.etCityName.setOnEditorActionListener { _, _, _ ->    private fun getWeatherData(cityName: String) {

            addAnimation(rotateAnimation)

        }    }

        

        binding.ivWeatherIcon.startAnimation(animationSet)                    }

    }

        private fun startWeatherAnimation(weatherMain: String) {

    private fun startMistAnimation() {

        // Mysterious mist with fade and drift effects        when (weatherMain.lowercase()) {            }            val cityName = binding.etCityName.text.toString().trim()        showLoading(true)

        val alphaAnimation = AlphaAnimation(0.4f, 1.0f).apply {

            duration = 3000            "clear" -> startSunAnimation()

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            "clouds" -> startCloudAnimation()            

        }

                    "rain" -> startRainAnimation()

        val translateAnimation = TranslateAnimation(

            -30f, 30f, 0f, 0f            "drizzle" -> startDrizzleAnimation()            override fun onResponse(call: Call, response: Response) {            if (cityName.isNotEmpty()) {        

        ).apply {

            duration = 8000            "thunderstorm" -> startThunderstormAnimation()

            repeatCount = Animation.INFINITE

            repeatMode = Animation.REVERSE            "snow" -> startSnowAnimation()                response.use {

            interpolator = AccelerateDecelerateInterpolator()

        }            "mist", "fog", "haze" -> startMistAnimation()

        

        val scaleAnimation = ScaleAnimation(            else -> startCloudAnimation()                    if (response.isSuccessful) {                getWeatherData(cityName)        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=${Constants.API_KEY}&units=metric"

            0.8f, 1.2f, 0.8f, 1.2f,

            Animation.RELATIVE_TO_SELF, 0.5f,        }

            Animation.RELATIVE_TO_SELF, 0.5f

        ).apply {    }                        val responseBody = response.body?.string()

            duration = 5000

            repeatCount = Animation.INFINITE    

            repeatMode = Animation.REVERSE

            interpolator = AccelerateDecelerateInterpolator()    private fun startSunAnimation() {                        responseBody?.let { json ->                binding.etCityName.text.clear()        val request = Request.Builder().url(url).build()

        }

                val rotation = RotateAnimation(0f, 360f, 

        val animationSet = AnimationSet(false).apply {

            addAnimation(alphaAnimation)            Animation.RELATIVE_TO_SELF, 0.5f,                             runOnUiThread {

            addAnimation(translateAnimation)

            addAnimation(scaleAnimation)            Animation.RELATIVE_TO_SELF, 0.5f)

        }

                rotation.duration = 8000                                parseWeatherData(json)                true

        binding.ivWeatherIcon.startAnimation(animationSet)

    }        rotation.interpolator = LinearInterpolator()

    

    private fun updateTime() {        rotation.repeatCount = Animation.INFINITE                            }

        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        binding.tvCurrentTime.text = currentTime        

    }

            val scale = ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,                        }            } else {        client.newCall(request).enqueue(object : Callback {

    private fun startTimeUpdates() {

        timeHandler.post(timeRunnable)            Animation.RELATIVE_TO_SELF, 0.5f,

    }

                Animation.RELATIVE_TO_SELF, 0.5f)                    } else {

    override fun onDestroy() {

        super.onDestroy()        scale.duration = 2000

        timeHandler.removeCallbacks(timeRunnable)

        binding.ivWeatherIcon.clearAnimation()        scale.interpolator = AccelerateDecelerateInterpolator()                        runOnUiThread {                false            override fun onFailure(call: Call, e: IOException) {

    }

}        scale.repeatCount = Animation.INFINITE

        scale.repeatMode = Animation.REVERSE                            Toast.makeText(this@MainActivity, "City not found", Toast.LENGTH_SHORT).show()

        

        val animationSet = AnimationSet(true)                        }            }                runOnUiThread {

        animationSet.addAnimation(rotation)

        animationSet.addAnimation(scale)                    }

        

        binding.ivWeatherIcon.startAnimation(animationSet)                }        }                    showLoading(false)

    }

                }

    private fun startCloudAnimation() {

        val floatX = TranslateAnimation(0f, 15f, 0f, 0f)        })    }                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()

        floatX.duration = 3000

        floatX.interpolator = AccelerateDecelerateInterpolator()    }

        floatX.repeatCount = Animation.INFINITE

        floatX.repeatMode = Animation.REVERSE                        Log.e("WeatherApp", "Network error", e)

        

        val alpha = AlphaAnimation(0.7f, 1.0f)    private fun fetchWeatherByLocation(lat: Double, lon: Double) {

        alpha.duration = 2500

        alpha.interpolator = AccelerateDecelerateInterpolator()        val client = OkHttpClient.Builder()    private fun getWeatherData(cityName: String) {                }

        alpha.repeatCount = Animation.INFINITE

        alpha.repeatMode = Animation.REVERSE            .connectTimeout(10, TimeUnit.SECONDS)

        

        val animationSet = AnimationSet(true)            .writeTimeout(10, TimeUnit.SECONDS)        showLoading(true)            }

        animationSet.addAnimation(floatX)

        animationSet.addAnimation(alpha)            .readTimeout(30, TimeUnit.SECONDS)

        

        binding.ivWeatherIcon.startAnimation(animationSet)            .build()        

    }

                

    private fun startRainAnimation() {

        val drop = TranslateAnimation(0f, 0f, -20f, 20f)        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=${Constants.API_KEY}&units=metric"        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=${Constants.API_KEY}&units=metric"            override fun onResponse(call: Call, response: Response) {

        drop.duration = 600

        drop.interpolator = BounceInterpolator()        val request = Request.Builder().url(url).build()

        drop.repeatCount = Animation.INFINITE

                        val request = Request.Builder().url(url).build()                runOnUiThread {

        val shake = TranslateAnimation(-3f, 3f, 0f, 0f)

        shake.duration = 150        client.newCall(request).enqueue(object : Callback {

        shake.interpolator = CycleInterpolator(5f)

        shake.repeatCount = Animation.INFINITE            override fun onFailure(call: Call, e: IOException) {                    showLoading(false)

        

        val animationSet = AnimationSet(true)                runOnUiThread {

        animationSet.addAnimation(drop)

        animationSet.addAnimation(shake)                    Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()        client.newCall(request).enqueue(object : Callback {                    

        

        binding.ivWeatherIcon.startAnimation(animationSet)                }

    }

                }            override fun onFailure(call: Call, e: IOException) {                    if (response.isSuccessful) {

    private fun startDrizzleAnimation() {

        val gentleDrop = TranslateAnimation(0f, 0f, -10f, 10f)            

        gentleDrop.duration = 1000

        gentleDrop.interpolator = AccelerateDecelerateInterpolator()            override fun onResponse(call: Call, response: Response) {                runOnUiThread {                        val responseBody = response.body?.string()

        gentleDrop.repeatCount = Animation.INFINITE

        gentleDrop.repeatMode = Animation.REVERSE                response.use {

        

        val alpha = AlphaAnimation(0.6f, 1.0f)                    if (response.isSuccessful) {                    showLoading(false)                        if (responseBody != null) {

        alpha.duration = 1500

        alpha.interpolator = AccelerateDecelerateInterpolator()                        val responseBody = response.body?.string()

        alpha.repeatCount = Animation.INFINITE

        alpha.repeatMode = Animation.REVERSE                        responseBody?.let { json ->                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()                            try {

        

        val animationSet = AnimationSet(true)                            runOnUiThread {

        animationSet.addAnimation(gentleDrop)

        animationSet.addAnimation(alpha)                                parseWeatherData(json)                    Log.e("WeatherApp", "Network error", e)                                val weatherResponse = Gson().fromJson(responseBody, weatherapp::class.java)

        

        binding.ivWeatherIcon.startAnimation(animationSet)                            }

    }

                            }                }                                updateUI(weatherResponse)

    private fun startThunderstormAnimation() {

        val flash = AlphaAnimation(1.0f, 0.2f)                    } else {

        flash.duration = 100

        flash.repeatCount = 3                        runOnUiThread {            }                            } catch (e: Exception) {

        flash.repeatMode = Animation.REVERSE

                                    Toast.makeText(this@MainActivity, "Unable to fetch weather data", Toast.LENGTH_SHORT).show()

        val shake = TranslateAnimation(-8f, 8f, -8f, 8f)

        shake.duration = 80                        }                                Toast.makeText(this@MainActivity, "Error parsing weather data", Toast.LENGTH_LONG).show()

        shake.interpolator = CycleInterpolator(4f)

        shake.repeatCount = 5                    }

        shake.startOffset = 400

                        }            override fun onResponse(call: Call, response: Response) {                                Log.e("WeatherApp", "JSON parsing error", e)

        val animationSet = AnimationSet(false)

        animationSet.addAnimation(flash)            }

        animationSet.addAnimation(shake)

                })                runOnUiThread {                            }

        binding.ivWeatherIcon.startAnimation(animationSet)

    }    }

    

    private fun startSnowAnimation() {                        showLoading(false)                        }

        val sway = TranslateAnimation(-10f, 10f, 0f, 0f)

        sway.duration = 4000    private fun parseWeatherData(json: String) {

        sway.interpolator = AccelerateDecelerateInterpolator()

        sway.repeatCount = Animation.INFINITE        try {                                        } else {

        sway.repeatMode = Animation.REVERSE

                    val gson = Gson()

        val fall = TranslateAnimation(0f, 0f, -15f, 15f)

        fall.duration = 3000            val weather = gson.fromJson(json, WeatherData::class.java)                    if (response.isSuccessful) {                        Toast.makeText(this@MainActivity, "Kota tidak ditemukan", Toast.LENGTH_LONG).show()

        fall.interpolator = LinearInterpolator()

        fall.repeatCount = Animation.INFINITE            

        fall.repeatMode = Animation.REVERSE

                    val weatherMain = weather.weather[0].main                        val responseBody = response.body?.string()                        Log.e("MainActivity", "Response not successful: ${response.code}")

        val rotate = RotateAnimation(0f, 360f,

            Animation.RELATIVE_TO_SELF, 0.5f,            val weatherDescription = weather.weather[0].description

            Animation.RELATIVE_TO_SELF, 0.5f)

        rotate.duration = 6000                                    if (responseBody != null) {                    }

        rotate.interpolator = LinearInterpolator()

        rotate.repeatCount = Animation.INFINITE            binding.tvCityName.text = weather.name

        

        val animationSet = AnimationSet(true)            binding.tvTemperature.text = "${weather.main.temp.toInt()}°C"                            try {                }

        animationSet.addAnimation(sway)

        animationSet.addAnimation(fall)            binding.tvFeelsLike.text = "Feels like ${weather.main.feels_like.toInt()}°C"

        animationSet.addAnimation(rotate)

                    binding.tvHumidity.text = "${weather.main.humidity}%"                                val weatherResponse = Gson().fromJson(responseBody, weatherapp::class.java)            }

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            binding.tvPressure.text = "${weather.main.pressure} hPa"

    

    private fun startMistAnimation() {            binding.tvWindSpeed.text = "${weather.wind.speed} m/s"                                updateUI(weatherResponse)        })

        val fade = AlphaAnimation(0.4f, 0.8f)

        fade.duration = 3000            binding.tvVisibility.text = "${weather.visibility / 1000} km"

        fade.interpolator = AccelerateDecelerateInterpolator()

        fade.repeatCount = Animation.INFINITE            binding.tvWeatherDescription.text = weatherDescription.replaceFirstChar { it.uppercase() }                            } catch (e: Exception) {    }

        fade.repeatMode = Animation.REVERSE

                    binding.tvMaxTemp.text = "${weather.main.temp_max.toInt()}°C"

        val drift = TranslateAnimation(-20f, 20f, 0f, 0f)

        drift.duration = 5000            binding.tvMinTemp.text = "${weather.main.temp_min.toInt()}°C"                                Toast.makeText(this@MainActivity, "Error parsing weather data", Toast.LENGTH_LONG).show()

        drift.interpolator = AccelerateDecelerateInterpolator()

        drift.repeatCount = Animation.INFINITE            

        drift.repeatMode = Animation.REVERSE

                    val sunriseTime = Date(weather.sys.sunrise * 1000L)                                Log.e("WeatherApp", "JSON parsing error", e)    @SuppressLint("SetTextI18n")

        val scale = ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f,

            Animation.RELATIVE_TO_SELF, 0.5f,            val sunsetTime = Date(weather.sys.sunset * 1000L)

            Animation.RELATIVE_TO_SELF, 0.5f)

        scale.duration = 4000            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())                            }    private fun updateUI(weather: weatherapp) {

        scale.interpolator = AccelerateDecelerateInterpolator()

        scale.repeatCount = Animation.INFINITE            

        scale.repeatMode = Animation.REVERSE

                    binding.tvSunrise.text = timeFormat.format(sunriseTime)                        }        try {

        val animationSet = AnimationSet(true)

        animationSet.addAnimation(fade)            binding.tvSunset.text = timeFormat.format(sunsetTime)

        animationSet.addAnimation(drift)

        animationSet.addAnimation(scale)                                } else {            binding.tvCityName.text = weather.name

        

        binding.ivWeatherIcon.startAnimation(animationSet)            setWeatherIcon(weatherMain)

    }

                startWeatherAnimation(weatherMain)                        Toast.makeText(this@MainActivity, "Kota tidak ditemukan", Toast.LENGTH_LONG).show()            binding.tvTemperature.text = "${weather.main.temp.toInt()}�C"

    private fun startLiveTime() {

        timeHandler = Handler(Looper.getMainLooper())            

        timeRunnable = Runnable {

            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())        } catch (e: Exception) {                        Log.e("MainActivity", "Response not successful: ${response.code}")            

            binding.tvCurrentTime.text = currentTime

            timeHandler.postDelayed(timeRunnable, 1000)            Log.e("WeatherApp", "Error parsing weather data: ${e.message}")

        }

        timeHandler.post(timeRunnable)            Toast.makeText(this, "Error displaying weather data", Toast.LENGTH_SHORT).show()                    }            if (weather.weather.isNotEmpty()) {

    }

            }

    override fun onDestroy() {

        super.onDestroy()    }                }                val weatherMain = weather.weather[0]

        timeHandler.removeCallbacks(timeRunnable)

        binding.ivWeatherIcon.clearAnimation()    

    }

}    private fun setWeatherIcon(weatherMain: String) {            }                binding.tvWeatherDescription.text = weatherMain.description.replaceFirstChar { 

        when (weatherMain.lowercase()) {

            "clear" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)        })                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 

            "clouds" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

            "rain" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)    }                }

            "drizzle" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

            "thunderstorm" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_thunderstorm)                

            "snow" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_snowy)

            "mist", "fog", "haze" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_misty)    @SuppressLint("SetTextI18n")                setWeatherIcon(weatherMain.main)

            else -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)

        }    private fun updateUI(weather: weatherapp) {                setBackground(weatherMain.main)

    }

            try {            }

    private fun startWeatherAnimation(weatherMain: String) {

        when (weatherMain.lowercase()) {            // Info dasar            

            "clear" -> startAdvancedSunAnimation()

            "clouds" -> startAdvancedCloudAnimation()            binding.tvCityName.text = weather.name            binding.tvHumidity.text = "${weather.main.humidity}%"

            "rain" -> startAdvancedRainAnimation()

            "drizzle" -> startAdvancedDrizzleAnimation()            binding.tvTemperature.text = "${weather.main.temp.toInt()}°C"            binding.tvPressure.text = "${weather.main.pressure} hPa"

            "thunderstorm" -> startAdvancedThunderstormAnimation()

            "snow" -> startAdvancedSnowAnimation()            binding.tvFeelsLike.text = "Terasa seperti ${weather.main.feels_like.toInt()}°C"            binding.tvWindSpeed.text = "${weather.wind.speed} m/s"

            "mist", "fog", "haze" -> startAdvancedMistAnimation()

            else -> startAdvancedCloudAnimation()                        binding.tvVisibility.text = "${weather.visibility / 1000} km"

        }

    }            if (weather.weather.isNotEmpty()) {            

    

    private fun startAdvancedSunAnimation() {                val weatherMain = weather.weather[0]        } catch (e: Exception) {

        val animationSet = AnimationSet(true)

                        binding.tvWeatherDescription.text = weatherMain.description.replaceFirstChar {             Log.e("MainActivity", "Error updating UI: ${e.message}")

        // Rotation animation

        val rotation = RotateAnimation(0f, 360f,                     if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()             Toast.makeText(this, "Error menampilkan data cuaca", Toast.LENGTH_SHORT).show()

            Animation.RELATIVE_TO_SELF, 0.5f, 

            Animation.RELATIVE_TO_SELF, 0.5f)                }        }

        rotation.duration = 12000

        rotation.interpolator = LinearInterpolator()                    }

        rotation.repeatCount = Animation.INFINITE

                        // Set ikon dan animasi cuaca

        // Scale animation (breathing effect)

        val scale = ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,                setWeatherIcon(weatherMain.main)    private fun showLoading(show: Boolean) {

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)                setBackground(weatherMain.main)        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE

        scale.duration = 3000

        scale.interpolator = AccelerateDecelerateInterpolator()            }        binding.btnSearch.isEnabled = !show

        scale.repeatCount = Animation.INFINITE

        scale.repeatMode = Animation.REVERSE                }

        

        // Alpha animation (glow effect)            // Detail cuaca

        val alpha = AlphaAnimation(0.8f, 1.0f)

        alpha.duration = 2000            binding.tvHumidity.text = "${weather.main.humidity}%"    private fun setWeatherIcon(weatherMain: String) {

        alpha.interpolator = AccelerateDecelerateInterpolator()

        alpha.repeatCount = Animation.INFINITE            binding.tvPressure.text = "${weather.main.pressure} hPa"        binding.ivWeatherIcon.clearAnimation()

        alpha.repeatMode = Animation.REVERSE

                    binding.tvWindSpeed.text = "${weather.wind.speed} m/s"        

        animationSet.addAnimation(rotation)

        animationSet.addAnimation(scale)            binding.tvVisibility.text = "${weather.visibility / 1000} km"        when (weatherMain.lowercase()) {

        animationSet.addAnimation(alpha)

                                "clear" -> {

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            // Temperature range                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

    

    private fun startAdvancedCloudAnimation() {            binding.tvMaxTemp.text = "Max: ${weather.main.temp_max.toInt()}°C"                startSunAnimation()

        val animationSet = AnimationSet(true)

                    binding.tvMinTemp.text = "Min: ${weather.main.temp_min.toInt()}°C"            }

        // Floating X animation

        val floatX = TranslateAnimation(0f, 20f, 0f, 0f)                        "clouds" -> {

        floatX.duration = 5000

        floatX.interpolator = AccelerateDecelerateInterpolator()            // Sunrise dan Sunset                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy) 

        floatX.repeatCount = Animation.INFINITE

        floatX.repeatMode = Animation.REVERSE            val sunriseTime = Date(weather.sys.sunrise * 1000L)                startCloudAnimation()

        

        // Floating Y animation              val sunsetTime = Date(weather.sys.sunset * 1000L)            }

        val floatY = TranslateAnimation(0f, 0f, 0f, -10f)

        floatY.duration = 4000            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())            "rain" -> {

        floatY.interpolator = AccelerateDecelerateInterpolator()

        floatY.repeatCount = Animation.INFINITE                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

        floatY.repeatMode = Animation.REVERSE

                    binding.tvSunrise.text = timeFormat.format(sunriseTime)                startRainAnimation()

        // Scale animation (depth)

        val scale = ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f,            binding.tvSunset.text = timeFormat.format(sunsetTime)            }

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)                        "drizzle" -> {

        scale.duration = 6000

        scale.interpolator = AccelerateDecelerateInterpolator()        } catch (e: Exception) {                binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)

        scale.repeatCount = Animation.INFINITE

        scale.repeatMode = Animation.REVERSE            Log.e("MainActivity", "Error updating UI: ${e.message}")                startDrizzleAnimation()

        

        // Alpha animation            Toast.makeText(this, "Error menampilkan data cuaca", Toast.LENGTH_SHORT).show()            }

        val alpha = AlphaAnimation(0.7f, 1.0f)

        alpha.duration = 3500        }            "thunderstorm" -> {

        alpha.interpolator = AccelerateDecelerateInterpolator()

        alpha.repeatCount = Animation.INFINITE    }                binding.ivWeatherIcon.setImageResource(R.drawable.ic_thunderstorm)

        alpha.repeatMode = Animation.REVERSE

                        startThunderstormAnimation()

        animationSet.addAnimation(floatX)

        animationSet.addAnimation(floatY)    private fun showLoading(show: Boolean) {            }

        animationSet.addAnimation(scale)

        animationSet.addAnimation(alpha)        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE            "snow" -> {

        

        binding.ivWeatherIcon.startAnimation(animationSet)        binding.btnSearch.isEnabled = !show                binding.ivWeatherIcon.setImageResource(R.drawable.ic_snowy)

    }

        }                startSnowAnimation()

    private fun startAdvancedRainAnimation() {

        val animationSet = AnimationSet(true)            }

        

        // Drop animation (Y translate)    // === ANIMASI CUACA ADVANCED ===            "mist", "fog", "haze" -> {

        val drop = TranslateAnimation(0f, 0f, -30f, 30f)

        drop.duration = 800                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_mist)

        drop.interpolator = BounceInterpolator()

        drop.repeatCount = Animation.INFINITE    private fun setWeatherIcon(weatherMain: String) {                startMistAnimation()

        

        // Shake animation (X translate)        // Clear animasi sebelumnya            }

        val shake = TranslateAnimation(-2f, 2f, 0f, 0f)

        shake.duration = 200        binding.ivWeatherIcon.clearAnimation()            else -> {

        shake.interpolator = CycleInterpolator(10f)

        shake.repeatCount = Animation.INFINITE                        binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

        

        // Alpha animation (splash effect)        when (weatherMain.lowercase()) {                startSunAnimation()

        val splash = AlphaAnimation(0.6f, 1.0f)

        splash.duration = 1000            "clear" -> {            }

        splash.interpolator = AccelerateDecelerateInterpolator()

        splash.repeatCount = Animation.INFINITE                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)        }

        splash.repeatMode = Animation.REVERSE

                        startAdvancedSunAnimation()    }

        // Scale animation (intensity)

        val intensity = ScaleAnimation(0.95f, 1.05f, 0.95f, 1.05f,            }

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)            "clouds" -> {    private fun startSunAnimation() {

        intensity.duration = 1200

        intensity.interpolator = AccelerateDecelerateInterpolator()                binding.ivWeatherIcon.setImageResource(R.drawable.ic_cloudy)         val rotateAnimation = RotateAnimation(

        intensity.repeatCount = Animation.INFINITE

        intensity.repeatMode = Animation.REVERSE                startAdvancedCloudAnimation()            0f, 360f,

        

        animationSet.addAnimation(drop)            }            Animation.RELATIVE_TO_SELF, 0.5f,

        animationSet.addAnimation(shake)

        animationSet.addAnimation(splash)            "rain" -> {            Animation.RELATIVE_TO_SELF, 0.5f

        animationSet.addAnimation(intensity)

                        binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)        )

        binding.ivWeatherIcon.startAnimation(animationSet)

    }                startAdvancedRainAnimation()        rotateAnimation.duration = 10000

    

    private fun startAdvancedDrizzleAnimation() {            }        rotateAnimation.repeatCount = Animation.INFINITE

        val animationSet = AnimationSet(true)

                    "drizzle" -> {        rotateAnimation.interpolator = LinearInterpolator()

        // Gentle drop animation

        val gentleDrop = TranslateAnimation(0f, 0f, -15f, 15f)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_rainy)        binding.ivWeatherIcon.startAnimation(rotateAnimation)

        gentleDrop.duration = 1500

        gentleDrop.interpolator = AccelerateDecelerateInterpolator()                startAdvancedDrizzleAnimation()    }

        gentleDrop.repeatCount = Animation.INFINITE

        gentleDrop.repeatMode = Animation.REVERSE            }

        

        // Soft alpha animation            "thunderstorm" -> {    private fun startCloudAnimation() {

        val softAlpha = AlphaAnimation(0.7f, 1.0f)

        softAlpha.duration = 2000                binding.ivWeatherIcon.setImageResource(R.drawable.ic_thunderstorm)        val translateAnimation = TranslateAnimation(

        softAlpha.interpolator = AccelerateDecelerateInterpolator()

        softAlpha.repeatCount = Animation.INFINITE                startAdvancedThunderstormAnimation()            Animation.RELATIVE_TO_SELF, -0.05f,

        softAlpha.repeatMode = Animation.REVERSE

                    }            Animation.RELATIVE_TO_SELF, 0.05f,

        animationSet.addAnimation(gentleDrop)

        animationSet.addAnimation(softAlpha)            "snow" -> {            Animation.RELATIVE_TO_SELF, 0f,

        

        binding.ivWeatherIcon.startAnimation(animationSet)                binding.ivWeatherIcon.setImageResource(R.drawable.ic_snowy)            Animation.RELATIVE_TO_SELF, 0f

    }

                    startAdvancedSnowAnimation()        )

    private fun startAdvancedThunderstormAnimation() {

        val animationSet = AnimationSet(false) // Sequential animations            }        translateAnimation.duration = 4000

        

        // Flash effect (Alpha)            "mist", "fog", "haze" -> {        translateAnimation.repeatCount = Animation.INFINITE

        val flash = AlphaAnimation(1.0f, 0.3f)

        flash.duration = 150                binding.ivWeatherIcon.setImageResource(R.drawable.ic_mist)        translateAnimation.repeatMode = Animation.REVERSE

        flash.repeatCount = 3

        flash.repeatMode = Animation.REVERSE                startAdvancedMistAnimation()        translateAnimation.interpolator = AccelerateDecelerateInterpolator()

        

        // Shake effect (Translate)            }        binding.ivWeatherIcon.startAnimation(translateAnimation)

        val shake = TranslateAnimation(-5f, 5f, -5f, 5f)

        shake.duration = 100            else -> {    }

        shake.interpolator = CycleInterpolator(6f)

        shake.repeatCount = 11                binding.ivWeatherIcon.setImageResource(R.drawable.ic_sunny)

        shake.startOffset = 600 // Start after flash

                        startAdvancedSunAnimation()    private fun startRainAnimation() {

        // Scale effect (dramatic)

        val dramatic = ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f,            }        val dropAnimation = TranslateAnimation(

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)        }            Animation.RELATIVE_TO_SELF, 0f,

        dramatic.duration = 300

        dramatic.interpolator = BounceInterpolator()    }            Animation.RELATIVE_TO_SELF, 0f,

        dramatic.repeatCount = 2

        dramatic.repeatMode = Animation.REVERSE            Animation.RELATIVE_TO_SELF, -0.1f,

        dramatic.startOffset = 1800 // Start after shake

            // 1. Animasi Matahari - Rotasi + Scale + Glow            Animation.RELATIVE_TO_SELF, 0.1f

        animationSet.addAnimation(flash)

        animationSet.addAnimation(shake)    private fun startAdvancedSunAnimation() {        )

        animationSet.addAnimation(dramatic)

                // Rotasi smooth berkelanjutan        dropAnimation.duration = 600

        binding.ivWeatherIcon.startAnimation(animationSet)

    }        val rotateAnimation = RotateAnimation(        dropAnimation.repeatCount = Animation.INFINITE

    

    private fun startAdvancedSnowAnimation() {            0f, 360f,        dropAnimation.repeatMode = Animation.REVERSE

        val animationSet = AnimationSet(true)

                    Animation.RELATIVE_TO_SELF, 0.5f,        dropAnimation.interpolator = BounceInterpolator()

        // Sway animation (X movement)

        val sway = TranslateAnimation(-15f, 15f, 0f, 0f)            Animation.RELATIVE_TO_SELF, 0.5f        binding.ivWeatherIcon.startAnimation(dropAnimation)

        sway.duration = 6000

        sway.interpolator = AccelerateDecelerateInterpolator()        )    }

        sway.repeatCount = Animation.INFINITE

        sway.repeatMode = Animation.REVERSE        rotateAnimation.duration = 12000 // 12 detik per putaran

        

        // Fall animation (Y movement)        rotateAnimation.repeatCount = Animation.INFINITE    private fun startDrizzleAnimation() {

        val fall = TranslateAnimation(0f, 0f, -20f, 20f)

        fall.duration = 4500        rotateAnimation.interpolator = LinearInterpolator()        val gentleAnimation = TranslateAnimation(

        fall.interpolator = LinearInterpolator()

        fall.repeatCount = Animation.INFINITE                    Animation.RELATIVE_TO_SELF, -0.03f,

        fall.repeatMode = Animation.REVERSE

                // Efek "breathing" atau pulsing seperti matahari bersinar            Animation.RELATIVE_TO_SELF, 0.03f,

        // Rotation animation

        val rotate = RotateAnimation(0f, 360f,        val scaleAnimation = ScaleAnimation(            Animation.RELATIVE_TO_SELF, -0.02f,

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)            1.0f, 1.15f, 1.0f, 1.15f,            Animation.RELATIVE_TO_SELF, 0.02f

        rotate.duration = 8000

        rotate.interpolator = LinearInterpolator()            Animation.RELATIVE_TO_SELF, 0.5f,        )

        rotate.repeatCount = Animation.INFINITE

                    Animation.RELATIVE_TO_SELF, 0.5f        gentleAnimation.duration = 2000

        // Fade animation

        val fade = AlphaAnimation(0.6f, 1.0f)        )        gentleAnimation.repeatCount = Animation.INFINITE

        fade.duration = 3000

        fade.interpolator = AccelerateDecelerateInterpolator()        scaleAnimation.duration = 3000        gentleAnimation.repeatMode = Animation.REVERSE

        fade.repeatCount = Animation.INFINITE

        fade.repeatMode = Animation.REVERSE        scaleAnimation.repeatCount = Animation.INFINITE        gentleAnimation.interpolator = AccelerateDecelerateInterpolator()

        

        // Scale animation        scaleAnimation.repeatMode = Animation.REVERSE        binding.ivWeatherIcon.startAnimation(gentleAnimation)

        val scale = ScaleAnimation(0.95f, 1.05f, 0.95f, 1.05f,

            Animation.RELATIVE_TO_SELF, 0.5f,        scaleAnimation.interpolator = AccelerateDecelerateInterpolator()    }

            Animation.RELATIVE_TO_SELF, 0.5f)

        scale.duration = 5000        

        scale.interpolator = AccelerateDecelerateInterpolator()

        scale.repeatCount = Animation.INFINITE        // Efek alpha untuk glow    private fun startThunderstormAnimation() {

        scale.repeatMode = Animation.REVERSE

                val alphaAnimation = AlphaAnimation(0.8f, 1.0f)        val flashAnimation = AlphaAnimation(1.0f, 0.3f)

        animationSet.addAnimation(sway)

        animationSet.addAnimation(fall)        alphaAnimation.duration = 2000        flashAnimation.duration = 200

        animationSet.addAnimation(rotate)

        animationSet.addAnimation(fade)        alphaAnimation.repeatCount = Animation.INFINITE        flashAnimation.repeatCount = 3

        animationSet.addAnimation(scale)

                alphaAnimation.repeatMode = Animation.REVERSE        flashAnimation.repeatMode = Animation.REVERSE

        binding.ivWeatherIcon.startAnimation(animationSet)

    }        alphaAnimation.interpolator = AccelerateDecelerateInterpolator()        binding.ivWeatherIcon.startAnimation(flashAnimation)

    

    private fun startAdvancedMistAnimation() {            }

        val animationSet = AnimationSet(true)

                val animationSet = AnimationSet(false)

        // Fade animation (ethereal effect)

        val fade = AlphaAnimation(0.4f, 0.9f)        animationSet.addAnimation(rotateAnimation)    private fun startSnowAnimation() {

        fade.duration = 4000

        fade.interpolator = AccelerateDecelerateInterpolator()        animationSet.addAnimation(scaleAnimation)        val swayFall = TranslateAnimation(

        fade.repeatCount = Animation.INFINITE

        fade.repeatMode = Animation.REVERSE        animationSet.addAnimation(alphaAnimation)            Animation.RELATIVE_TO_SELF, -0.1f,

        

        // Drift X animation        binding.ivWeatherIcon.startAnimation(animationSet)            Animation.RELATIVE_TO_SELF, 0.1f,

        val driftX = TranslateAnimation(-25f, 25f, 0f, 0f)

        driftX.duration = 6000    }            Animation.RELATIVE_TO_SELF, 0f,

        driftX.interpolator = AccelerateDecelerateInterpolator()

        driftX.repeatCount = Animation.INFINITE            Animation.RELATIVE_TO_SELF, 0f

        driftX.repeatMode = Animation.REVERSE

            // 2. Animasi Awan - Floating complex dengan depth        )

        // Drift Y animation

        val driftY = TranslateAnimation(0f, 0f, -8f, 8f)    private fun startAdvancedCloudAnimation() {        swayFall.duration = 5000

        driftY.duration = 5000

        driftY.interpolator = AccelerateDecelerateInterpolator()        // Floating horizontal        swayFall.repeatCount = Animation.INFINITE

        driftY.repeatCount = Animation.INFINITE

        driftY.repeatMode = Animation.REVERSE        val translateX = TranslateAnimation(        swayFall.repeatMode = Animation.REVERSE

        

        // Breathing scale animation            Animation.RELATIVE_TO_SELF, -0.08f,        swayFall.interpolator = AccelerateDecelerateInterpolator()

        val breathing = ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f,

            Animation.RELATIVE_TO_SELF, 0.5f,            Animation.RELATIVE_TO_SELF, 0.08f,        binding.ivWeatherIcon.startAnimation(swayFall)

            Animation.RELATIVE_TO_SELF, 0.5f)

        breathing.duration = 7000            Animation.RELATIVE_TO_SELF, 0f,    }

        breathing.interpolator = AccelerateDecelerateInterpolator()

        breathing.repeatCount = Animation.INFINITE            Animation.RELATIVE_TO_SELF, 0f

        breathing.repeatMode = Animation.REVERSE

                )    private fun startMistAnimation() {

        // Slow rotation

        val slowRotate = RotateAnimation(0f, 360f,        translateX.duration = 5000        val fadeAnimation = AlphaAnimation(0.4f, 1.0f)

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f)        translateX.repeatCount = Animation.INFINITE        fadeAnimation.duration = 3500

        slowRotate.duration = 10000

        slowRotate.interpolator = LinearInterpolator()        translateX.repeatMode = Animation.REVERSE        fadeAnimation.repeatCount = Animation.INFINITE

        slowRotate.repeatCount = Animation.INFINITE

                translateX.interpolator = AccelerateDecelerateInterpolator()        fadeAnimation.repeatMode = Animation.REVERSE

        animationSet.addAnimation(fade)

        animationSet.addAnimation(driftX)                fadeAnimation.interpolator = AccelerateDecelerateInterpolator()

        animationSet.addAnimation(driftY)

        animationSet.addAnimation(breathing)        // Floating vertical        binding.ivWeatherIcon.startAnimation(fadeAnimation)

        animationSet.addAnimation(slowRotate)

                val translateY = TranslateAnimation(    }

        binding.ivWeatherIcon.startAnimation(animationSet)

    }            Animation.RELATIVE_TO_SELF, 0f,

    

    private fun startLiveTime() {            Animation.RELATIVE_TO_SELF, 0f,    private fun setBackground(weatherMain: String) {

        timeHandler = Handler(Looper.getMainLooper())

        timeRunnable = Runnable {            Animation.RELATIVE_TO_SELF, -0.03f,        val backgroundResource = when (weatherMain.lowercase()) {

            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            binding.tvCurrentTime.text = currentTime            Animation.RELATIVE_TO_SELF, 0.03f            "clear" -> R.drawable.sunny_background

            timeHandler.postDelayed(timeRunnable, 1000)

        }        )            "clouds" -> R.drawable.cloudy_background

        timeHandler.post(timeRunnable)

    }        translateY.duration = 4000            "rain", "drizzle" -> R.drawable.rainy_background

    

    override fun onDestroy() {        translateY.repeatCount = Animation.INFINITE            "thunderstorm" -> R.drawable.rainy_background

        super.onDestroy()

        timeHandler.removeCallbacks(timeRunnable)        translateY.repeatMode = Animation.REVERSE            "snow" -> R.drawable.snowy_background

        binding.ivWeatherIcon.clearAnimation()

    }        translateY.interpolator = AccelerateDecelerateInterpolator()            else -> R.drawable.sunny_background

}
                }

        // Efek depth dengan scale        

        val scaleAnimation = ScaleAnimation(        binding.root.background = ContextCompat.getDrawable(this, backgroundResource)

            1.0f, 1.05f, 1.0f, 1.05f,    }

            Animation.RELATIVE_TO_SELF, 0.5f,

            Animation.RELATIVE_TO_SELF, 0.5f    private fun startTimeUpdates() {

        )        timeRunnable = object : Runnable {

        scaleAnimation.duration = 6000            override fun run() {

        scaleAnimation.repeatCount = Animation.INFINITE                val currentTime = Date()

        scaleAnimation.repeatMode = Animation.REVERSE                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        scaleAnimation.interpolator = AccelerateDecelerateInterpolator()                val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

                        

        // Alpha untuk efek kabut                binding.tvCurrentTime.text = timeFormat.format(currentTime)

        val alphaAnimation = AlphaAnimation(0.7f, 1.0f)                binding.tvDate.text = dateFormat.format(currentTime)

        alphaAnimation.duration = 3500                

        alphaAnimation.repeatCount = Animation.INFINITE                timeHandler.postDelayed(this, 1000)

        alphaAnimation.repeatMode = Animation.REVERSE            }

                }

        val animationSet = AnimationSet(false)        timeRunnable?.let { timeHandler.post(it) }

        animationSet.addAnimation(translateX)    }

        animationSet.addAnimation(translateY)

        animationSet.addAnimation(scaleAnimation)    override fun onDestroy() {

        animationSet.addAnimation(alphaAnimation)        super.onDestroy()

        binding.ivWeatherIcon.startAnimation(animationSet)        timeRunnable?.let { timeHandler.removeCallbacks(it) }

    }        binding.ivWeatherIcon.clearAnimation()

    }

    // 3. Animasi Hujan - Drop + Shake + Splash}

    private fun startAdvancedRainAnimation() {
        // Efek tetesan turun
        val dropAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -0.15f,
            Animation.RELATIVE_TO_SELF, 0.15f
        )
        dropAnimation.duration = 800
        dropAnimation.repeatCount = Animation.INFINITE
        dropAnimation.repeatMode = Animation.REVERSE
        dropAnimation.interpolator = BounceInterpolator()
        
        // Shake horizontal untuk efek angin
        val shakeX = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.03f,
            Animation.RELATIVE_TO_SELF, 0.03f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        shakeX.duration = 200
        shakeX.repeatCount = Animation.INFINITE
        shakeX.repeatMode = Animation.REVERSE
        shakeX.interpolator = CycleInterpolator(2f)
        
        // Alpha untuk efek intensitas hujan
        val alphaAnimation = AlphaAnimation(0.6f, 1.0f)
        alphaAnimation.duration = 1000
        alphaAnimation.repeatCount = Animation.INFINITE
        alphaAnimation.repeatMode = Animation.REVERSE
        
        // Scale untuk efek splash
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.08f, 1.0f, 1.08f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 1200
        scaleAnimation.repeatCount = Animation.INFINITE
        scaleAnimation.repeatMode = Animation.REVERSE
        scaleAnimation.interpolator = AccelerateDecelerateInterpolator()
        
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(dropAnimation)
        animationSet.addAnimation(shakeX)
        animationSet.addAnimation(alphaAnimation)
        animationSet.addAnimation(scaleAnimation)
        binding.ivWeatherIcon.startAnimation(animationSet)
    }

    // 4. Animasi Gerimis - Gentle swaying
    private fun startAdvancedDrizzleAnimation() {
        // Swaying lembut
        val swayAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.05f,
            Animation.RELATIVE_TO_SELF, 0.05f,
            Animation.RELATIVE_TO_SELF, -0.03f,
            Animation.RELATIVE_TO_SELF, 0.03f
        )
        swayAnimation.duration = 2500
        swayAnimation.repeatCount = Animation.INFINITE
        swayAnimation.repeatMode = Animation.REVERSE
        swayAnimation.interpolator = AccelerateDecelerateInterpolator()
        
        // Gentle scale
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.03f, 1.0f, 1.03f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 3000
        scaleAnimation.repeatCount = Animation.INFINITE
        scaleAnimation.repeatMode = Animation.REVERSE
        scaleAnimation.interpolator = AccelerateDecelerateInterpolator()
        
        // Soft alpha
        val alphaAnimation = AlphaAnimation(0.8f, 1.0f)
        alphaAnimation.duration = 2000
        alphaAnimation.repeatCount = Animation.INFINITE
        alphaAnimation.repeatMode = Animation.REVERSE
        
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(swayAnimation)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(alphaAnimation)
        binding.ivWeatherIcon.startAnimation(animationSet)
    }

    // 5. Animasi Badai - Flash + Shake + Scale
    private fun startAdvancedThunderstormAnimation() {
        // Lightning flash effect
        val flashAnimation = AlphaAnimation(1.0f, 0.2f)
        flashAnimation.duration = 150
        flashAnimation.repeatCount = 4
        flashAnimation.repeatMode = Animation.REVERSE
        flashAnimation.interpolator = AccelerateInterpolator()
        
        // Thunder shake
        val shakeAnimation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.08f,
            Animation.RELATIVE_TO_SELF, 0.08f,
            Animation.RELATIVE_TO_SELF, -0.06f,
            Animation.RELATIVE_TO_SELF, 0.06f
        )
        shakeAnimation.duration = 100
        shakeAnimation.repeatCount = 12
        shakeAnimation.repeatMode = Animation.REVERSE
        shakeAnimation.interpolator = CycleInterpolator(4f)
        
        // Dramatic scale pulse
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.25f, 1.0f, 1.25f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 300
        scaleAnimation.repeatCount = 3
        scaleAnimation.repeatMode = Animation.REVERSE
        scaleAnimation.interpolator = BounceInterpolator()
        
        // Sequential animation - flash first, then shake and scale
        flashAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val stormSet = AnimationSet(false)
                stormSet.addAnimation(shakeAnimation)
                stormSet.addAnimation(scaleAnimation)
                binding.ivWeatherIcon.startAnimation(stormSet)
            }
        })
        
        binding.ivWeatherIcon.startAnimation(flashAnimation)
    }

    // 6. Animasi Salju - Complex falling with rotation
    private fun startAdvancedSnowAnimation() {
        // Swaying fall motion
        val swayFall = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.12f,
            Animation.RELATIVE_TO_SELF, 0.12f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        swayFall.duration = 6000
        swayFall.repeatCount = Animation.INFINITE
        swayFall.repeatMode = Animation.REVERSE
        swayFall.interpolator = AccelerateDecelerateInterpolator()
        
        // Vertical falling motion
        val fallMotion = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -0.18f,
            Animation.RELATIVE_TO_SELF, 0.18f
        )
        fallMotion.duration = 4500
        fallMotion.repeatCount = Animation.INFINITE
        fallMotion.repeatMode = Animation.REVERSE
        fallMotion.interpolator = AccelerateDecelerateInterpolator()
        
        // Snowflake rotation
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 8000
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.interpolator = LinearInterpolator()
        
        // Ethereal fade
        val fadeAnimation = AlphaAnimation(0.6f, 1.0f)
        fadeAnimation.duration = 3000
        fadeAnimation.repeatCount = Animation.INFINITE
        fadeAnimation.repeatMode = Animation.REVERSE
        
        // Gentle scale for depth
        val scaleAnimation = ScaleAnimation(
            0.9f, 1.1f, 0.9f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 5000
        scaleAnimation.repeatCount = Animation.INFINITE
        scaleAnimation.repeatMode = Animation.REVERSE
        scaleAnimation.interpolator = AccelerateDecelerateInterpolator()
        
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(swayFall)
        animationSet.addAnimation(fallMotion)
        animationSet.addAnimation(rotateAnimation)
        animationSet.addAnimation(fadeAnimation)
        animationSet.addAnimation(scaleAnimation)
        binding.ivWeatherIcon.startAnimation(animationSet)
    }

    // 7. Animasi Kabut - Ethereal drift
    private fun startAdvancedMistAnimation() {
        // Ethereal fade in/out
        val fadeAnimation = AlphaAnimation(0.3f, 0.9f)
        fadeAnimation.duration = 4000
        fadeAnimation.repeatCount = Animation.INFINITE
        fadeAnimation.repeatMode = Animation.REVERSE
        fadeAnimation.interpolator = AccelerateDecelerateInterpolator()
        
        // Complex drift pattern
        val driftX = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.15f,
            Animation.RELATIVE_TO_SELF, 0.15f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        driftX.duration = 6000
        driftX.repeatCount = Animation.INFINITE
        driftX.repeatMode = Animation.REVERSE
        driftX.interpolator = AccelerateDecelerateInterpolator()
        
        val driftY = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -0.08f,
            Animation.RELATIVE_TO_SELF, 0.08f
        )
        driftY.duration = 5000
        driftY.repeatCount = Animation.INFINITE
        driftY.repeatMode = Animation.REVERSE
        driftY.interpolator = AccelerateDecelerateInterpolator()
        
        // Breathing scale effect
        val breatheAnimation = ScaleAnimation(
            0.85f, 1.15f, 0.85f, 1.15f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        breatheAnimation.duration = 7000
        breatheAnimation.repeatCount = Animation.INFINITE
        breatheAnimation.repeatMode = Animation.REVERSE
        breatheAnimation.interpolator = AccelerateDecelerateInterpolator()
        
        // Subtle rotation
        val rotateAnimation = RotateAnimation(
            0f, 30f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnimation.duration = 10000
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.repeatMode = Animation.REVERSE
        rotateAnimation.interpolator = AccelerateDecelerateInterpolator()
        
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(fadeAnimation)
        animationSet.addAnimation(driftX)
        animationSet.addAnimation(driftY)
        animationSet.addAnimation(breatheAnimation)
        animationSet.addAnimation(rotateAnimation)
        binding.ivWeatherIcon.startAnimation(animationSet)
    }

    // Set background sesuai cuaca
    private fun setBackground(weatherMain: String) {
        val backgroundResource = when (weatherMain.lowercase()) {
            "clear" -> R.drawable.sunny_background
            "clouds" -> R.drawable.cloudy_background
            "rain", "drizzle" -> R.drawable.rainy_background
            "thunderstorm" -> R.drawable.rainy_background
            "snow" -> R.drawable.snowy_background
            else -> R.drawable.sunny_background
        }
        
        binding.root.background = ContextCompat.getDrawable(this, backgroundResource)
    }

    // Update waktu real-time
    private fun startTimeUpdates() {
        timeRunnable = object : Runnable {
            override fun run() {
                val currentTime = Date()
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                
                binding.tvCurrentTime.text = timeFormat.format(currentTime)
                binding.tvDate.text = dateFormat.format(currentTime)
                
                timeHandler.postDelayed(this, 1000)
            }
        }
        timeRunnable?.let { timeHandler.post(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeRunnable?.let { timeHandler.removeCallbacks(it) }
        binding.ivWeatherIcon.clearAnimation()
    }
}