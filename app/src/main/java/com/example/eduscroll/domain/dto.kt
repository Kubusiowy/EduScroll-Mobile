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


@Serializable
data class EducationMaterialDto(
    val id: Int,
    val title: String?,
    val lessonId: Int
)

@Serializable
data class ParagraphDto(
    val id: Int,
    val paragraphNumber: Int,
    val header: String,
    val content: String,
    val materialId: Int
)


@Serializable
data class QuestionDto(
    val id: Int,
    val question: String,
    val answerA: String,
    val answerB: String,
    val answerC: String?,
    val answerD: String?,
    val correctAnswer: String,
    val expGain: Int,
    val lessonId: Int
)


@Serializable
data class PassedLessonRequest(
    val userId: Int,
    val lessonId: Int,
    val correctAnswers: Int
)


@Serializable
data class ProgressResponseDto(
    val message: String
)