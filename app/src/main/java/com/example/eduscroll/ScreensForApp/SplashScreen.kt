package com.example.eduscroll.ScreensForApp

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eduscroll.R
import com.example.eduscroll.ScreensSealed.Screens
import com.example.eduscroll.StorageOperations.UserPrefs
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        delay(2500)

        val isGuest = UserPrefs.isGuestLogged(context)
        val userId = UserPrefs.getUserId(context) ?: 1
        val preferredCategory = UserPrefs.getPreferredCategory(context)

        when {

            isGuest && preferredCategory != null -> {
                navController.navigate(
                    Screens.HomeScreen.pass(
                        userId = userId,
                        categoryId = preferredCategory
                    )
                ) {
                    popUpTo(Screens.SplashScreen.route) { inclusive = true }
                }
            }


            isGuest -> {
                navController.navigate(
                    Screens.InterestFormScreen.passId(userId)
                ) {
                    popUpTo(Screens.SplashScreen.route) { inclusive = true }
                }
            }


            else -> {
                navController.navigate(Screens.LoginScreen.route) {
                    popUpTo(Screens.SplashScreen.route) { inclusive = true }
                }
            }
        }
    }


    val infinite = rememberInfiniteTransition(label = "splash_anim")

    val scale by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(1200, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    val alpha by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1200),
            RepeatMode.Reverse
        ),
        label = "logo_alpha"
    )


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logoessa),
                contentDescription = "Logo EduScroll",
                modifier = Modifier
                    .size(210.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale
                    )
                    .alpha(alpha)
            )

            Text(
                text = "Edukacja cyfrowa dla każdego",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .alpha(alpha)
            )
        }
    }
}
