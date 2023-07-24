package com.example.pruebatecnica.data.network

import com.example.pruebatecnica.data.model.UserList2Model
import com.example.pruebatecnica.data.model.UserListModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserLisApiClient {

    @GET("sec_dev_interview/")
    suspend fun getAllList(): Response<List<UserList2Model>>

    @POST("sec_dev_interview/")
    suspend fun createUser(@Body userData: UserListModel): Response<UserListModel>

}