package com.nurmuhimawann.gastromap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nurmuhimawann.gastromap.data.GithubRepository
import com.nurmuhimawann.gastromap.data.RestaurantRepository
import com.nurmuhimawann.gastromap.data.local.dataStore.SettingPreferences
import com.nurmuhimawann.gastromap.ui.screen.about.AboutScreenViewModel
import com.nurmuhimawann.gastromap.ui.screen.detail.DetailScreenViewModel
import com.nurmuhimawann.gastromap.ui.screen.favorite.FavoriteScreenViewModel
import com.nurmuhimawann.gastromap.ui.screen.home.HomeScreenViewModel

class ViewModelFactory(
    private val repositoryProducer: () -> RestaurantRepository,
    private val githubRepositoryProducer: () -> GithubRepository,
    private val preferences: SettingPreferences
) : ViewModelProvider.NewInstanceFactory() {

    private val repository by lazy { repositoryProducer() }
    private val githubRepository by lazy { githubRepositoryProducer() }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GastroMapViewModel::class.java)) {
            return GastroMapViewModel(preferences) as T
        }

        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            return HomeScreenViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(FavoriteScreenViewModel::class.java)) {
            return FavoriteScreenViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(DetailScreenViewModel::class.java)) {
            return DetailScreenViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(AboutScreenViewModel::class.java)) {
            return AboutScreenViewModel(githubRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
