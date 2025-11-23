package com.example.eduscroll.client



import com.example.eduscroll.domain.CategoryDto
import com.example.eduscroll.domain.LessonDto
import retrofit2.Response
import retrofit2.http.GET
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


}
