package com.example.secureauthapp.data.remote

import com.example.secureauthapp.data.model.UserInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface IdentityHubApiService {
    
    @GET("/connect/userinfo")
    suspend fun getUserInfo(
        @Header("Authorization") authorization: String
    ): Response<UserInfo>
}
