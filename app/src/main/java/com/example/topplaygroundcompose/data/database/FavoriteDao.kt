package com.example.topplaygroundcompose.data.database

import androidx.room.*
import com.example.topplaygroundcompose.data.model.FavoriteArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(article: FavoriteArticle)

    @Delete
    suspend fun deleteFavorite(article: FavoriteArticle)

    @Query("SELECT * FROM favorite_articles ORDER BY added_date DESC")
    fun getAllFavorites(): Flow<List<FavoriteArticle>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_articles WHERE id = :articleId)")
    suspend fun isFavorite(articleId: Int): Boolean

    @Query("SELECT * FROM favorite_articles WHERE id = :articleId")
    suspend fun getFavoriteById(articleId: Int): FavoriteArticle?

    @Query("SELECT COUNT(*) FROM favorite_articles")
    suspend fun getFavoritesCount(): Int
}
