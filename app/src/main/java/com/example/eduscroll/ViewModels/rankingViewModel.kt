package com.example.eduscroll.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduscroll.client.RetrofitInstance
import kotlinx.coroutines.launch

data class RankingEntry(
    val position: Int,
    val name: String,
    val exp: Int,
    val isCurrentUser: Boolean = false
)

data class RankingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val entries: List<RankingEntry> = emptyList()
)

class RankingViewModel : ViewModel() {

    var uiState by mutableStateOf(RankingUiState())
        private set

    fun loadRanking(userId: Int) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                // 1. pobieramy realny progres demo usera z backendu
                val progress = RetrofitInstance.api.getProgress()
                // zakładam że masz: @GET("api/progress") suspend fun getProgress(): List<PassedLessonDto>

                val totalCorrect = progress.sumOf { it.correctAnswers }
                val userExp = totalCorrect * 10 // tak jak w profilu

                // 2. przykładowi gracze (EXP na sztywno)
                val basePlayers = listOf(
                    RankingEntry(0, "CyberNinja", 420),
                    RankingEntry(0, "AnnaSecure", 350),
                    RankingEntry(0, "PhishBuster", 310),
                    RankingEntry(0, "SafeKid_21", 250),
                    RankingEntry(0, "HashMaster", 220),
                    RankingEntry(0, "NetGuardian", 200),
                )

                // 3. nasz gracz – z prawdziwym EXP z serwera
                val currentUser = RankingEntry(
                    position = 0,
                    name = "Gość #$userId",
                    exp = userExp,
                    isCurrentUser = true
                )

                // 4. łączymy, sortujemy po EXP i nadajemy pozycje
                val sorted = (basePlayers + currentUser)
                    .sortedByDescending { it.exp }
                    .mapIndexed { index, entry ->
                        entry.copy(position = index + 1)
                    }

                uiState = uiState.copy(
                    isLoading = false,
                    entries = sorted
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = e.message ?: "Nie udało się załadować rankingu"
                )
            }
        }
    }
}