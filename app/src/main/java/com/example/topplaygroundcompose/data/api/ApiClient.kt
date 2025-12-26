package com.example.topplaygroundcompose.data.api

import com.example.topplaygroundcompose.data.model.Article
import com.example.topplaygroundcompose.data.model.ArticlesResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceflightApiService {
    @GET("articles")
    suspend fun getArticles(
        @Query("_limit") limit: Int = 50
    ): ArticlesResponse

    @GET("articles/{id}")
    suspend fun getArticleById(@Path("id") id: Int): Article
}

object ApiClient {
    private const val BASE_URL = "https://api.spaceflightnewsapi.net/v4/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: SpaceflightApiService = retrofit.create(SpaceflightApiService::class.java)
}
