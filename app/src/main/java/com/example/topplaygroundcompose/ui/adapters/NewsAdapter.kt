package com.example.topplaygroundcompose.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.topplaygroundcompose.R
import com.example.topplaygroundcompose.data.model.Article
import com.example.topplaygroundcompose.data.model.FavoriteArticle
import com.example.topplaygroundcompose.databinding.ItemNewsBinding

class NewsAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onFavoriteClick: (Int) -> Unit,
    private val isFavoriteProvider: (Int) -> Boolean
) : ListAdapter<Any, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Article -> VIEW_TYPE_ARTICLE
            is FavoriteArticle -> VIEW_TYPE_FAVORITE
            else -> throw IllegalArgumentException("$position")
        }
    }

    inner class NewsViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Any) {
            when (item) {
                is Article -> bindArticle(item)
                is FavoriteArticle -> bindFavoriteArticle(item)
            }
        }

        private fun bindArticle(article: Article) = with(binding) {
            tvTitle.text = article.title
            tvNewsSite.text = root.context.getString(R.string.news_site, article.newsSite)
            tvDate.text = article.formattedDate
            tvSummary.text = article.summary

            Glide.with(root.context)
                .load(article.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivArticleImage)

            ivFavorite.isVisible = true
            val isFav = isFavoriteProvider(article.id)
            ivFavorite.setImageResource(
                if (isFav) R.drawable.ic_star_filled
                else R.drawable.ic_star_outline
            )

            root.setOnClickListener { onItemClick(article.id) }
            ivFavorite.setOnClickListener { onFavoriteClick(article.id) }
        }

        private fun bindFavoriteArticle(favorite: FavoriteArticle) = with(binding) {
            tvTitle.text = favorite.title
            tvNewsSite.text = root.context.getString(R.string.news_site, favorite.newsSite)
            tvDate.text = favorite.publishedAt.take(10)
            tvSummary.text = favorite.summary

            Glide.with(root.context)
                .load(favorite.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivArticleImage)

            ivFavorite.isVisible = true
            ivFavorite.setImageResource(R.drawable.ic_star_filled)

            root.setOnClickListener { onItemClick(favorite.id) }
            ivFavorite.setOnClickListener { onFavoriteClick(favorite.id) }
        }
    }

    private class NewsDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Article && newItem is Article ->
                    oldItem.id == newItem.id
                oldItem is FavoriteArticle && newItem is FavoriteArticle ->
                    oldItem.id == newItem.id
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_ARTICLE = 0
        private const val VIEW_TYPE_FAVORITE = 1
    }
}
