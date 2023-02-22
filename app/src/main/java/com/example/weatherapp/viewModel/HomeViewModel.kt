package com.example.weatherapp.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.retrofit.NetworkModule
import com.example.weatherapp.model.weather.Weather
import com.example.weatherapp.view.customFunctions.internet
import com.example.weatherapp.view.internet.checkInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import retrofit2.awaitResponse

class HomeViewModel: ViewModel() {
    private var checking = 0

    private val _getWeatherUIState = MutableStateFlow<GetWeatherUIState>(GetWeatherUIState.Error)
    val getWeatherUIState: StateFlow<GetWeatherUIState> = _getWeatherUIState
    private var networkModule = NetworkModule()

    sealed class GetWeatherUIState {
        data class Success(val weather: Weather): GetWeatherUIState()
        object Error: GetWeatherUIState()
        object Cancel: GetWeatherUIState()
    }

    fun getWeather(
        latitude: String,
        longitude: String,
        start_date: String,
        end_date: String,
        context: Context
    ) {
        runBlocking {
            val fields = HashMap<String, String>()
            fields["latitude"] = latitude
            fields["longitude"] = longitude
            fields["start_date"] = start_date
            fields["end_date"] = end_date

            val call = networkModule.retrofitInterface().getWeather(fields).awaitResponse()
            checking = 0
            while (checking == 0) {
                internet.value = checkInternetConnection(context)
                if (internet.value) {
                    if (call.isSuccessful) {
                        _getWeatherUIState.value = GetWeatherUIState.Success(call.body()!!)
                        checking++
                    } else {
                        _getWeatherUIState.value = GetWeatherUIState.Error
                        checking++
                    }
                } else {
                    networkModule.retrofitInterface().getWeather(fields).cancel()
                    _getWeatherUIState.value = GetWeatherUIState.Cancel
                    checking++
                }
            }
        }
    }
}