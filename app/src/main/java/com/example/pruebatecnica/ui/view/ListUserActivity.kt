package com.example.pruebatecnica.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebatecnica.R
import com.example.pruebatecnica.data.adapter.ListUserAdapter
import com.example.pruebatecnica.data.model.UserList2Model
import com.example.pruebatecnica.databinding.ActivityListUserBinding
import com.example.pruebatecnica.databinding.ActivityNewUserBinding
import com.example.pruebatecnica.ui.viewmodel.ListUserViewModel
import com.example.pruebatecnica.ui.viewmodel.NewUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListUserBinding

    private val listUserViewModel: ListUserViewModel by viewModels()

    private lateinit var listUserAdapter: ListUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listUserViewModel.onCreate()

        binding.svName.setOnQueryTextListener( object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                println(newText)
                listUserAdapter.filter(newText)
                return true
            }

        })

        listUserViewModel.listUser.observe(this) {list ->

            //println(list.listIterator().next().nombre)
            rvCharacterList(list as ArrayList<UserList2Model>)

        }

        binding.fbNewUser.setOnClickListener{

            val intent = Intent(this@ListUserActivity, NewUserActivity::class.java)
            startActivity(intent)

        }

    }

    private fun rvCharacterList (lista: ArrayList<UserList2Model>) {

        listUserAdapter = ListUserAdapter(lista)
        binding.rvListUserActivity.adapter = listUserAdapter
        binding.rvListUserActivity.layoutManager = LinearLayoutManager(this)


    }

}