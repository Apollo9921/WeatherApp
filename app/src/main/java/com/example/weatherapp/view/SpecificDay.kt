package com.example.weatherapp.view

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.model.weather.WeatherType
import com.example.weatherapp.view.customFunctions.*
import com.example.weatherapp.view.navigation.Destination
import com.example.weatherapp.view.theme.Black
import com.example.weatherapp.view.theme.White
import java.time.LocalDateTime

private var hourlyTimeList: List<String> = ArrayList()
private var hourlyWeatherCodeList: List<String> = ArrayList()
private var hourlyTemperatureList: List<String> = ArrayList()
private var hourlyWindSpeedList: List<String> = ArrayList()
private var dailyTimeList: List<String> = ArrayList()

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SpecificDay(
    navHostController: NavHostController,
    hourlyTime: String?,
    hourlyWeatherCode: String?,
    hourlyTemperature: String?,
    hourlyWindSpeed: String?,
    dailyTime: String?,
    dailyWeatherCode: Int?
) {
    hourlyTimeList = hourlyTime!!.split(",")
    hourlyWeatherCodeList = hourlyWeatherCode!!.split(",")
    hourlyTemperatureList = hourlyTemperature!!.split(",")
    hourlyWindSpeedList = hourlyWindSpeed!!.split(",")
    dailyTimeList = dailyTime!!.split(",")

    val day = hourlyTimeList[0].split("[")
    val dayOfWeek = remember {
        LocalDateTime.parse(day[1]).dayOfWeek
    }.toString()

    val weatherCode = WeatherType.fromWMO(dailyWeatherCode!!)

    BackHandler {
        navHostController.popBackStack(Destination.Home.route, inclusive = true)
        navHostController.navigate(Destination.Home.route)
    }

    Scaffold(
        topBar = { TopBar(navHostController, dayOfWeek) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(weatherCode.background)
        ) {
            items(hourlyTimeList.size) { i ->
                HourlyList(i)
            }
        }
    }
}

@Composable
private fun TopBar(navHostController: NavHostController, dayOfWeek: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .padding(10.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = com.example.weatherapp.R.drawable.back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier
                .size(
                    if (mediaQueryWidth() <= small) {
                        40.dp
                    } else if (mediaQueryWidth() <= normal) {
                        60.dp
                    } else {
                        80.dp
                    }
                )
                .clickable {
                    navHostController.popBackStack(Destination.Home.route, inclusive = true)
                    navHostController.navigate(Destination.Home.route)
                }
        )
        Text(
            text = dayOfWeek,
            color = White,
            fontSize =
            if (mediaQueryWidth() <= small) {
                25.sp
            } else if (mediaQueryWidth() <= normal) {
                35.sp
            } else {
                40.sp
            },
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HourlyList(i: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val time = hourlyTimeList[i].split("T")
        val timing = if (i == hourlyTimeList.size - 1) {
            time[1].substring(0, time[1].length - 1)
        } else {
            time[1].substring(0, time[1].length)
        }

        Text(
            text = timing,
            color = White,
            fontSize =
            if (mediaQueryWidth() <= small) {
                20.sp
            } else if (mediaQueryWidth() <= normal) {
                25.sp
            } else {
                30.sp
            },
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(10.dp))
        val temperature = when (i) {
            0 -> {
                hourlyTemperatureList[i].substring(1, hourlyTemperatureList[i].length)
            }
            hourlyTemperatureList.size - 1 -> {
                hourlyTemperatureList[i].substring(0, hourlyTemperatureList[i].length - 1)
            }
            else -> {
                hourlyTemperatureList[i].substring(0, hourlyTemperatureList[i].length)
            }
        }
        Text(
            text = "$temperature $tempUnit",
            color = White,
            fontSize =
            if (mediaQueryWidth() <= small) {
                20.sp
            } else if (mediaQueryWidth() <= normal) {
                25.sp
            } else {
                30.sp
            },
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(10.dp))
        val weatherCode: WeatherType
        var code: List<String>
        when (i) {
            0 -> {
                code = hourlyWeatherCodeList[i].split("[")
                weatherCode = WeatherType.fromWMO(code[1].toInt())
            }
            hourlyWeatherCodeList.size - 1 -> {
                code = hourlyWeatherCodeList[i].split("]")
                code = code[0].split(" ")
                weatherCode = WeatherType.fromWMO(code[1].toInt())
            }
            else -> {
                val codeWeather = hourlyWeatherCodeList[i].split(" ")
                weatherCode = WeatherType.fromWMO(codeWeather[1].toInt())
            }
        }
        Image(
            painter = painterResource(weatherCode.iconRes),
            contentDescription = null,
            modifier = Modifier.size(
                if (mediaQueryWidth() <= small) {
                    30.dp
                } else if (mediaQueryWidth() <= normal) {
                    35.dp
                } else {
                    40.dp
                }
            )
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Image(
            painter = painterResource(com.example.weatherapp.R.drawable.wind),
            contentDescription = null,
            modifier = Modifier.size(
                if (mediaQueryWidth() <= small) {
                    30.dp
                } else if (mediaQueryWidth() <= normal) {
                    35.dp
                } else {
                    40.dp
                }
            )
        )
        val windSpeed = when (i) {
            0 -> {
                hourlyWindSpeedList[i].substring(1, hourlyWindSpeedList[i].length)
            }
            hourlyWindSpeedList.size - 1 -> {
                hourlyWindSpeedList[i].substring(0, hourlyWindSpeedList[i].length - 1)
            }
            else -> {
                hourlyWindSpeedList[i].substring(0, hourlyWindSpeedList[i].length)
            }
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = "$windSpeed $windUnit",
            color = White,
            fontSize =
            if (mediaQueryWidth() <= small) {
                20.sp
            } else if (mediaQueryWidth() <= normal) {
                25.sp
            } else {
                30.sp
            },
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
    }
}