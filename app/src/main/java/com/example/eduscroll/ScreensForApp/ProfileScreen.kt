package com.example.eduscroll.ScreensForApp

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduscroll.ScreensSealed.Screens
import com.example.eduscroll.StorageOperations.UserPrefs
import com.example.eduscroll.ViewModels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userId: Int,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state = profileViewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()  // 🔄 tu dalej leci prawdziwy stan z backendu
    }

    // animowane liczby – żeby ładnie „dojeżdżały”
    val animatedLevel by animateIntAsState(
        targetValue = state.level,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "level_anim"
    )

    val animatedExp by animateIntAsState(
        targetValue = state.exp,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "exp_anim"
    )

    val animatedLessons by animateIntAsState(
        targetValue = state.totalLessons,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "lessons_anim"
    )

    val animatedCorrect by animateIntAsState(
        targetValue = state.totalCorrect,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "correct_anim"
    )

    // progres do następnego levela – 50 exp na poziom
    val expToNextLevel = 50
    val currentLevelExp = state.exp % expToNextLevel
    val progressRaw = if (state.exp == 0) 0f else currentLevelExp / expToNextLevel.toFloat()
    val progressAnim by animateFloatAsState(
        targetValue = progressRaw.coerceIn(0f, 1f),
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    // pulsujący avatar
    val infinite = rememberInfiniteTransition(label = "avatar_pulse")
    val avatarScale by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil",
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val preferredCategory =
                                UserPrefs.getPreferredCategory(context) ?: 1
                            navController.navigate(
                                Screens.HomeScreen.pass(userId, preferredCategory)
                            ) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = "Błąd: ${state.error}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {

                        // 🔵 AVATAR z lekkim glow + puls
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(avatarScale),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.primary,
                                tonalElevation = 6.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(90.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Avatar",
                                        tint = Color.White,
                                        modifier = Modifier.size(70.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Gość #$userId",
                            fontSize = 20.sp
                        )

                        // 🌈 KAFEL ze statami + progress bar do kolejnego poziomu
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.9f
                                )
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Statystyki gracza",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text("Poziom: $animatedLevel")
                                Text("EXP: $animatedExp")
                                Text("Zaliczone lekcje: $animatedLessons")
                                Text("Poprawne odpowiedzi: $animatedCorrect")

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Postęp do następnego poziomu",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )

                                LinearProgressIndicator(
                                    progress = { progressAnim },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // 🔴 WYLOGOWANIE – czyścimy pamięć
                        Button(
                            onClick = {
                                UserPrefs.clearAll(context)

                                navController.navigate(Screens.LoginScreen.route) {
                                    popUpTo(Screens.SplashScreen.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Wyloguj się")
                        }

                        Text(
                            text = "Dane bazują na Twoim realnym progresie z serwera (prototyp EduScroll).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}