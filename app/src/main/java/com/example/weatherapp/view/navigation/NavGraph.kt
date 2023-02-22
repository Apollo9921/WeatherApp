package com.example.weatherapp.view.navigation

import androidx.compose.animation.*
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.weatherapp.view.Home
import com.example.weatherapp.view.SpecificDay
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedNavGraph(navHostController: NavHostController) {
    AnimatedNavHost(navController = navHostController, startDestination = Destination.Home.route) {
        composable(
            route = Destination.Home.route
        ) {
            Home(navHostController)
        }
        composable(
            route = Destination.SpecificDay.route,
            arguments = listOf(
                navArgument("hourlyTime") {
                    type = NavType.StringType
                },
                navArgument("hourlyWeatherCode") {
                    type = NavType.StringType
                },
                navArgument("hourlyTemperature") {
                    type = NavType.StringType
                },
                navArgument("hourlyWindSpeed") {
                    type = NavType.StringType
                },
                navArgument("dailyTime") {
                    type = NavType.StringType
                },
                navArgument("dailyWeatherCode") {
                    type = NavType.IntType
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            }
        ) {
            SpecificDay(
                navHostController = navHostController,
                hourlyTime = it.arguments?.getString("hourlyTime"),
                hourlyWeatherCode = it.arguments?.getString("hourlyWeatherCode"),
                hourlyTemperature = it.arguments?.getString("hourlyTemperature"),
                hourlyWindSpeed = it.arguments?.getString("hourlyWindSpeed"),
                dailyTime = it.arguments?.getString("dailyTime"),
                dailyWeatherCode = it.arguments?.getInt("dailyWeatherCode")
            )
        }
    }
}