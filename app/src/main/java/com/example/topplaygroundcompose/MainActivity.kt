package com.example.topplaygroundcompose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.topplaygroundcompose.databinding.ActivityMainBinding
import com.example.topplaygroundcompose.ui.screens.FavoritesFragment
import com.example.topplaygroundcompose.ui.screens.NewsListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_news
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_news -> NewsListFragment()
                R.id.nav_favorites -> FavoritesFragment()
                else -> NewsListFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            true
        }
    }
}
