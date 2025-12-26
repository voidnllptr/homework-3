package com.example.topplaygroundcompose.data.repository

import com.example.topplaygroundcompose.data.api.ApiClient
import com.example.topplaygroundcompose.data.database.FavoriteDao
import com.example.topplaygroundcompose.data.model.Article
import com.example.topplaygroundcompose.data.model.FavoriteArticle
import kotlinx.coroutines.flow.Flow

class NewsRepository(
    private val favoriteDao: FavoriteDao
) {

    suspend fun getArticles(limit: Int = 50): List<Article> {
        return ApiClient.api.getArticles(limit).results
    }

    suspend fun getArticleById(id: Int): Article {
        return ApiClient.api.getArticleById(id)
    }

    suspend fun addToFavorites(articleId: Int, article: Article) {
        val favorite = FavoriteArticle(
            id = article.id,
            title = article.title,
            url = article.url,
            imageUrl = article.imageUrl,
            newsSite = article.newsSite,
            summary = article.summary,
            publishedAt = article.publishedAt ?: ""
        )
        favoriteDao.insertFavorite(favorite)
    }

    suspend fun removeFromFavorites(article: FavoriteArticle) {
        favoriteDao.deleteFavorite(article)
    }

    fun getFavorites(): Flow<List<FavoriteArticle>> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun isFavorite(articleId: Int): Boolean {
        return favoriteDao.isFavorite(articleId)
    }

    suspend fun getFavoriteById(articleId: Int): FavoriteArticle? {
        return favoriteDao.getFavoriteById(articleId)
    }
}
