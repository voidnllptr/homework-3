package com.example.topplaygroundcompose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.topplaygroundcompose.data.model.Article
import com.example.topplaygroundcompose.data.model.FavoriteArticle
import com.example.topplaygroundcompose.data.repository.NewsRepository
import com.example.topplaygroundcompose.data.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<Article>) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(
    val repository: NewsRepository
) : ViewModel() {

    private val _newsState = MutableStateFlow<UiState>(UiState.Loading)
    val newsState: StateFlow<UiState> = _newsState.asStateFlow()

    private val _favoritesState = MutableStateFlow<List<FavoriteArticle>>(emptyList())
    val favoritesState: StateFlow<List<FavoriteArticle>> = _favoritesState.asStateFlow()

    private val _isFavoriteMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val isFavoriteMap: StateFlow<Map<Int, Boolean>> = _isFavoriteMap.asStateFlow()

    init {
        loadNews()
        loadFavorites()
    }

    fun loadNews() {
        viewModelScope.launch {
            _newsState.value = UiState.Loading
            try {
                val articles = repository.getArticles(Constants.ARTICLES_LIMIT)
                _newsState.value = UiState.Success(articles)
                updateArticlesFavoriteStatus(articles)
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is SocketTimeoutException -> "error_network"
                    is UnknownHostException -> "error_network"
                    else -> "error_loading"
                }
                _newsState.value = UiState.Error(errorMessage)
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { favorites ->
                _favoritesState.value = favorites
                updateFavoritesFavoriteStatus(favorites)
            }
        }
    }

    suspend fun getArticleById(id: Int): Article {
        return repository.getArticleById(id)
    }

    fun toggleFavoriteById(articleId: Int) {
        viewModelScope.launch {
            val isFavNow = repository.isFavorite(articleId)

            if (isFavNow) {
                val favorite = repository.getFavoriteById(articleId)
                favorite?.let {
                    repository.removeFromFavorites(it)
                }
            } else {
                val state = newsState.value
                if (state is UiState.Success) {
                    val article = state.data.firstOrNull { it.id == articleId }
                    if (article != null) {
                        repository.addToFavorites(articleId, article)
                    }
                }
            }

            val newState = newsState.value
            if (newState is UiState.Success) {
                updateArticlesFavoriteStatus(newState.data)
            }
        }
    }

    private fun updateArticlesFavoriteStatus(articles: List<Article>) {
        viewModelScope.launch {
            val map = articles.associate { it.id to repository.isFavorite(it.id) }
            _isFavoriteMap.value = map
        }
    }

    private fun updateFavoritesFavoriteStatus(favorites: List<FavoriteArticle>) {
        val map = favorites.associate { it.id to true }
        _isFavoriteMap.value = map
    }
}
