package com.example.pruebatecnica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.pruebatecnica.ui.view.ListUserActivity
import com.example.pruebatecnica.ui.view.NewUserActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Crear un objeto Handler
        val handler = Handler()

        // Definir el tiempo de retraso en milisegundos (3 segundos = 3000 ms)
        val delayMillis = 1000L

        // Crear un Runnable que se ejecutará después del retraso
        val runnable = Runnable {
            // Crear el Intent que deseas ejecutar
            val intent = Intent(this@MainActivity, ListUserActivity::class.java)
            startActivity(intent)
            finish() // Opcional: finalizar la actividad actual si no deseas volver a ella
        }

        // Ejecutar el Runnable después del retraso especificado
        handler.postDelayed(runnable, delayMillis)

    }
}