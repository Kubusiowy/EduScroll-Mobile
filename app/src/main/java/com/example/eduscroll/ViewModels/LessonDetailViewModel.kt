package com.example.eduscroll.ViewModels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduscroll.client.RetrofitInstance
import com.example.eduscroll.domain.EducationMaterialDto
import com.example.eduscroll.domain.ParagraphDto
import com.example.eduscroll.domain.PassedLessonRequest
import com.example.eduscroll.domain.QuestionDto
import kotlinx.coroutines.launch

enum class LessonStep {
    MATERIALS,
    QUIZ,
    SUMMARY
}

data class LessonUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val materials: List<MaterialWithParagraphs> = emptyList(),
    val questions: List<QuestionDto> = emptyList(),
    val currentStep: LessonStep = LessonStep.MATERIALS,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, String> = emptyMap(),
    val correctCount: Int = 0,
    val progressSaved: Boolean = false
)

// materiał + paragrafy
data class MaterialWithParagraphs(
    val material: EducationMaterialDto,
    val paragraphs: List<ParagraphDto>
)

class LessonDetailViewModel : ViewModel() {

    var uiState by mutableStateOf(LessonUiState())
        private set

    fun loadLesson(lessonId: Int) {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val materials = RetrofitInstance.api.getMaterialsForLesson(lessonId)

                val materialsWithParagraphs = materials.map { material ->
                    val paragraphs = RetrofitInstance.api
                        .getParagraphsForMaterial(material.id)
                        .sortedBy { it.paragraphNumber }

                    MaterialWithParagraphs(
                        material = material,
                        paragraphs = paragraphs
                    )
                }

                val questions = RetrofitInstance.api
                    .getQuestionsForLesson(lessonId)

                uiState = uiState.copy(
                    isLoading = false,
                    materials = materialsWithParagraphs,
                    questions = questions,
                    currentStep = LessonStep.MATERIALS
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Nie udało się wczytać lekcji"
                )
            }
        }
    }

    fun goToQuiz() {
        uiState = uiState.copy(currentStep = LessonStep.QUIZ)
    }

    fun selectAnswer(questionId: Int, answerLetter: String) {
        val updated = uiState.selectedAnswers.toMutableMap()
        updated[questionId] = answerLetter
        uiState = uiState.copy(selectedAnswers = updated)
    }

    fun nextQuestion() {
        val next = uiState.currentQuestionIndex + 1
        if (next < uiState.questions.size) {
            uiState = uiState.copy(currentQuestionIndex = next)
        }
    }

    fun finishQuizAndSave(lessonId: Int) {
        viewModelScope.launch {
            val correct = uiState.questions.count { q ->
                val selected = uiState.selectedAnswers[q.id]
                selected != null && selected.equals(q.correctAnswer, ignoreCase = true)
            }

            uiState = uiState.copy(
                correctCount = correct,
                currentStep = LessonStep.SUMMARY
            )

            try {
                val body = PassedLessonRequest(
                    lessonId = lessonId,
                    correctAnswers = correct
                )

                val response = RetrofitInstance.api
                    .postLessonProgress(lessonId, body)

                if (response.isSuccessful) {
                    uiState = uiState.copy(progressSaved = true)
                } else {
                    uiState = uiState.copy(
                        error = "Nie udało się zapisać progresu (${response.code()})"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    error = "Błąd sieci przy zapisie progresu: ${e.message}"
                )
            }
        }
    }
}
