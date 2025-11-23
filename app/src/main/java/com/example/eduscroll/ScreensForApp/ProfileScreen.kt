package com.example.eduscroll.ScreensForApp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduscroll.ScreensSealed.Screens
import com.example.eduscroll.ViewModels.ProfileViewModel
import com.example.eduscroll.StorageOperations.UserPrefs

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
        profileViewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil", fontSize = 20.sp) },


                navigationIcon = {
                    IconButton(
                        onClick = {
                            val preferredCategory = UserPrefs.getPreferredCategory(context) ?: 1
                            navController.navigate(
                                Screens.HomeScreen.pass(userId, preferredCategory)
                            ) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć",
                            tint = MaterialTheme.colorScheme.primary
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

                        // Avatar + nazwa
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Gość #$userId",
                                fontSize = 20.sp
                            )
                        }

                        // Statystyki
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Poziom: ${state.level}")
                                Text("EXP: ${state.exp}")
                                Text("Zaliczone lekcje: ${state.totalLessons}")
                                Text("Poprawne odpowiedzi: ${state.totalCorrect}")
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))


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
                    }
                }
            }
        }
    }
}