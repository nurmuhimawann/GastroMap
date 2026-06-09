package com.nurmuhimawann.gastromap.data

import com.nurmuhimawann.gastromap.common.Constants
import com.nurmuhimawann.gastromap.data.remote.response.GithubDetailUser
import com.nurmuhimawann.gastromap.data.remote.retrofit.ApiGithubService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GithubRepository(
    private val apiGithubService: ApiGithubService
) {

    fun getDetailGithubUser(): Flow<GithubDetailUser> = flow {
        val response = apiGithubService.getDetailGithubUser(Constants.githubUsername)
        emit(response)
    }
}
