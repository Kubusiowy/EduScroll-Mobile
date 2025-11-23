package com.example.eduscroll.ViewModels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduscroll.client.RetrofitInstance
import com.example.eduscroll.domain.PassedLessonDto
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val username: String = "Gość #1",
    val totalLessons: Int = 0,
    val totalCorrect: Int = 0,
    val exp: Int = 0,
    val level: Int = 1
)

class ProfileViewModel : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile() {
        uiState = uiState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // backend i tak zwraca progres demo-usera
                val progress: List<PassedLessonDto> = RetrofitInstance.api.getProgress()

                val totalLessons = progress.size
                val totalCorrect = progress.sumOf { it.correctAnswers }
                val exp = totalCorrect * 10
                val level = 1 + (exp / 50)

                uiState = uiState.copy(
                    isLoading = false,
                    totalLessons = totalLessons,
                    totalCorrect = totalCorrect,
                    exp = exp,
                    level = level
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Nie udało się wczytać profilu"
                )
            }
        }
    }
}