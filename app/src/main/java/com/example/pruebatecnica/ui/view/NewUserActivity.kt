package com.example.pruebatecnica.ui.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.pruebatecnica.R
import com.example.pruebatecnica.databinding.ActivityNewUserBinding
import com.example.pruebatecnica.ui.viewmodel.NewUserViewModel
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import android.util.Base64
import com.example.pruebatecnica.data.model.Datos
import com.example.pruebatecnica.data.model.UserListModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class NewUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewUserBinding
    private val selectedDate = Calendar.getInstance()

    val REQUEST_STORAGE_PERMISSION = 101
    val REQUEST_CAMERA_PERMISSION = 102

    private val newUserViewModel: NewUserViewModel by viewModels()

    private var takePictureActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private var imageUri1: Uri? = null
    private lateinit var imageUri: Uri
    private var base64String: String = "";

    //CONF DE LA LIBRERIA EL TAMAÑO DEL CORTE Y LO QUE HACE SI RECIBE IMAGEN O SI OPTIENE DE GALERIA
    private var cropActivityResultContract = object : ActivityResultContract<Uri?, Uri?>(){
        override fun createIntent(context: Context, input: Uri?): Intent {
            return CropImage.activity(input).setAspectRatio(300, 300).getIntent(this@NewUserActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Uri?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions(this)

        setupActivityResultListener()
        binding.ivProfileImage.setOnClickListener{

            openCameraAndGetPhoto()

        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { result ->
            result?.let {
                binding.ivProfileImage.setImageURI(it)
                saveImageToGallery(it)
            }
        }

        binding.ivReturnListNewUserActivity.setOnClickListener{
            onBackPressed()
            finish()
        }

        binding.tieDateNewUserActivity.setOnClickListener {

            showDatePicker()

        }

        binding.btnSave.setOnClickListener {

            if (validateForm()){
                //manda informacion
                newUserViewModel.onCreate(UserListModel(0,binding.tieNameNewUserActivity.text.toString(), binding.tieApNewUserActivity.text.toString(), binding.tieAmNewUserActivity.text.toString(),
                    calculateAge(binding.tieDateNewUserActivity.text.toString()), binding.tieEmailNewUserActivity.text.toString(), binding.tieDateNewUserActivity.text.toString(),
                    listOf(Datos(binding.tieAddresNewUserActivity.text.toString(), binding.tieNumberNewUserActivity.text.toString(), binding.tieColoniaNewUserActivity.text.toString(), binding.tieMunicipioNewUserActivity.text.toString(),
                        binding.tieEstadoNewUserActivity.text.toString(), binding.tieCpNewUserActivity.text.toString(), base64String )
                    ))
                )
                if (newUserViewModel.userNew?.nombre?.isNotEmpty() == true){
                    showAlertDialog("Exito!!","La informacion de guardo con exito!!")
                }else{
                    showAlertDialog("Ups!!","Intente mas tarde...")
                }
            }

        }

    }

    fun parseStringToDate(): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.parse(binding.tieDateNewUserActivity.text.toString())
    }

    private fun showDatePicker() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, yearSelected, monthOfYear, dayOfMonthSelected ->
            selectedDate.set(Calendar.YEAR, yearSelected)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonthSelected)

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(selectedDate.time)

            binding.tieDateNewUserActivity.setText(formattedDate)
        }, year, month, dayOfMonth)

        datePickerDialog.show()
    }

    private fun checkAndRequestPermissions(activity: Activity) {
        val storagePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)

        val permissionsToRequest = ArrayList<String>()

        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), REQUEST_STORAGE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de almacenamiento otorgado
                } else {
                    // Permiso de almacenamiento denegado
                    showAlertDialog(this, "Permiso Denegado", "Denegaste el permiso de Almacenamiento")
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso de cámara otorgado
                } else {
                    // Permiso de cámara denegado
                    showAlertDialog(this, "Permiso Denegado", "Denegaste el permiso de Escritura")
                }
            }
        }
    }

    fun showAlertDialog(context: Context, title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        // Configurar el título y mensaje del AlertDialog
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)

        // Configurar el botón "Aceptar"
        alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
            // Aquí puedes agregar acciones que se ejecutarán al hacer clic en el botón "Aceptar"
            dialog.dismiss() // Cierra el diálogo cuando se hace clic en "Aceptar"
        }

        // Mostrar el AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun validateForm (): Boolean {

        val regex = "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"

        if(binding.tieNameNewUserActivity.text?.isEmpty() == true){

            binding.tieNameNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieNameNewUserActivity.text?.length!! < 3 ){

            binding.tieNameNewUserActivity.error = getString(R.string.minCharacter_NewUserActivity)
            return false

        }else if (binding.tieApNewUserActivity.text?.isEmpty() == true){

            binding.tieApNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieApNewUserActivity.text?.length!! < 3){

            binding.tieApNewUserActivity.error = getString(R.string.minCharacter_NewUserActivity)
            return false

        }else if (binding.tieAmNewUserActivity.text?.isEmpty() == true){

            binding.tieAmNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieAmNewUserActivity.text?.length!! < 3){

            binding.tieAmNewUserActivity.error = getString(R.string.minCharacter_NewUserActivity)
            return false

        }else if (binding.tieEmailNewUserActivity.text?.isEmpty() == true){

            binding.tieEmailNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieEmailNewUserActivity.text?.matches(Regex(regex)) == false){

            binding.tieEmailNewUserActivity.error = getString(R.string.emailValid_NewUserActivity)
            return false

        }else if (binding.tieDateNewUserActivity.text?.isEmpty() == true){

            binding.tieDateNewUserActivity.error = getString(R.string.dateValid_NewUserActivity)
            return false

        }else if (binding.tieAddresNewUserActivity.text?.isEmpty() == true){

            binding.tieAddresNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieAddresNewUserActivity.text?.length!! < 3){

            binding.tieAddresNewUserActivity.error = getString(R.string.minCharacter_NewUserActivity)
            return false

        }else if(binding.tieNumberNewUserActivity.text?.isEmpty() == true){

            binding.tieNumberNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if(binding.tieColoniaNewUserActivity.text?.isEmpty() == true){

            binding.tieColoniaNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieColoniaNewUserActivity.text?.length!! < 3){

            binding.tieColoniaNewUserActivity.error = getString(R.string.minCharacter_NewUserActivity)
            return false

        }else if(binding.tieMunicipioNewUserActivity.text?.isEmpty() == true){

            binding.tieMunicipioNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieMunicipioNewUserActivity.text?.length!! < 3){

            binding.tieMunicipioNewUserActivity.error = getString(R.string.minCharacter_NewUserActivity)
            return false

        }else if(binding.tieEstadoNewUserActivity.text?.isEmpty() == true){

            binding.tieEstadoNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieEstadoNewUserActivity.text?.length!! < 3) {

            binding.tieEstadoNewUserActivity.error =
                getString(R.string.minCharacter_NewUserActivity)
            return false
        }else if(binding.tieCpNewUserActivity.text?.isEmpty() == true){

            binding.tieCpNewUserActivity.error = getString(R.string.vacio_NewUserActivity)
            return false

        }else if (binding.tieCpNewUserActivity.text?.length!! < 3){

            binding.tieCpNewUserActivity.error = getString(R.string.minCharacter_NewUserActivity)
            return false

        }else if (base64String.isEmpty() || base64String.equals("") || base64String == null){

            binding.tvImageValid.visibility = View.VISIBLE
            return false

        }


        binding.tieNameNewUserActivity.error = null
        binding.tieApNewUserActivity.error = null
        binding.tieAmNewUserActivity.error = null
        binding.tieEmailNewUserActivity.error = null
        binding.tieDateNewUserActivity.error = null
        binding.tieAddresNewUserActivity.error = null
        binding.tieNumberNewUserActivity.error = null
        binding.tieColoniaNewUserActivity.error = null
        binding.tieMunicipioNewUserActivity.error = null
        binding.tieEstadoNewUserActivity.error = null
        binding.tieCpNewUserActivity.error = null
        binding.tvImageValid.visibility = View.GONE

        return true

    }

    /*TOMO LA FOTO, LA GUARDO Y LA PASO A MI variable takePictureActivityResultLauncher DE FORMA TEMPORAL
    * PARA PASARLA POR INTENT A MI ACTIVITY*/
    fun openCameraAndGetPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {//CREA LA IMAGEN TEMPORAL
            createImageFile()
        } catch (ex: IOException) {
            // Manejo de errores si no se puede crear el archivo
            null
        }
        photoFile?.also {
            imageUri = FileProvider.getUriForFile(
                this,
                "com.example.pruebatecnica.fileprovider",
                it
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            takePictureActivityResultLauncher?.launch(takePictureIntent)
        }
    }

    /*CREA EL ARCHIVO TEMPORAL*/
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "PNG_${timeStamp}_",
            ".png",
            storageDir
        )
    }

    /*ES UN ESCUCHADOR AQUI RECIBO LA IMAGEN EN MI ACTIVIDAD*/
    private fun setupActivityResultListener() {
        takePictureActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Ahora tienes la imagen capturada en la URI imageUri
                // Puedes guardarla o mostrarla en el ImageView
                cropActivityResultLauncher.launch(imageUri)
            }
        }
    }

    /*GUARDO LA IMAGEN DENTRO DE LA MEMORIA DEL TELEFONO EN PICTURES*/
    private fun saveImageToGallery(uri: Uri?) {
        if (uri == null) return

        // Obtener el Bitmap a partir de la URI
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        base64String = bitmapToBase64(bitmap)

        val filename = "${System.currentTimeMillis()}.png"
        var outputStream: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "pruebaandroid")
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = imageUri?.let { resolver.openOutputStream(it) }
            imageUri1 = imageUri
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + File.separator + "pruebaandroid")
            imagesDir.mkdirs() // Create the directory if it doesn't exist
            val image = File(imagesDir, filename)
            outputStream = FileOutputStream(image)
            imageUri1 = image.absolutePath.toUri()
        }

        outputStream?.use {
            // Comprime el bitmap en formato JPEG y escribe en el OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
            println("RUTA "+ imageUri1!!.path)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String {
        var realPath: String = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(uri, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    realPath = cursor.getString(columnIndex)
                }
            } else {
                val scheme = uri.scheme
                if (scheme == "content") {
                    cursor = context.contentResolver.query(uri, projection, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        realPath = cursor.getString(columnIndex)
                    }
                } else if (scheme == "file") {
                    realPath = uri.path!!
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return realPath
    }

    private fun calculateAge(dateOfBirth: String): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = dateFormat.parse(dateOfBirth)

        if (birthDate == null) {
            // Manejar el caso en el que la fecha de nacimiento no sea válida
            return -1
        }

        val cal = Calendar.getInstance()
        val today = cal.time

        val diff = today.time - birthDate.time
        cal.timeInMillis = diff

        val age = cal.get(Calendar.YEAR) - 1970

        return age
    }

    private fun showAlertDialog( title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        // Configurar el título y el mensaje del AlertDialog
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)

        // Agregar un botón "Aceptar" al AlertDialog
        alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
            // Acciones que se realizarán cuando el usuario haga clic en el botón "Aceptar"
            dialog.dismiss() // Cerrar el AlertDialog
        }

        // Mostrar el AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


}