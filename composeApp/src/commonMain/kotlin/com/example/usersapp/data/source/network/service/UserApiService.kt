package com.example.usersapp.data.source.network.service

import com.example.usersapp.data.source.network.response.UserDetailsResponse
import com.example.usersapp.data.source.network.response.UserResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface UserApiService {

    @GET("users")
    suspend fun getUsers(@Query("page") page: Int): UserResponse

    @GET("users/{id}")
    suspend fun getDetails(@Path("id") id: Int): UserDetailsResponse

}