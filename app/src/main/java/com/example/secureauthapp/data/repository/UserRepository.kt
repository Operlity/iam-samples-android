package com.example.secureauthapp.data.repository

import com.example.secureauthapp.data.model.Resource
import com.example.secureauthapp.data.model.UserInfo
import com.example.secureauthapp.data.remote.IdentityHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val apiService: IdentityHubApiService) {
    
    suspend fun getUserInfo(accessToken: String): Resource<UserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserInfo("Bearer $accessToken")
                
                if (response.isSuccessful) {
                    response.body()?.let {
                        return@withContext Resource.Success(it)
                    } ?: return@withContext Resource.Error("Empty response body")
                } else {
                    return@withContext Resource.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                return@withContext Resource.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
