package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapp.R
import com.example.weatherapp.model.weather.Weather
import com.example.weatherapp.model.weather.WeatherType
import com.example.weatherapp.view.customFunctions.*
import com.example.weatherapp.view.internet.checkInternetConnection
import com.example.weatherapp.view.main.keepSplashOpened
import com.example.weatherapp.view.navigation.Destination
import com.example.weatherapp.view.theme.White
import com.example.weatherapp.viewModel.HomeViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.time.LocalDate

private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
private var latitude = mutableStateOf("")
private var longitude = mutableStateOf("")
private var loading = mutableStateOf(false)
private var cancel = mutableStateOf(false)
private var error = mutableStateOf(false)
private var success = mutableStateOf(false)
private var clicked = mutableStateOf(false)
private lateinit var weather: Weather
private var itemID = ""
private var id = 0
private var viewModel: HomeViewModel = HomeViewModel()

@SuppressLint("StaticFieldLeak")
private lateinit var context: Context

@SuppressLint("MissingPermission")
@Composable
fun Home(navHostController: NavHostController) {
    keepSplashOpened = false
    context = LocalContext.current
    RequestPermissions()
    internet.value = checkInternetConnection(context)
    if (permissionsGranted.value && internet.value) {
        statusCheck(context)
        if (locationOn.value) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val task = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener {
                latitude.value = it.latitude.toString()
                longitude.value = it.longitude.toString()
            }
            if (latitude.value.isNotBlank() && longitude.value.isNotBlank()) {
                loading.value = true
                GetWeather()
            }
        } else {
            BuildAlertMessageNoGps(context)
        }
    }
    GetResult(navHostController)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
private fun HomeUI(navHostController: NavHostController) {
    val code = weather.current_weather.weathercode
    val weatherType = WeatherType.fromWMO(code)
    val weatherBackground = weatherType.background
    // Current Weather
    val temperature = weather.current_weather.temperature
    val windSpeed = weather.current_weather.windspeed
    // Units
    val temperatureUnit = weather.hourly_units.temperature_2m
    tempUnit = temperatureUnit
    val windSpeedUnit = weather.hourly_units.windspeed_10m
    windUnit = windSpeedUnit
    // Daily
    val maxTemperature = weather.daily.temperature_2m_max
    val minTemperature = weather.daily.temperature_2m_min
    val dailyWeatherCode = weather.daily.weathercode
    val dailyTime = weather.daily.time
    // Hourly
    val hourlyTime = weather.hourly.time
    val hourlyWeatherCode = weather.hourly.weathercode
    val hourlyTemperature = weather.hourly.temperature_2m
    val hourlyWindSpeed = weather.hourly.windspeed_10m

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(weatherBackground)
    ) {
        LazyColumn {
            item {
                TopBar()
                Spacer(modifier = Modifier.padding(20.dp))
                CurrentWeather(weatherType, temperature, windSpeed, temperatureUnit, windSpeedUnit)
                Spacer(modifier = Modifier.padding(5.dp))
            }
            item {
                WeatherDays(
                    dailyWeatherCode,
                    maxTemperature,
                    minTemperature,
                    temperatureUnit,
                    dailyTime,
                    navHostController,
                    hourlyTime,
                    hourlyWeatherCode,
                    hourlyTemperature,
                    hourlyWindSpeed
                )
            }
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.location),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier.size(
                if (mediaQueryWidth() <= small) {
                    40.dp
                } else if (mediaQueryWidth() <= normal) {
                    45.dp
                } else {
                    50.dp
                }
            )
        )
        Spacer(modifier = Modifier.padding(start = 10.dp))
        val cityName = getCityName(context, latitude.value.toDouble(), longitude.value.toDouble())
        if (cityName.isNotEmpty()) {
            Text(
                text = cityName,
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
}

@Composable
private fun CurrentWeather(
    weatherType: WeatherType,
    temperature: Double,
    windSpeed: Double,
    temperatureUnit: String,
    windSpeedUnit: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = null,
                colorFilter = ColorFilter.tint(White),
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
            Text(
                text = "$windSpeed $windSpeedUnit",
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
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Image(
            painter = painterResource(id = weatherType.iconRes),
            contentDescription = null,
            modifier = Modifier.size(
                if (mediaQueryWidth() <= small) {
                    150.dp
                } else if (mediaQueryWidth() <= normal) {
                    250.dp
                } else {
                    350.dp
                }
            )
        )
        Spacer(modifier = Modifier.padding(20.dp))
        Text(
            text = weatherType.weatherDesc,
            color = White,
            fontSize =
            if (mediaQueryWidth() <= small) {
                30.sp
            } else if (mediaQueryWidth() <= normal) {
                35.sp
            } else {
                40.sp
            },
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = "$temperature $temperatureUnit",
            color = White,
            fontSize =
            if (mediaQueryWidth() <= small) {
                50.sp
            } else if (mediaQueryWidth() <= normal) {
                55.sp
            } else {
                60.sp
            },
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WeatherDays(
    dailyWeatherCode: List<Int>,
    maxTemperature: List<Double>,
    minTemperature: List<Double>,
    temperatureUnit: String,
    dailyTime: List<String>,
    navHostController: NavHostController,
    hourlyTime: List<String>,
    hourlyWeatherCode: List<Int>,
    hourlyTemperature: List<Double>,
    hourlyWindSpeed: List<Double>
) {
    LazyRow {
        items(dailyWeatherCode.size) { i ->
            if (clicked.value) {
                clicked.value = false
                val daysList = itemID
                val hourliesList: ArrayList<String> = ArrayList()
                val hourliesWeatherCode: ArrayList<Int> = ArrayList()
                val hourliesTemperature: ArrayList<Double> = ArrayList()
                val hourliesWindSpeed: ArrayList<Double> = ArrayList()
                for (y in hourlyTime.indices) {
                    if (hourlyTime[y].contains(daysList)) {
                        hourliesList.add(hourlyTime[y])
                        hourliesWeatherCode.add(hourlyWeatherCode[y])
                        hourliesTemperature.add(hourlyTemperature[y])
                        hourliesWindSpeed.add(hourlyWindSpeed[y])
                    }
                }
                itemID = ""
                navHostController.navigate(
                    Destination.SpecificDay.passArgument(
                        hourliesList.toString(),
                        hourliesWeatherCode.toString(),
                        hourliesTemperature.toString(),
                        hourliesWindSpeed.toString(),
                        dailyTime[id],
                        dailyWeatherCode[id]
                    )
                )
            }
            val daysList = remember {
                LocalDate.parse(dailyTime[i])
            }
            val weatherType = WeatherType.fromWMO(dailyWeatherCode[i])
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${daysList.dayOfWeek}",
                        color = White,
                        fontSize =
                        if (mediaQueryWidth() <= small) {
                            15.sp
                        } else if (mediaQueryWidth() <= normal) {
                            20.sp
                        } else {
                            25.sp
                        },
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Column(
                        modifier = Modifier
                            .width(
                                if (mediaQueryWidth() <= small) {
                                    150.dp
                                } else if (mediaQueryWidth() <= normal) {
                                    180.dp
                                } else {
                                    210.dp
                                }
                            )
                            .border(
                                border = BorderStroke(width = 3.dp, color = White)
                            )
                            .background(weatherType.background)
                            .clickable {
                                clicked.value = true
                                itemID = dailyTime[i]
                                id = i
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = weatherType.weatherDesc,
                            color = White,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                15.sp
                            } else if (mediaQueryWidth() <= normal) {
                                17.sp
                            } else {
                                20.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            modifier = Modifier
                                .width(
                                    if (mediaQueryWidth() <= small) {
                                        130.dp
                                    } else if (mediaQueryWidth() <= normal) {
                                        160.dp
                                    } else {
                                        190.dp
                                    }
                                )
                        )
                        Image(
                            painter = painterResource(id = weatherType.iconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(
                                    if (mediaQueryWidth() <= small) {
                                        120.dp
                                    } else if (mediaQueryWidth() <= normal) {
                                        150.dp
                                    } else {
                                        180.dp
                                    }
                                )
                                .padding(20.dp)
                        )
                        Text(
                            text = "${maxTemperature[i]} $temperatureUnit",
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
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "${minTemperature[i]} $temperatureUnit",
                            color = White,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                15.sp
                            } else if (mediaQueryWidth() <= normal) {
                                20.sp
                            } else {
                                25.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

@Composable
private fun GetResult(navHostController: NavHostController) {
    Loading(isLoading = loading.value)
    when {
        !internet.value -> {
            NoInternet(context)
        }
        success.value -> {
            HomeUI(navHostController)
        }
        error.value -> {
            Error(context)
        }
        cancel.value -> {
            NoInternet(context)
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun GetWeather() {
    val startDate = remember {
        LocalDate.now()
    }
    val endDate = startDate.plusDays(6)
    GlobalScope.launch {
        viewModel.getWeather(
            latitude.value,
            longitude.value,
            start_date = startDate.toString(),
            end_date = endDate.toString(),
            context
        )
        viewModel.getWeatherUIState.collect {
            when (it) {
                HomeViewModel.GetWeatherUIState.Cancel -> {
                    loading.value = false
                    cancel.value = true
                }
                HomeViewModel.GetWeatherUIState.Error -> {
                    loading.value = false
                    error.value = true
                }
                is HomeViewModel.GetWeatherUIState.Success -> {
                    weather = it.weather
                    loading.value = false
                    success.value = true
                }
            }
        }
    }
}