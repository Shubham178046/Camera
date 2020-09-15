package com.example.camera

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_capture_image.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
  var model = ArrayList<Model>()
    lateinit var cameraAdapter: CameraAdapter
    var positions : Int=-1
    private val CAMERA_REQUEST = 1888
    private val RESULT_LOAD_IMAGE=2000
    private val imageView: ImageView? = null
    private val MY_CAMERA_PERMISSION_CODE = 100
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model.add(Model("https://shorturl.at/ixQU4","Shubham", " Developer"))
        model.add(Model("https://shorturl.at/fqNR5","Jagdish", " Singer"))
        model.add(Model("https://shorturl.at/bgvY6","Arbaaz", "Network Engineer"))
        model.add(Model("https://shorturl.at/agjH9","Shivam", "Speaker"))
        model.add(Model("https://shorturl.at/fMOY7","Dhairya", " BusinessMan"))
        model.add(Model("https://shorturl.at/xOS46","Purvang", "Police"))

        cameraAdapter = CameraAdapter(applicationContext,model,object : CameraAdapter.OnClick{
            override fun onClick(position: Int) {
                captureImage()
                positions=position
            }
        })

        cameraRecyclerview.layoutManager= LinearLayoutManager(this)
        cameraRecyclerview.adapter = cameraAdapter
    }
    fun captureImage()
    {
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    MY_CAMERA_PERMISSION_CODE
                )
            }

        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST)
        }

       /* val cameraIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(cameraIntent, RESULT_LOAD_IMAGE)*/
    }
    private fun selectImage(context: Context) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePicture =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, CAMERA_REQUEST)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, RESULT_LOAD_IMAGE)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode === MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CAMERA_REQUEST && resultCode === Activity.RESULT_OK) {
            val photo : Bitmap = data!!.getExtras()!!.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            val image: ByteArray = stream.toByteArray()
            photo.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val mat = Matrix()
            mat.postRotate("270".toInt().toFloat())
            val bMapRotate =
                Bitmap.createBitmap(photo, 0, 0, photo.width, photo.height, mat, true)
            cameraAdapter.updateList(convertImage2Base64(data),positions)
            cameraAdapter.notifyDataSetChanged()
        }
       /* else if(requestCode === RESULT_LOAD_IMAGE && resultCode === Activity.RESULT_OK)
        {
            val photo : Bitmap = data!!.getExtras()!!.get("data") as Bitmap
            val stream = ByteArrayOutputStream()
            val image: ByteArray = stream.toByteArray()
            photo.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val mat = Matrix()
            mat.postRotate("270".toInt().toFloat())
            val bMapRotate =
                Bitmap.createBitmap(photo, 0, 0, photo.width, photo.height, mat, true)
            cameraAdapter.updateList(convertImage2Base64(data),positions)
            cameraAdapter.notifyDataSetChanged()
        }*/
    }
    fun convertImage2Base64(data : Intent): String {
        val bitmap =   data!!.getExtras()!!.get("data") as Bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image: ByteArray = stream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(image, 0)
    }
}