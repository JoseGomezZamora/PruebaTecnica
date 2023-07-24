package com.example.pruebatecnica.data

import com.example.pruebatecnica.data.model.UserList2Model
import com.example.pruebatecnica.data.model.UserListModel
import com.example.pruebatecnica.data.network.UserService
import javax.inject.Inject

class UserRepository @Inject constructor( private val api: UserService) {

    suspend fun getAllUserFromApi(): List<UserList2Model>{

        val response: List<UserList2Model> = api.getListUser()
        println("QQQQQ"+response[0].nombre)
        println("QQQQQ"+response[1].nombre)
        return response

    }

    suspend fun postUserApi(user: UserListModel): UserListModel? {

        val response: UserListModel? = api.postUser(user)

        return response

    }

}