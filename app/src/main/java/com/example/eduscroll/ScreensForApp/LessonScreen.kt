package com.example.eduscroll.ScreensForApp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduscroll.ViewModels.*

@Composable
fun LessonScreen(
    navController: NavController,
    lessonId: Int,
    userId: Int,
    viewModel: LessonDetailViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(lessonId) { viewModel.loadLesson(lessonId) }

    val animAlpha by animateFloatAsState(
        if (!state.isLoading) 1f else 0f,
        tween(600, easing = FastOutSlowInEasing)
    )

    Box(
        Modifier
            .fillMaxSize()
            .alpha(animAlpha)
    ) {
        when {
            state.isLoading -> LoadingView()
            state.error != null -> ErrorView(state.error)

            else -> when (state.currentStep) {
                LessonStep.MATERIALS -> LessonMaterialsScreen(
                    state = state,
                    onNext = { viewModel.goToQuiz() }
                )

                LessonStep.QUIZ -> LessonQuizScreen(
                    state = state,
                    onAnswer = { q, ans -> viewModel.selectAnswer(q, ans) },
                    onNext = { viewModel.nextQuestion() },
                    onFinish = { viewModel.finishQuizAndSave(lessonId) }
                )

                LessonStep.SUMMARY -> LessonSummaryScreen(
                    correct = state.correctCount,
                    total = state.questions.size,
                    onHome = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun LessonMaterialsScreen(
    state: LessonUiState,
    onNext: () -> Unit
) {
    val fade by animateFloatAsState(1f, tween(600))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .alpha(fade)
    ) {
        Text("Materiały lekcji", style = MaterialTheme.typography.titleLarge)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.materials.size) { index ->
                val scale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(300, delayMillis = index * 80)
                )

                MaterialCard(
                    modifier = Modifier.scale(scale),
                    material = state.materials[index]
                )
            }
        }

        val pulse by rememberInfiniteTransition().animateFloat(
            initialValue = 1f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                tween(900, easing = LinearEasing),
                RepeatMode.Reverse
            )
        )

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .scale(pulse)
        ) {
            Text("Przejdź do quizu")
        }
    }
}

@Composable
fun MaterialCard(
    modifier: Modifier = Modifier,
    material: MaterialWithParagraphs
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(material.material.title ?: "Materiał", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            material.paragraphs.forEach { p ->
                Text("• ${p.header}", style = MaterialTheme.typography.labelLarge)
                Text(p.content, modifier = Modifier.padding(bottom = 12.dp))
            }
        }
    }
}

@Composable
fun LessonQuizScreen(
    state: LessonUiState,
    onAnswer: (Int, String) -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    val question = state.questions[state.currentQuestionIndex]
    val selected = state.selectedAnswers[question.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Pytanie ${state.currentQuestionIndex + 1}/${state.questions.size}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(20.dp))

        Text(question.question, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(20.dp))

        @Composable
        fun AnimatedAnswer(letter: String, text: String?) {
            if (text == null) return

            val isSelected = selected == letter
            val scale by animateFloatAsState(if (isSelected) 1.03f else 1f, tween(150))

            Button(
                onClick = { onAnswer(question.id, letter) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .scale(scale),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (isSelected)
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                else null
            ) {
                Text("$letter) $text")
            }
        }

        AnimatedAnswer("A", question.answerA)
        AnimatedAnswer("B", question.answerB)
        AnimatedAnswer("C", question.answerC)
        AnimatedAnswer("D", question.answerD)

        Spacer(Modifier.height(20.dp))

        val isLast = state.currentQuestionIndex == state.questions.size - 1

        val pulse by rememberInfiniteTransition().animateFloat(
            1f, 1.05f, infiniteRepeatable(tween(900), RepeatMode.Reverse)
        )

        Button(
            onClick = if (isLast) onFinish else onNext,
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .scale(pulse)
        ) {
            Text(if (isLast) "Zakończ quiz" else "Następne pytanie")
        }
    }
}

@Composable
fun LessonSummaryScreen(
    correct: Int,
    total: Int,
    onHome: () -> Unit
) {
    val fade by animateFloatAsState(1f, tween(700))
    val scale by animateFloatAsState(1f, tween(700))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .alpha(fade)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Podsumowanie", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Text("$correct / $total poprawnych")
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Wróć do lekcji")
        }
    }
}

@Composable
fun LoadingView() = Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator()
}

@Composable
fun ErrorView(msg: String) = Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
) {
    Text("Błąd: $msg")
}