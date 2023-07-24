package com.example.pruebatecnica.domain

import com.example.pruebatecnica.data.UserRepository
import com.example.pruebatecnica.data.model.UserList2Model
import com.example.pruebatecnica.data.model.UserListModel
import javax.inject.Inject

class ListUserUseCase @Inject constructor(private val repository: UserRepository){

    suspend fun getAllUser(): List<UserList2Model>{

        val users = repository.getAllUserFromApi()

        return users

    }


}