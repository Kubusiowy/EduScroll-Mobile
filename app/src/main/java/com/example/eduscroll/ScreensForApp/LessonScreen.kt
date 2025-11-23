package com.example.eduscroll.ScreensForApp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduscroll.ViewModels.LessonDetailViewModel
import com.example.eduscroll.ViewModels.LessonStep
import com.example.eduscroll.ViewModels.LessonUiState

@Composable
fun LessonScreen(
    navController: NavController,
    lessonId: Int,
    userId: Int, // na razie nieużywane, ale może się przydać np. w przyszłości do profilu
    viewModel: LessonDetailViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

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
                onAnswer = { qId, answer -> viewModel.selectAnswer(qId, answer) },
                onNext = { viewModel.nextQuestion() },
                onFinish = {
                    // 👇 TUTAJ ZMIANA – backend ma usera „na sztywno”, więc tylko lessonId
                    viewModel.finishQuizAndSave(lessonId)
                }
            )

            LessonStep.SUMMARY -> LessonSummaryScreen(
                correct = state.correctCount,
                total = state.questions.size,
                onHome = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LessonMaterialsScreen(
    state: LessonUiState,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Materiały lekcji", style = MaterialTheme.typography.titleLarge)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.materials.size) { index ->
                val item = state.materials[index]
                MaterialCard(item)
            }
        }

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
            Text("Przejdź do quizu")
        }
    }
}

@Composable
fun MaterialCard(material: com.example.eduscroll.ViewModels.MaterialWithParagraphs) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(material.material.title ?: "Materiał", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

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

    // jaka odpowiedź jest wybrana dla tego pytania
    val selectedLetter = state.selectedAnswers[question.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Pytanie ${state.currentQuestionIndex + 1}/${state.questions.size}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(16.dp))

        Text(question.question, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))

        @Composable
        fun AnswerButton(letter: String, text: String?) {
            if (text.isNullOrBlank()) return

            val isSelected = selectedLetter == letter

            Button(
                onClick = { onAnswer(question.id, letter) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                border = if (isSelected) {
                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else null
            ) {
                Text("$letter) $text")
            }
        }

        AnswerButton("A", question.answerA)
        AnswerButton("B", question.answerB)
        AnswerButton("C", question.answerC)
        AnswerButton("D", question.answerD)

        Spacer(Modifier.height(20.dp))

        val isLast = state.currentQuestionIndex == state.questions.size - 1

        Button(
            onClick = if (isLast) onFinish else onNext,
            enabled = selectedLetter != null, // bez wyboru nie idziesz dalej
            modifier = Modifier.fillMaxWidth()
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
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Podsumowanie", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Text("Poprawne odpowiedzi: $correct / $total")

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
