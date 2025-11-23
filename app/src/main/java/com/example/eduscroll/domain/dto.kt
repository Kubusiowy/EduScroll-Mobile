package com.example.eduscroll.domain

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int,
    val name: String,
    val description: String? = null

)

@Serializable
data class LessonDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val categoryId: Int
)