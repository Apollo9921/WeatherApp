package com.example.weatherapp.view.customFunctions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.view.GetWeather
import com.example.weatherapp.view.internet.checkInternetConnection
import com.example.weatherapp.view.theme.Black
import com.example.weatherapp.view.theme.Thunder
import com.example.weatherapp.view.theme.White
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*

var permissionsGranted = mutableStateOf(false)
var locationOn = mutableStateOf(false)
var internet = mutableStateOf(true)

var tempUnit = ""
var windUnit = ""

val small = 600.dp
val normal = 840.dp

@Composable
fun mediaQueryWidth(): Dp {
    return LocalContext.current.resources.displayMetrics.widthPixels.dp / LocalDensity.current.density
}

@SuppressLint("ServiceCast")
fun statusCheck(context: Context) {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    locationOn.value = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

@Composable
fun BuildAlertMessageNoGps(context: Context) {
    AlertDialog(
        onDismissRequest = { locationOn.value },
        backgroundColor = Black,
        title = {
            Text(
                text = stringResource(id = R.string.enableLocation),
                color = White,
                fontSize =
                if (mediaQueryWidth() <= small) {
                    16.sp
                } else if (mediaQueryWidth() <= normal) {
                    20.sp
                } else {
                    24.sp
                },
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center
            )
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    runBlocking { delay(500) }
                    statusCheck(context)
                }) {
                    Text(
                        text = stringResource(id = R.string.tryAgain),
                        color = White,
                        fontSize =
                        if (mediaQueryWidth() <= small) {
                            13.sp
                        } else if (mediaQueryWidth() <= normal) {
                            17.sp
                        } else {
                            21.sp
                        },
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}

fun getCityName(context: Context, lat: Double, long: Double): String {
    var cityName: String? = null
    val geoCoder = Geocoder(context, Locale.getDefault())
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geoCoder.getFromLocation(
            lat, long, 1
        ) { addresses ->
            cityName = addresses[0].adminArea
            if (addresses[0].locality != null) {
                cityName = addresses[0].locality
            }
        }
    } else {
        val address: MutableList<Address>? = geoCoder.getFromLocation(lat, long, 1)
        cityName = address!![0].adminArea
        if (address[0].locality != null) {
            cityName = address[0].locality
        }
    }
    return cityName!!
}

@Composable
fun Loading(isLoading: Boolean) {
    if (isLoading) {
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Black.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(
                        if (mediaQueryWidth() <= small) {
                            100.dp
                        } else if (mediaQueryWidth() <= normal) {
                            150.dp
                        } else {
                            200.dp
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun NoInternet(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Thunder),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.wifi_off),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier.size(
                if (mediaQueryWidth() <= small) {
                    100.dp
                } else if (mediaQueryWidth() <= normal) {
                    150.dp
                } else {
                    200.dp
                }
            )
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = stringResource(id = R.string.noInternet),
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
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(
                    if (mediaQueryWidth() <= small) {
                        300.dp
                    } else if (mediaQueryWidth() <= normal) {
                        350.dp
                    } else {
                        400.dp
                    }
                )
                .clickable {
                    internet.value = checkInternetConnection(context)
                }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions() {
    val context = LocalContext.current
    val permissionOne = rememberPermissionState(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val permissionTwo = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    if (!permissionOne.status.isGranted) {
        AlertDialog(
            onDismissRequest = { },
            backgroundColor = Black,
            title = {
                val textToShow = if (permissionOne.status.shouldShowRationale) {
                    stringResource(id = R.string.permissionLocation)
                } else {
                    stringResource(id = R.string.requiredLocation)
                }
                Text(
                    text = textToShow,
                    color = White,
                    fontSize =
                    if (mediaQueryWidth() <= small) {
                        16.sp
                    } else if (mediaQueryWidth() <= normal) {
                        20.sp
                    } else {
                        24.sp
                    },
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        if (permissionOne.status.shouldShowRationale) {
                            permissionOne.launchPermissionRequest()
                        } else {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts(
                                        "package",
                                        context.packageName,
                                        null
                                    )
                                }
                            )
                        }
                    }) {
                        Text(
                            text = stringResource(id = R.string.requestPermission),
                            color = White,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                13.sp
                            } else if (mediaQueryWidth() <= normal) {
                                17.sp
                            } else {
                                21.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        )

    } else {
        permissionsGranted.value = true
    }
    if (!permissionTwo.status.isGranted) {
        AlertDialog(
            onDismissRequest = { },
            backgroundColor = Black,
            title = {
                val textToShow = if (permissionOne.status.shouldShowRationale) {
                    stringResource(id = R.string.permissionLocation)
                } else {
                    stringResource(id = R.string.requiredLocation)
                }
                Text(
                    text = textToShow,
                    color = White,
                    fontSize =
                    if (mediaQueryWidth() <= small) {
                        16.sp
                    } else if (mediaQueryWidth() <= normal) {
                        20.sp
                    } else {
                        24.sp
                    },
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )
            },
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(onClick = {
                        if (!permissionTwo.status.shouldShowRationale) {
                            permissionTwo.launchPermissionRequest()
                        } else {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts(
                                        "package",
                                        context.packageName,
                                        null
                                    )
                                }
                            )
                        }
                    }) {
                        Text(
                            text = stringResource(id = R.string.requestPermission),
                            color = White,
                            fontSize =
                            if (mediaQueryWidth() <= small) {
                                16.sp
                            } else if (mediaQueryWidth() <= normal) {
                                20.sp
                            } else {
                                24.sp
                            },
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        )
    } else {
        permissionsGranted.value = true
    }
}

@Composable
fun Error(context: Context) {
    val checkInternet = remember { mutableStateOf(false) }
    if (checkInternet.value) {
        GetWeather()
    } else {
        internet.value = false
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Thunder),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.error),
            contentDescription = null,
            colorFilter = ColorFilter.tint(White),
            modifier = Modifier.size(
                if (mediaQueryWidth() <= small) {
                    100.dp
                } else if (mediaQueryWidth() <= normal) {
                    150.dp
                } else {
                    200.dp
                }
            )
        )
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = stringResource(id = R.string.errorMessage),
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
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(
                    if (mediaQueryWidth() <= small) {
                        300.dp
                    } else if (mediaQueryWidth() <= normal) {
                        350.dp
                    } else {
                        400.dp
                    }
                )
                .clickable {
                    checkInternet.value = checkInternetConnection(context)
                }
        )
    }
}