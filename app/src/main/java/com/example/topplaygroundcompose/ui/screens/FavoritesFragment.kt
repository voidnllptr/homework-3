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
import com.example.topplaygroundcompose.databinding.FragmentFavoritesBinding
import com.example.topplaygroundcompose.ui.MainViewModel
import com.example.topplaygroundcompose.ui.adapters.NewsAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = NewsAdapter(
            onItemClick = { articleId ->
                navigateToDetail(articleId)
            },
            onFavoriteClick = { articleId ->
                toggleFavorite(articleId)
            },
            isFavoriteProvider = { id ->
                viewModel.isFavoriteMap.value[id] == true
            }
        )

        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@FavoritesFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesState.collect { favorites ->
                    binding.progressBar.isVisible = false
                    if (favorites.isEmpty()) {
                        binding.tvEmptyState.isVisible = true
                        binding.rvFavorites.isVisible = false
                    } else {
                        binding.tvEmptyState.isVisible = false
                        binding.rvFavorites.isVisible = true
                        adapter.submitList(favorites)
                    }
                }
            }
        }
    }

    private fun toggleFavorite(articleId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toggleFavoriteById(articleId)
        }
    }

    private fun navigateToDetail(articleId: Int) {
        val bundle = Bundle().apply {
            putInt(
                com.example.topplaygroundcompose.data.utils.Constants.EXTRA_ARTICLE_ID,
                articleId
            )
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ArticleDetailFragment())
            .addToBackStack("ArticleDetail")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
