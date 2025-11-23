package com.example.eduscroll.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduscroll.client.RetrofitInstance
import com.example.eduscroll.domain.LessonDto
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var lessons by mutableStateOf<List<LessonDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadLessons(categoryId: Int) {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null

                lessons = RetrofitInstance.api.getLessonsForCategory(categoryId)

            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
