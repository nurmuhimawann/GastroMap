package com.nurmuhimawann.gastromap.di

import android.content.Context
import com.nurmuhimawann.gastromap.data.GithubRepository
import com.nurmuhimawann.gastromap.data.RestaurantRepository
import com.nurmuhimawann.gastromap.data.local.dataStore.SettingPreferences
import com.nurmuhimawann.gastromap.data.local.room.FavoriteRestaurantDatabase
import com.nurmuhimawann.gastromap.data.remote.retrofit.ApiConfig
import com.nurmuhimawann.gastromap.ui.ViewModelFactory

object Injection {
    fun provideViewModelFactory(context: Context): ViewModelFactory {
        return ViewModelFactory(
            { provideRestaurantRepository(context) },
            { provideGithubRepository() },
            provideSettingPreferences(context)
        )
    }

    fun provideRestaurantRepository(
        context: Context
    ): RestaurantRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteRestaurantDatabase.getInstance(context)
        val dao = database.favoriteRestaurantDao()

        return RestaurantRepository.getInstance(apiService, dao)
    }

    fun provideGithubRepository(
    ): GithubRepository {
        val apiGithubService = ApiConfig.getApiGithubService()

        return GithubRepository(apiGithubService)
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.getInstance(context)
    }
}


