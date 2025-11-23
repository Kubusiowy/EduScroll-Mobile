package com.example.eduscroll.ViewModels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduscroll.client.RetrofitInstance
import com.example.eduscroll.domain.LessonDto
import com.example.eduscroll.domain.PassedLessonDto // DTO z api/progress
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var lessons by mutableStateOf<List<LessonDto>>(emptyList())
        private set

    var completedLessons by mutableStateOf<Set<Int>>(emptySet())
        private set

    fun loadLessons(categoryId: Int) {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null

                val lessonsFromApi = RetrofitInstance.api.getLessonsForCategory(categoryId)
                val progress = RetrofitInstance.api.getProgress()
                val completedIds = progress
                    .map { it.lessonId }
                    .toSet()

                lessons = lessonsFromApi
                completedLessons = completedIds

            } catch (e: Exception) {
                error = e.message ?: "Nie udało się wczytać lekcji"
            } finally {
                isLoading = false
            }
        }
    }
}
