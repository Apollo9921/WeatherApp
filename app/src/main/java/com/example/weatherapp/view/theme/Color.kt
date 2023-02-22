package com.example.weatherapp.view.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)

// Weather Background Colors=
// Cloud
val Background1 = Color(0xFF2f8e8e)
val Background2 = Color(0xFF2fb2ba)
val WithCloud = Brush.verticalGradient(colors =  listOf(Background1, Background2))

// Sun
val Background3 = Color(0xFFdedc1c)
val Background4 = Color(0xFFd7a20b)
val Sunny = Brush.verticalGradient(colors =  listOf(Background3, Background4))

// Rain
val Background5 = Color(0xFF219cd7)
val Background6 = Color(0xFF056796)
val Rain = Brush.verticalGradient(colors =  listOf(Background5, Background6))

// Snow
val Background7 = Color(0xFFd3dcdd)
val Background8 = Color(0xFF83939a)
val Snow = Brush.verticalGradient(colors =  listOf(Background7, Background8))

// Thunder
val Background9 = Color(0xFF96a3a4)
val Background10 = Color(0xFF424c50)
val Thunder = Brush.verticalGradient(colors =  listOf(Background9, Background10))