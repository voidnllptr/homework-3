package com.example.topplaygroundcompose.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "favorite_articles")
data class FavoriteArticle(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    @ColumnInfo(name = "news_site")
    val newsSite: String?,

    @ColumnInfo(name = "summary")
    val summary: String,

    @ColumnInfo(name = "published_at")
    val publishedAt: String,

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis()
)
