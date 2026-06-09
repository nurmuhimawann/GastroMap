package com.nurmuhimawann.gastromap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurmuhimawann.gastromap.data.local.dataStore.SettingPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.update

data class AppUiState(
    val isDarkMode: Boolean = false
)

class GastroMapViewModel(
    private val preferences: SettingPreferences
): ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> get() = _uiState

    init {
        getDarkMode()
    }

    private fun getDarkMode() {
        viewModelScope.launch {
            preferences.getThemeSetting().collect { isDarkMode ->
                _uiState.update { it.copy(isDarkMode = isDarkMode) }
            }
        }
    }

    fun setDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferences.saveThemeSetting(isDarkMode)
        }
    }
}
