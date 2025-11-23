package com.example.eduscroll.ScreensForApp

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduscroll.ViewModels.RankingEntry
import com.example.eduscroll.ViewModels.RankingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    navController: NavController,
    userId: Int,
    rankingViewModel: RankingViewModel = viewModel()
) {
    val state = rankingViewModel.uiState

    LaunchedEffect(userId) {
        rankingViewModel.loadRanking(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ranking globalny",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
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
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(
                            text = "To jest prototypowy ranking – EXP jest prawdziwy dla Twojego konta, reszta graczy to przykładowi przeciwnicy 💫",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )

                        CurrentUserSummaryRow(
                            userId = userId,
                            entries = state.entries
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.entries) { entry ->
                                RankingRow(entry = entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentUserSummaryRow(
    userId: Int,
    entries: List<RankingEntry>
) {
    val current = entries.find { it.isCurrentUser } ?: return


    val infinite = rememberInfiniteTransition(label = "user_card_pulse")
    val scale by infinite.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "user_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Twoja pozycja",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "#${current.position} – ${current.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${current.exp} EXP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "EXP liczony z Twoich quizów",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingRow(entry: RankingEntry) {
    val isTop3 = entry.position <= 3


    val infinite = rememberInfiniteTransition(label = "rank_pulse")
    val glowAlpha by infinite.animateFloat(
        initialValue = if (isTop3) 0.3f else 0f,
        targetValue = if (isTop3) 1f else 0f,
        animationSpec = infiniteRepeatable(
            tween(900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )


    val scale by animateFloatAsState(
        targetValue = if (entry.isCurrentUser) 1.02f else 1f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "row_scale"
    )

    val bgColor =
        if (entry.isCurrentUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(1f),
        colors = CardDefaults.cardColors(
            containerColor = bgColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTop3 || entry.isCurrentUser) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isTop3) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Top gracz",
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = glowAlpha),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "#${entry.position}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.name,
                    fontSize = 16.sp,
                    fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = if (entry.isCurrentUser)
                        "To Ty 🟢 – EXP z prawdziwego progresu"
                    else
                        "Gracz EduScroll",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "${entry.exp} EXP",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}