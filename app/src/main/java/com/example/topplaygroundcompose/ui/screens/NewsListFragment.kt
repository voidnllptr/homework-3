package com.example.topplaygroundcompose.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.topplaygroundcompose.R
import com.example.topplaygroundcompose.databinding.FragmentNewsListBinding
import com.example.topplaygroundcompose.ui.MainViewModel
import com.example.topplaygroundcompose.ui.UiState
import com.example.topplaygroundcompose.ui.adapters.NewsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class NewsListFragment : Fragment() {

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadNews()
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(
            onItemClick = { articleId ->
                navigateToDetail(articleId)
            },
            onFavoriteClick = { articleId ->
                viewModel.toggleFavoriteById(articleId)
            },
            isFavoriteProvider = { id ->
                viewModel.isFavoriteMap.value[id] == true
            }
        )

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@NewsListFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newsState.collect { state ->

                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.isVisible = true
                            binding.tvLoading.isVisible = true
                            binding.rvNews.isVisible = false
                            binding.tvEmptyState.isVisible = false
                            binding.btnRetry.isVisible = false
                        }
                        is UiState.Success -> {
                            binding.progressBar.isVisible = false
                            binding.tvLoading.isVisible = false
                            binding.rvNews.isVisible = true
                            binding.tvEmptyState.isVisible = false
                            binding.btnRetry.isVisible = false
                            adapter.submitList(state.data)
                        }
                        is UiState.Error -> {
                            binding.progressBar.isVisible = false
                            binding.tvLoading.isVisible = false
                            binding.rvNews.isVisible = false
                            binding.tvEmptyState.isVisible = true
                            binding.btnRetry.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun navigateToDetail(articleId: Int) {
        val bundle = Bundle().apply {
            putInt(com.example.topplaygroundcompose.data.utils.Constants.EXTRA_ARTICLE_ID, articleId)
        }

        val detailFragment = ArticleDetailFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack("ArticleDetail")
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
