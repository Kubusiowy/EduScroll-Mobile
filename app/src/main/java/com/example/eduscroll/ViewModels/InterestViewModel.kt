package com.example.eduscroll.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduscroll.client.RetrofitInstance
import com.example.eduscroll.domain.CategoryDto
import kotlinx.coroutines.launch

data class InterestUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryDto> = emptyList(),
    val errorMessage: String? = null
)

class InterestViewModel : ViewModel() {

    var uiState: InterestUiState by mutableStateOf(InterestUiState())
        private set

    init {
        loadCategories()
    }

    fun loadCategories() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = RetrofitInstance.api.getCategories()
                uiState = uiState.copy(
                    isLoading = false,
                    categories = result,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Wystąpił błąd podczas pobierania kategorii"
                )
            }
        }
    }
}
