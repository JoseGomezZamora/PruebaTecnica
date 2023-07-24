package com.example.pruebatecnica.domain

import com.example.pruebatecnica.data.UserRepository
import com.example.pruebatecnica.data.model.UserListModel
import javax.inject.Inject

class NewUserUseCase  @Inject constructor(private val repository: UserRepository){

    suspend fun postNewUser(user: UserListModel): UserListModel? {

        val user = repository.postUserApi(user)

        return user

    }

}