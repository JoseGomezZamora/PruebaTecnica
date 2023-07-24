package com.example.pruebatecnica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebatecnica.data.model.Datos
import com.example.pruebatecnica.data.model.UserListModel
import com.example.pruebatecnica.domain.NewUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewUserViewModel @Inject constructor(private val newUserUseCase: NewUserUseCase): ViewModel() {

    var userNew: UserListModel? = null

    fun onCreate(user: UserListModel) {

        viewModelScope.launch {

            print("Hola mundo corrotines"+ user)
            val result = newUserUseCase.postNewUser(user)

            if (!result?.nombre.equals("") || result?.nombre == null){
                if (result != null) {
                    userNew = UserListModel(result.id,result.nombre, result.apellidoPaterno, result.apellidoMaterno, result.edad, result.email, result.fechaNac,
                        listOf(Datos(result.datos[0].calle, result.datos[0].numero, result.datos[0].colonia, result.datos[0].delegacion, result.datos[0].estado, result.datos[0].cp, result.datos[0].imagen))
                    )
                    // Datos(result.datos.calle, result.datos.numero, result.datos.colonia, result.datos.delegacion, result.datos.estado, result.datos.cp, result.datos.imagen)
                    //listOf(Datos(result.datos[0].calle, result.datos[0].numero, result.datos[0].colonia, result.datos[0].delegacion, result.datos[0].estado, result.datos[0].cp, result.datos[0].imagen))
                }
            }

        }

    }


}