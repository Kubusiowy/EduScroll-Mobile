package com.example.eduscroll.client



import com.example.eduscroll.domain.CategoryDto
import com.example.eduscroll.domain.EducationMaterialDto
import com.example.eduscroll.domain.LessonDto
import com.example.eduscroll.domain.ParagraphDto
import com.example.eduscroll.domain.PassedLessonRequest
import com.example.eduscroll.domain.QuestionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path



interface ApiService{


    @GET("ping")
    suspend fun pingServer(): Response<Map<String, Boolean>>
    @GET("api/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("api/categories/{id}/lessons")
    suspend fun getLessonsForCategory(
        @Path("id") categoryId: Int
    ): List<LessonDto>

    @GET("api/lessons/{id}/materials")
    suspend fun getMaterialsForLesson(
        @Path("id") lessonId: Int
    ): List<EducationMaterialDto>

    @GET("api/materials/{id}/paragraphs")
    suspend fun getParagraphsForMaterial(
        @Path("id") materialId: Int
    ): List<ParagraphDto>

    @GET("api/lessons/{id}/questions")
    suspend fun getQuestionsForLesson(
        @Path("id") lessonId: Int
    ): List<QuestionDto>

    @POST("api/progress/lesson/{id}")
    suspend fun postLessonProgress(
        @Path("id") lessonId: Int,
        @Body body: PassedLessonRequest
    ): Response<Map<String, String>>


}
