package com.example.eduscroll.ScreensForApp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduscroll.ScreensSealed.Screens
import com.example.eduscroll.StorageOperations.UserPrefs
import com.example.eduscroll.ViewModels.InterestViewModel
import com.example.eduscroll.domain.CategoryDto

@Composable
fun InterestFormScreen(
    navController: NavController,
    userId: Int,
    interestViewModel: InterestViewModel = viewModel()
) {
    val state = interestViewModel.uiState
    val context = LocalContext.current

    var selectedId by remember { mutableStateOf<Int?>(null) }

    var level by remember { mutableStateOf(2f) }

    val canContinue = selectedId != null && !state.isLoading && state.errorMessage == null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Nie udało się pobrać kategorii 😐",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.errorMessage ?: "",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { interestViewModel.loadCategories() }) {
                        Text("Spróbuj ponownie")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Dopasuj EduScroll do siebie",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Wybierz kategorię, którą chcesz zacząć.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )


                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.categories.forEach { category ->
                                InterestCategoryRow(
                                    category = category,
                                    selected = category.id == selectedId,
                                    onToggle = {
                                        selectedId =
                                            if (selectedId == category.id) null
                                            else category.id
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Mój poziom znajomości tematu:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Slider(
                                value = level,
                                onValueChange = { level = it },
                                valueRange = 1f..5f,
                                steps = 3,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = when (level.toInt()) {
                                    1 -> "Nowicjusz"
                                    2 -> "Początkujący"
                                    3 -> "Średnio zaawansowany"
                                    4 -> "Zaawansowany"
                                    else -> "Ekspert"
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!canContinue) {
                            Text(
                                text = "Wybierz jedną kategorię, aby przejść dalej.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Button(
                            onClick = {
                                val categoryId = selectedId ?: return@Button


                                UserPrefs.setPreferredCategory(context, categoryId)

                                navController.navigate(
                                    Screens.HomeScreen.pass(
                                        userId = userId,
                                        categoryId = categoryId
                                    )
                                ) {
                                    popUpTo(Screens.InterestFormScreen.route) { inclusive = true }
                                }
                            },
                            enabled = canContinue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Zapisz i przejdź do lekcji",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InterestCategoryRow(
    category: CategoryDto,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (selected) 4.dp else 0.dp,
        color = if (selected)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else
            MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = selected,
                    onClick = onToggle,
                    label = {
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.SemiBold
                        )
                    })
            }
            if (!category.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
