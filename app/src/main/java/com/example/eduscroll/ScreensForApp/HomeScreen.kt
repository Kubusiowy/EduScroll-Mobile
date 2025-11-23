package com.example.eduscroll.ScreensForApp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduscroll.ScreensSealed.Screens
import com.example.eduscroll.ViewModels.HomeViewModel
import com.example.eduscroll.domain.LessonDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userId: Int,
    categoryId: Int,
    homeViewModel: HomeViewModel = viewModel()
) {

    LaunchedEffect(categoryId) {
        homeViewModel.loadLessons(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Twoje Lekcje",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, userId)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                homeViewModel.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                homeViewModel.error != null -> {
                    Text(
                        text = "Błąd: ${homeViewModel.error}",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                homeViewModel.lessons.isEmpty() -> {
                    Text(
                        text = "Brak lekcji dla tej kategorii 😶",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Lekcje: ${homeViewModel.lessons.size}",
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        )

                        LessonList(
                            lessons = homeViewModel.lessons,
                            completedLessons = homeViewModel.completedLessons,
                            navController = navController,
                            userId = userId
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun LessonList(
    lessons: List<LessonDto>,
    completedLessons: Set<Int>,
    navController: NavController,
    userId: Int
) {
    // paleta gradientów – każda lekcja inny vibe
    val gradients = listOf(
        listOf(Color(0xFF6366F1), Color(0xFF818CF8)), // fiolet
        listOf(Color(0xFF10B981), Color(0xFF34D399)), // zielony
        listOf(Color(0xFFF97316), Color(0xFFFBBF24)), // pomarańcz
        listOf(Color(0xFFEC4899), Color(0xFFF472B6)), // róż
        listOf(Color(0xFF0EA5E9), Color(0xFF22D3EE))  // niebieski
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        items(lessons.size) { index ->
            val lesson = lessons[index]
            val isLeft = index % 2 == 0
            val primary = MaterialTheme.colorScheme.primary

            val isCompleted = lesson.id in completedLessons
            val gradientColors =
                if (isCompleted) listOf(Color(0xFF16A34A), Color(0xFF22C55E)) // mocny zielony
                else gradients[index % gradients.size]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = 32.dp)
            ) {

                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f

                    // główna pionowa ścieżka
                    drawLine(
                        color = primary.copy(alpha = 0.18f),
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, size.height),
                        strokeWidth = 18f,
                        cap = StrokeCap.Round
                    )

                    // odnogi w lewo/prawo
                    val bubbleCenterX = if (isLeft) {
                        size.width * 0.2f
                    } else {
                        size.width * 0.8f
                    }

                    drawLine(
                        color = if (isCompleted)
                            Color(0xFF22C55E).copy(alpha = 0.9f)
                        else
                            primary.copy(alpha = 0.8f),
                        start = Offset(centerX, centerY),
                        end = Offset(bubbleCenterX, centerY),
                        strokeWidth = 20f,
                        cap = StrokeCap.Round
                    )
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = if (isLeft) Alignment.CenterStart else Alignment.CenterEnd
                ) {
                    LessonBubble(
                        lesson = lesson,
                        index = index,
                        gradientColors = gradientColors,
                        isCompleted = isCompleted,
                        onClick = {
                            navController.navigate(
                                Screens.LessonScreen.pass(
                                    lessonId = lesson.id,
                                    userId = userId
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LessonBubble(
    lesson: LessonDto,
    index: Int,
    gradientColors: List<Color>,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Surface(
        shape = CircleShape,
        color = Color.Transparent,
        shadowElevation = 18.dp,
        tonalElevation = 0.dp,
        modifier = Modifier
            .size(110.dp)
            .clickable(onClick = onClick)
    ) {

        Box(
            modifier = Modifier
                .background(
                    color = primary.copy(alpha = 0.18f),
                    shape = CircleShape
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(gradientColors),
                        shape = CircleShape
                    )
                    .border(
                        width = if (isCompleted) 3.dp else 2.dp,
                        color = if (isCompleted)
                            Color.White
                        else
                            Color.White.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = (index + 1).toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = lesson.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 2
                    )

                    if (isCompleted) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ukończona",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, userId: Int) {

    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = "Lekcje") },
            label = { Text("Lekcje") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Screens.RankingScreen.pass(userId)) },
            icon = { Icon(Icons.Default.Star, contentDescription = "Ranking") },
            label = { Text("Ranking") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(
                    Screens.ProfileScreen.pass(userId)
                )
            },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profil") },
            label = { Text("Profil") }
        )
    }
}