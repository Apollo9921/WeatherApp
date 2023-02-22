package com.example.weatherapp.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherapp.view.navigation.AnimatedNavGraph
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

var keepSplashOpened = true
@SuppressLint("StaticFieldLeak")
private lateinit var context: Context

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        setContent {
            context = LocalContext.current
            val navHostController = rememberAnimatedNavController()
            AnimatedNavGraph(navHostController)
        }
    }
}