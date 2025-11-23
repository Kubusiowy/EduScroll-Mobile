package com.example.eduscroll.ScreensSealed

sealed class Screens(val route: String) {

    object SplashScreen : Screens("splash_screen")

    object LoginScreen : Screens("login_screen")

    object InterestFormScreen : Screens("interest_form_screen/{userId}") {
        fun passId(userId: Int) = "interest_form_screen/$userId"
    }

    object HomeScreen : Screens("home_screen/{userId}/{categoryId}") {
        fun pass(userId: Int, categoryId: Int) =
            "home_screen/$userId/$categoryId"
    }
    
    object LessonScreen : Screens("lesson_screen/{lessonId}/{userId}") {
        fun pass(lessonId: Int, userId: Int) =
            "lesson_screen/$lessonId/$userId"
    }

}
