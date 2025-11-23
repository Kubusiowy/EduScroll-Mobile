package com.example.eduscroll.NavHost

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eduscroll.ScreensForApp.HomeScreen
import com.example.eduscroll.ScreensForApp.InterestFormScreen
import com.example.eduscroll.ScreensForApp.LessonScreen
import com.example.eduscroll.ScreensForApp.LoginScreen
import com.example.eduscroll.ScreensForApp.SplashScreen
import com.example.eduscroll.ScreensSealed.Screens


@Composable
fun NavHostMain(navController: NavHostController) {

    NavHost(navController = navController, startDestination = Screens.SplashScreen.route ) {

        composable(route = Screens.SplashScreen.route) {
            SplashScreen(navController = navController)
        }

        composable(route = Screens.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        composable(Screens.InterestFormScreen.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 1
            InterestFormScreen(
                navController = navController,
                userId = userId
            )
        }

        composable(Screens.HomeScreen.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 1
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: -1

            HomeScreen(
                navController = navController,
                userId = userId,
                categoryId = categoryId
            )
        }

        composable(Screens.LessonScreen.route) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")?.toIntOrNull() ?: -1
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 1

            LessonScreen(
                navController = navController,
                lessonId = lessonId,
                userId = userId
            )
        }


    }



}