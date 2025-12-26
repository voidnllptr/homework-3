package com.example.topplaygroundcompose.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.topplaygroundcompose.R
import com.example.topplaygroundcompose.data.model.Article
import com.example.topplaygroundcompose.databinding.FragmentArticleDetailBinding
import com.example.topplaygroundcompose.ui.MainViewModel
import com.example.topplaygroundcompose.data.utils.Constants.EXTRA_ARTICLE_ID
import com.example.topplaygroundcompose.ui.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()
    private var articleId: Int = 0
    private var article: Article? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articleId = arguments?.getInt(EXTRA_ARTICLE_ID) ?: 0
        if (articleId == 0) {
            parentFragmentManager.popBackStack()
            return
        }

        setupClickListeners()
        observeViewModel()
        loadArticle(articleId)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.tvSource.setOnClickListener {
            article?.url?.let { url ->
                Toast.makeText(context, getString(R.string.article_source) + ": $url", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnToggleFavorite.setOnClickListener {
            article?.let { toggleFavorite(it.id) }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavoriteMap.collect { favoriteMap ->
                article?.let { article ->
                    val isFavorite = favoriteMap[article.id] == true
                    updateFavoriteUI(isFavorite)
                }
            }
        }
    }

    private fun loadArticle(id: Int) {
        lifecycleScope.launch {
            binding.progressBar.isVisible = true
            binding.tvTitle.isVisible = false
            binding.tvNewsSite.isVisible = false
            binding.tvDate.isVisible = false
            binding.tvSummary.isVisible = false
            binding.tvSource.isVisible = false
            binding.tvSourceLabel.isVisible = false
            binding.btnToggleFavorite.isVisible = false
            binding.btnBack.isVisible = false

            try {
                try {
                    article = viewModel.getArticleById(id)
                } catch (apiException: Exception) {
                    val articlesState = viewModel.newsState.value
                    article = if (articlesState is UiState.Success) {
                        articlesState.data.find { it.id == id }
                    } else null
                }

                article?.let {
                    bindArticleData(it)
                } ?: run {
                    Toast.makeText(context, getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
                    delay(1000)
                    parentFragmentManager.popBackStack()
                }
            } catch (e: Exception) {
                Toast.makeText(context, getString(R.string.error_loading), Toast.LENGTH_SHORT).show()
                delay(1000)
                parentFragmentManager.popBackStack()
            } finally {
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun bindArticleData(article: Article) {
        binding.apply {
            tvTitle.text = article.title
            tvTitle.isVisible = true

            tvNewsSite.text = getString(R.string.news_site, article.newsSite ?: getString(R.string.error_empty))
            tvNewsSite.isVisible = true

            tvDate.text = getString(R.string.news_date, article.formattedDate ?: getString(R.string.error_empty))
            tvDate.isVisible = true

            tvSummaryLabel.isVisible = true
            tvSummary.text = article.summary.ifEmpty { getString(R.string.error_empty) }
            tvSummary.isVisible = true

            tvSourceLabel.isVisible = true
            tvSource.text = article.url?.substringAfterLast("/") ?: getString(R.string.error_network)
            tvSource.isVisible = !article.url.isNullOrEmpty()

            btnToggleFavorite.isVisible = true
            btnBack.isVisible = true

            val isFavorite = viewModel.isFavoriteMap.value[article.id] == true
            updateFavoriteUI(isFavorite)
        }

        Glide.with(this@ArticleDetailFragment)
            .load(article.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivArticleImage)
    }

    private fun toggleFavorite(articleId: Int) {
        lifecycleScope.launch {
            val wasFavorite = viewModel.isFavoriteMap.value[articleId] == true
            viewModel.toggleFavoriteById(articleId)

            delay(500)
            Toast.makeText(
                requireContext(),
                if (!wasFavorite) getString(R.string.btn_add_favorite) else getString(R.string.btn_remove_favorite),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateFavoriteUI(isFavorite: Boolean) {
        binding.btnToggleFavorite.apply {
            text = if (isFavorite) {
                getString(R.string.btn_remove_favorite)
            } else {
                getString(R.string.btn_add_favorite)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
