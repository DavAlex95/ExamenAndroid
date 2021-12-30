package com.dafs.examenandroid

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Location

import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore

import android.util.Log
import android.view.Menu
import android.view.MenuItem

import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dafs.examenandroid.databinding.ActivityMainBinding
import com.google.android.gms.location.*

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


import java.io.ByteArrayOutputStream


import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    private val CHANEL_ID = "my_channel_id"
    private val notificationId = 202

    private lateinit var database: DatabaseReference

    private val CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO = 2106

    private val LOG_TAG = "EnviarUbicacion"
    private var imageIdentifier: String? = null
    private var grantedPermissions = false
    private var bitmap: Bitmap? = null
    private var itemImg: ImageView? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        checkPermisses()

        CreateNotificationChannel()

    }

    fun saveLocation(ubicacion: Location) {

        //val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        //val currentDate = sdf.format(Date())

        val date = getCurrentDateTime()
        val dateInString = date.toString("dd/MM/yyyy HH:mm:ss")

        val db = Firebase.firestore

        val location = hashMapOf(
            "fecha" to dateInString,
            "latitud" to ubicacion.latitude,
            "longitud" to ubicacion.longitude
        )

        db.collection("location")
            .add(location)
            .addOnSuccessListener { documentReference ->
                Log.d(LOG_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(LOG_TAG, "Error adding document", e)
            }

        Log.d(LOG_TAG, "Latitud es ${ubicacion.latitude} y la longitud es ${ubicacion.longitude}")
    }

    fun onPermissionGranted() {
        // Hasta aquí sabemos que los permisos ya están concedidos
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    Log.d(LOG_TAG, "Se Inicializa la base de datos Firebase")
                } else {
                    Log.d(LOG_TAG, "No se pudo obtener la ubicación")
                }
            }

            val locationRequest = LocationRequest.create().apply {
                interval = 300000
                fastestInterval = 300000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {

                    Log.d(LOG_TAG, "Se recibió una actualización")
                    for (location in p0.locations) {
                        saveLocation(location)
                        sendNotification()
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.d(LOG_TAG, "Tal vez no solicitaste permiso antes")
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO) {
            val todosLosPermisosConcedidos =
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (grantResults.isNotEmpty() && todosLosPermisosConcedidos) {
                grantedPermissions = true
                onPermissionGranted()
                Log.d(LOG_TAG, "El usuario concedió todos los permisos")
            } else {
                Log.d(LOG_TAG, "Uno o más permisos fueron denegados")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermisses() {
        val permissions = arrayListOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
        // Segundo plano para Android Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        val permissionsLikeArray = permissions.toTypedArray()
        if (hasPermissions(permissionsLikeArray)) {
            grantedPermissions = true
            onPermissionGranted()
            Log.d(LOG_TAG, "Los permisos ya fueron concedidos")
        } else {
            requestPermissions(permissionsLikeArray)
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions(permisos: Array<String>) {
        Log.d(LOG_TAG, "Solicitando permisos...")
        requestPermissions(
            permisos,
            CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO
        )
    }

    private fun hasPermissions(permisos: Array<String>): Boolean {
        return permisos.all {
            return ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val txtDescription = "Notification Description"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT

            val channel: NotificationChannel =
                NotificationChannel(CHANEL_ID, name, importance).apply {
                    description = txtDescription
                }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {

        val bitmap: Bitmap = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.common_full_open_on_phone
        )
        val bitmapLarge: Bitmap = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.common_full_open_on_phone
        )

        val builder = NotificationCompat.Builder(this, CHANEL_ID)
            .setSmallIcon(R.drawable.common_full_open_on_phone)
            .setContentTitle("Nueva Actualizacion")
            .setContentText("Localidad obtenida").setLargeIcon(bitmapLarge)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.imgAddPicture) {
                captureImage()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun captureImage() {
        val alertTitle = getString(R.string.charge_img)
        val positiveButtonTittle = getString(R.string.create_button_tittle)
        val negativeButtonTittle = getString(R.string.close_button_tittle)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )


        itemImg = ImageView(this)
        val btnUploadImgFromCamera = Button(this)
        val btnUploadImgFromGallery = Button(this)


        itemImg!!.adjustViewBounds = true
        itemImg!!.scaleType = ImageView.ScaleType.CENTER_CROP
        btnUploadImgFromGallery.text = "Buscar Imagen en Galeria"
        btnUploadImgFromCamera.text = "Tomar Foto"


        layout.addView(itemImg)
        layout.addView(btnUploadImgFromGallery)
        layout.addView(btnUploadImgFromCamera)

        btnUploadImgFromGallery.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    val permisos = arrayListOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    val permissionsLikeArray = permisos.toTypedArray()
                    requestPermissions(permissionsLikeArray)
                } else {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    launchGalleryActivity.launch(intent)

                }
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                launchGalleryActivity.launch(intent)

            }


        }

        btnUploadImgFromCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    val permisos = arrayListOf(
                        android.Manifest.permission.CAMERA
                    )
                    val permissionsLikeArray = permisos.toTypedArray()
                    requestPermissions(permissionsLikeArray)

                } else {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    launchCameraActivity.launch(cameraIntent)
;
                }
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                launchCameraActivity.launch(cameraIntent)
            }

        }

        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(alertTitle)
        alertBuilder.setView(layout)
        alertBuilder.setNegativeButton(
            negativeButtonTittle
        ) { dialogInterface, i -> dialogInterface.dismiss() }
        alertBuilder.setPositiveButton(
            positiveButtonTittle
        ) { dialog, which ->
            if (bitmap != null) {
                val baos = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()
                saveImgInFirebase(data)

            }
        }
        alertBuilder.setCancelable(false)
        alertBuilder.create().show()


    }

    //Itent para seleccinar Galerias
    private var launchGalleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val mUri = data!!.data
                try {
                     if (Build.VERSION.SDK_INT >= 29) {

                         // To handle deprication use
                         val source = ImageDecoder.createSource(this.contentResolver, mUri!!)
                         bitmap = ImageDecoder.decodeBitmap(source)
                    } else {
                        // Use older version
                         bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver, mUri)
                    }
                } catch (e: Exception) {
                }
                itemImg!!.setImageBitmap(bitmap)
            }
        }
    //Itent para Tomar una Foto
    private var launchCameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                bitmap = data!!.extras!!["data"] as Bitmap?
                itemImg!!.setImageBitmap(bitmap)
            }
        }

    private fun saveImgInFirebase(data:ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {

            val storage=FirebaseStorage.getInstance().getReference("images/")
            val date = getCurrentDateTime()
            val dateInString = date.toString("dd/MM/yyyy HH:mm:ss")


            imageIdentifier = UUID.randomUUID().toString() + ".png"
            val uploadTask =
                FirebaseStorage.getInstance().reference.child("my_images").child(imageIdentifier!!)
                    .putBytes(data)
            uploadTask.addOnFailureListener { exception ->
                Toast.makeText(
                    baseContext,
                    exception.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }.addOnSuccessListener { taskSnapshot ->
                Toast.makeText(
                    baseContext,
                    "Uploading  Process is Successful",
                    Toast.LENGTH_LONG
                ).show()

            }

        }


    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }
}