package com.example.topplaygroundcompose.data.utils

object Constants {
    const val BASE_URL = "https://api.spaceflightnewsapi.net/"
    const val ARTICLES_LIMIT = 50

    const val DATABASE_NAME = "spaceflight_db"

    const val PREF_NAME = "spaceflight_prefs"
    const val KEY_IS_FIRST_LAUNCH = "is_first_launch"

    const val NEWS_SPAN_COUNT = 1
    const val FAVORITES_SPAN_COUNT = 1

    const val DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val DATE_FORMAT_UI = "dd.MM.yyyy"

    const val TIMEOUT_SECONDS = 30L
    const val CONNECTION_TIMEOUT = 10_000L
    const val READ_TIMEOUT = 30_000L

    const val EXTRA_ARTICLE_ID = "article_id"
    const val EXTRA_ARTICLE_TITLE = "article_title"

    const val DATE_UNKNOWN = "Дата неизвестна"
}
