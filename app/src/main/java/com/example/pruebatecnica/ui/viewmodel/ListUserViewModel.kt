package com.example.pruebatecnica.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebatecnica.data.model.UserList2Model
import com.example.pruebatecnica.data.model.UserListModel
import com.example.pruebatecnica.domain.ListUserUseCase
import com.example.pruebatecnica.domain.NewUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel  @Inject constructor(private val listUserUseCase: ListUserUseCase): ViewModel(){

    val listUser = MutableLiveData<List<UserList2Model>>()

    fun onCreate() {

        viewModelScope.launch {

            val result = listUserUseCase.getAllUser()

            if (!result.isNullOrEmpty()){
                listUser.postValue(result)
            }

        }

    }

}