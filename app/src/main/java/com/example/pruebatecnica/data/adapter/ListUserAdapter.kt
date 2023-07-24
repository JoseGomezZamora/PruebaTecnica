package com.example.pruebatecnica.data.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebatecnica.R
import com.example.pruebatecnica.data.model.UserList2Model
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import java.util.stream.Collectors

class ListUserAdapter (private val userList: ArrayList<UserList2Model>):
    RecyclerView.Adapter<ListUserAdapter.vh>(){

    private lateinit var cont: Context
    private var id: Int = 0
    private var dataAux: ArrayList<UserList2Model> = ArrayList(userList)//var aux

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vh {
        cont = parent.context//contexto de mi actividad donde esta el contenedor fragment
        val layoutInflater = LayoutInflater.from(parent.context)
        return ListUserAdapter.vh(layoutInflater.inflate(R.layout.item_user_list, parent, false))
    }

    override fun onBindViewHolder(holder: vh, position: Int) {

        id = userList[position].id
        // Verificar si el objeto Datos no es nulo antes de acceder a sus propiedades
        if (userList[position].datos != null) {
            val imagen = userList[position].datos.imagen
            showImageFromBase64(imagen, holder.imageView)
        }else{
            holder.imageView.setBackgroundResource(R.drawable.ic_launcher_background)
        }
        holder.tvNameUser.text = userList[position].nombre
        holder.tvEmailItem.text = userList[position].email
        holder.itemView.setOnClickListener {
            //nombre: String, email: String, edad: Int, fechaNac: String
            showCustomAlertDialog(userList[position].nombre, userList[position].email, userList[position].edad, userList[position].fechaNac)
        }

    }

    override fun getItemCount(): Int = userList.size

    class vh(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cvContetnItemList = itemView.findViewById<CardView>(R.id.cvContetnItemList)
        var imageView = itemView.findViewById<ImageView>(R.id.imageViewPerfilItem)
        var tvNameUser = itemView.findViewById<TextView>(R.id.tvNameUserItem)
        var tvEmailItem = itemView.findViewById<TextView>(R.id.tvEmailItem)
    }

    private fun showImageFromBase64(base64String: String, imageView: ImageView) {
        try {
            // Decodificar la cadena Base64 a un array de bytes
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

            // Convertir los bytes a un objeto Bitmap
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            // Mostrar el Bitmap en el ImageView
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar cualquier error que pueda ocurrir durante el proceso
        }
    }

    fun filter(query:String) {

        val longitud = query.length
        if (longitud == 0) {
            userList.clear()
            userList.addAll(dataAux!!)
        } else {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                userList.stream()
                    .filter { i -> i.nombre.lowercase().contains(query.lowercase()) }
                    .collect(Collectors.toList())
            } else {
                val filteredList = java.util.ArrayList<UserList2Model>()
                for (c in userList) {
                    if (c.nombre.lowercase().contains(query.lowercase())) {
                        filteredList.add(c)
                    }
                }
                filteredList
            }
            userList.clear()
            userList.addAll(collection)
        }
        notifyDataSetChanged()
    }

    fun showCustomAlertDialog( nombre: String, email: String, edad: Int, fechaNac: String) {
        val view: View = LayoutInflater.from(cont).inflate(R.layout.alert_inf, null)

        // Obtener las referencias a los TextViews dentro del diseño personalizado
        val tvNombreA = view.findViewById<TextView>(R.id.tvNombreA)
        val tvEmailA = view.findViewById<TextView>(R.id.tvEmailA)
        val tvEdadA = view.findViewById<TextView>(R.id.tvEdadA)
        val tvFechaNacA = view.findViewById<TextView>(R.id.tvFechaNacA)

        // Establecer los datos en los TextViews
        tvNombreA.text = "Nombre: $nombre"
        tvEmailA.text = "Email: $email"
        tvEdadA.text = "Edad: $edad"
        tvFechaNacA.text = "Fecha de Nacimiento: $fechaNac"

        // Construir el AlertDialog
        val alertDialogBuilder = AlertDialog.Builder( cont)
            .setTitle("Información")
            .setView(view)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        // Mostrar el AlertDialog
        alertDialogBuilder.create().show()
    }

}