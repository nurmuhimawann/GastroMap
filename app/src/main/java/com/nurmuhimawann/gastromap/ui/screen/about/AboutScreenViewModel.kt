package com.nurmuhimawann.gastromap.ui.screen.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nurmuhimawann.gastromap.data.GithubRepository
import com.nurmuhimawann.gastromap.data.Result
import com.nurmuhimawann.gastromap.data.remote.response.GithubDetailUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.update

data class AboutUiState(
    val result: Result<GithubDetailUser> = Result.Loading
)

class AboutScreenViewModel(
    private val repository: GithubRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> get() = _uiState

    init {
        getGithubUserDetail()
    }

    fun getGithubUserDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(result = Result.Loading) }
            repository.getDetailGithubUser()
                .catch { e ->
                    _uiState.update { it.copy(result = Result.Error(e.message.toString())) }
                }
                .collect { user ->
                    _uiState.update { it.copy(result = Result.Success(user)) }
                }
        }
    }
}
