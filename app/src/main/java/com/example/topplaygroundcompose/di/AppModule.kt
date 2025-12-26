package com.example.topplaygroundcompose.di

import com.example.topplaygroundcompose.data.database.AppDatabase
import com.example.topplaygroundcompose.data.database.FavoriteDao
import com.example.topplaygroundcompose.data.repository.NewsRepository
import com.example.topplaygroundcompose.ui.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { AppDatabase.getDatabase(androidContext()) }
    single<FavoriteDao> { get<AppDatabase>().favoriteDao() }

    single { NewsRepository(favoriteDao = get()) }

    viewModel { MainViewModel(repository = get()) }
}
