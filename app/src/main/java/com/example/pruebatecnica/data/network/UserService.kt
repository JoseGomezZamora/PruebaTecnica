package com.example.pruebatecnica.data.network

import android.util.Log
import com.example.pruebatecnica.data.model.UserList2Model
import com.example.pruebatecnica.data.model.UserListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserService @Inject constructor(
    private val api: UserLisApiClient
){

    suspend fun getListUser(): List<UserList2Model> {
        return withContext(Dispatchers.IO) {
            val response = api.getAllList()
            println(response.body()?.get(0)?.nombre)
            response.body() ?: emptyList()
        }
    }

    suspend fun postUser(userListModel: UserListModel): UserListModel? {
        return withContext(Dispatchers.IO) {
            /*userListModel.datos.get(0).calle = ""
            userListModel.datos.get(0).cp = ""
            userListModel.datos.get(0).colonia = ""
            userListModel.datos.get(0).estado = ""
            userListModel.datos.get(0).delegacion = ""
            userListModel.datos.get(0).imagen = ""
            userListModel.datos.get(0).numero = ""*/
            val response = api.createUser(userListModel)
            if (response.isSuccessful) {
                val user = response.body()
                Log.d("UserService", "postUser successful: $user")
                user
            } else {
                Log.e("UserService", "postUser failed: ${response.code()}")
                null
            }
        }
    }



}