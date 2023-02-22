package com.example.weatherapp.view.navigation

sealed class Destination(val route: String) {
    object Home : Destination(route = "home")
    object SpecificDay :
        Destination(
            route = "specific_day/{hourlyTime}/{hourlyWeatherCode}/{hourlyTemperature}/{hourlyWindSpeed}/{dailyTime}/{dailyWeatherCode}"
        ) {
        fun passArgument(
            hourlyTime: String,
            hourlyWeatherCode: String,
            hourlyTemperature: String,
            hourlyWindSpeed: String,
            dailyTime: String,
            dailyWeatherCode: Int
        ): String {
            return "specific_day/$hourlyTime/$hourlyWeatherCode/$hourlyTemperature/$hourlyWindSpeed/$dailyTime/$dailyWeatherCode"
        }
    }
}
