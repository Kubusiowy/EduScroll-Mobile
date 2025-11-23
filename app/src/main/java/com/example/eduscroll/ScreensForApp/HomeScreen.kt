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
            BottomNavigationBar(navController)
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
                        color = Color.White
                    )
                }

                homeViewModel.lessons.isEmpty() -> {
                    Text(
                        text = "Brak lekcji dla tej kategorii 😶",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // DEBUG – zobaczysz ile lekcji przyszło
                        Text(
                            text = "Lekcje: ${homeViewModel.lessons.size}",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        )

                        LessonList(
                            lessons = homeViewModel.lessons,
                            navController = navController
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
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        items(lessons.size) { index ->
            val lesson = lessons[index]
            val isLeft = index % 2 == 0
            val primary = MaterialTheme.colorScheme.primary

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


                    drawLine(
                        color = primary.copy(alpha = 0.25f),
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, size.height),
                        strokeWidth = 18f,
                        cap = StrokeCap.Round
                    )


                    val bubbleCenterX = if (isLeft) {
                        size.width * 0.2f
                    } else {
                        size.width * 0.8f
                    }

                    drawLine(
                        color = primary.copy(alpha = 0.8f),
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
                        onClick = {
                            // TODO: przejście do szczegółów lekcji,

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
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

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
                    color = primary.copy(alpha = 0.25f),
                    shape = CircleShape
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primary,
                                secondary
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.7f),
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
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = {  },
            icon = { Icon(Icons.Default.Home, contentDescription = "Lekcje") },
            label = { Text("Lekcje") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { /* TODO ranking */ },
            icon = { Icon(Icons.Default.Star, contentDescription = "Ranking") },
            label = { Text("Ranking") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { /* TODO profil */ },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profil") },
            label = { Text("Profil") }
        )
    }
}
