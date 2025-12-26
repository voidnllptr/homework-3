package com.example.topplaygroundcompose.data.model

import com.example.topplaygroundcompose.data.utils.Constants
import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("newsSite") val newsSite: String?,
    @SerializedName("summary") val summary: String,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,

    val isFavorite: Boolean = false
) {
    val formattedDate: String
        get() = publishedAt?.take(10) ?: Constants.DATE_UNKNOWN
}
